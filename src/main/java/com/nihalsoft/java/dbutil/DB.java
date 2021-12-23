package com.nihalsoft.java.dbutil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.mchange.v2.c3p0.DataSources;
import com.nihalsoft.java.dbutil.common.ColumnInfo;
import com.nihalsoft.java.dbutil.common.ActiveConnection;
import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.common.EntityDescriptor;
import com.nihalsoft.java.dbutil.common.EntityUtil;
import com.nihalsoft.java.dbutil.common.Isolation;
import com.nihalsoft.java.dbutil.common.Propagation;
import com.nihalsoft.java.dbutil.common.Session;
import com.nihalsoft.java.dbutil.common.TransactionManager;
import com.nihalsoft.java.dbutil.common.TxConsumer;
import com.nihalsoft.java.dbutil.common.Util;
import com.nihalsoft.java.dbutil.result.BeanProcessorEx;
import com.nihalsoft.java.dbutil.result.handler.DataMapHandler;
import com.nihalsoft.java.dbutil.result.handler.DataMapListHandler;

public class DB {

    private static final Logger log = Logger.getLogger(DB.class.getName());

    private QueryRunner qr;
    private RowProcessor rowProcessor;
    private TransactionManager txManager;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public DB(String driverClass, String jdbcUrl, String user, String password) throws Exception {
        this(driverClass, jdbcUrl, user, password, null);
    }

    public DB(String driverClass, String jdbcUrl, String user, String password, Properties prop) throws Exception {

        Properties p = new Properties();
        p.put("user", user);
        p.put("password", password);
        p.put("driverClass", driverClass);

        DataSource uds = DataSources.unpooledDataSource(jdbcUrl, p);

        if (prop != null) {
            for (Object key : prop.keySet()) {
                prop.put(key, prop.get(key).toString());
            }
            prop.forEach((k, v) -> log.info(" --> " + k + " = " + v));
            _init(DataSources.pooledDataSource(uds, prop));
        } else {
            _init(DataSources.pooledDataSource(uds));
        }

    }

    public DB(DataSource ds) {
        this._init(ds);
    }

    private void _init(DataSource ds) {

        qr = new QueryRunner(ds);
        txManager = new TransactionManager(ds);
        rowProcessor = new BasicRowProcessor(new BeanProcessorEx());
    }

    /**
     * 
     * @param tableName
     * @return
     */
    public boolean hasTable(String tableName) {
        Connection con = null;
        ResultSet rs = null;
        try {
            con = qr.getDataSource().getConnection();
            DatabaseMetaData meta = con.getMetaData();
            rs = meta.getTables(null, null, tableName, new String[] { "TABLE" });
            boolean res = rs.next();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, null, rs);
        }
        return false;
    }

    public <T> T query(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
        ActiveConnection conn = txManager.getActiveConnection();
        return conn != null //
                ? qr.query(conn.getConnection(), sql, handler, params) //
                : qr.query(sql, handler, params);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<Object[]> queryForList(String sql, Object... args) throws Exception {
        return this.query(sql, new ArrayListHandler(), args);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public Object[] queryForObject(String sql, Object... args) throws Exception {
        return this.query(sql, new ArrayHandler(), args);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<DataMap> queryForDataMapList(String sql, Object... args) throws Exception {
        return this.query(sql, new DataMapListHandler(), args);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public DataMap queryForDataMap(String sql, Object... args) throws Exception {
        return this.query(sql, new DataMapHandler(), args);
    }

    /**
     * 
     * @param <T>
     * @param sql
     * @param clazz
     * @param args
     * @return
     * @throws Exception
     */
    public <T> T queryForBean(String sql, Class<? extends T> clazz, Object... args) throws SQLException {
        return this.query(sql, new BeanHandler<T>(clazz, rowProcessor), args);
    }

    /**
     * 
     * @param <T>
     * @param sql
     * @param type
     * @param args
     * @return
     * @throws Exception
     */
    public <T> List<T> queryForBeanList(String sql, Class<? extends T> type, Object... args) throws SQLException {
        return this.query(sql, new BeanListHandler<T>(type, rowProcessor), args);
    }

    /**
     * 
     * @param <T>
     * @param sql
     * @param type
     * @param args
     * @return
     * @throws SQLException
     */
    public <T> T queryForObject(String sql, Class<T> type, Object... args) throws SQLException {
        return this.query(sql, new ScalarHandler<T>(), args);
    }

    /**
     * 
     * @param tableName
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public int delete(String tableName, String criteria, Object... args) throws Exception {
        String sql = "DELETE FROM " + tableName + " WHERE " + criteria;
        log.info(sql);
        return this.update(sql, args);
    }

    /**
     * 
     * @param <T>
     * @param dataMap
     * @param tableName
     * @return
     * @throws Exception
     */
    public <T> T insert(String tableName, Map<String, Object> dataMap) throws Exception {

        Object[] values = new Object[dataMap.size()];

        String col = "";
        int i = 0;
        for (Entry<String, Object> entry : dataMap.entrySet()) {
            col += "," + entry.getKey() + "=?";
            values[i++] = entry.getValue();
        }

        String sql = "INSERT INTO " + tableName + " SET " + col.substring(1);
        log.info(sql);

        return this.insert(sql, values);
    }

    /**
     * 
     * @param <T>
     * @param sql
     * @param values
     * @return
     * @throws SQLException
     */
    public <T> T insert(String sql, Object... values) throws SQLException {
        ActiveConnection conn = txManager.getActiveConnection();
        return (conn != null) //
                ? qr.insert(conn.getConnection(), sql, new ScalarHandler<T>(), values) //
                : qr.insert(sql, new ScalarHandler<T>(), values);
    }

    /**
     * 
     * @param dataMap
     * @param tableName
     * @param criteria
     * @param params
     * @return
     * @throws Exception
     */
    public int update(String tableName, Map<String, Object> dataMap, String criteria, Object... params)
            throws Exception {

        Object[] values = new Object[dataMap.size() + params.length];

        String col = "";
        int i = 0;
        for (Entry<String, Object> entry : dataMap.entrySet()) {
            col += "," + entry.getKey() + "=?";
            values[i++] = entry.getValue();
        }

        for (Object obj : params) {
            values[i++] = obj;
        }

        String sql = "UPDATE " + tableName + " SET " + col.substring(1) + " WHERE " + criteria;
        log.info(sql);
        return this.update(sql, values);
    }

    /**
     * 
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public int update(String sql, Object... params) throws SQLException {
        ActiveConnection conn = txManager.getActiveConnection();
        return (conn != null) ? qr.update(conn.getConnection(), sql, params) : qr.update(sql, params);
    }

    /**
     * 
     * @param <T>
     * @param sql
     * @param rsh
     * @param params
     * @return
     * @throws SQLException
     */
    public <T> T insertBatch(String sql, ResultSetHandler<T> rsh, Object[][] params) throws SQLException {
        return qr.insertBatch(sql, rsh, params);
    }

    public void session(TxConsumer txc) throws Exception {
        new Session(txManager).begin(txc);
    }

    public Session session(Propagation propgation) throws Exception {
        return new Session(txManager).propagation(propgation);
    }

    public Session session(Propagation propgation, Isolation isolation) throws Exception {
        return new Session(txManager).propagation(propgation).isolation(isolation);
    }

    /**
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    public <T> Dao<T> dao(Class<T> clazz) {
        return new Dao<T>(clazz).setDB(this);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public <T> T getScalar(String sql, Object... args) throws Exception {
        log.info(sql);
        return this.query(sql, new ScalarHandler<T>(), args);
    }

    /**
     * 
     * @param <T>
     * @param clazz
     * @param id
     * @return
     * @throws Exception
     */
    public <T> T findOne(Class<T> clazz, Object id) throws Exception {
        EntityDescriptor ed = EntityUtil.getDescriptor(clazz);
        Util.throwSqlException(!ed.hasId(), "There is no id column");
        return this.queryForBean("SELECT * FROM " + ed.getTableName() + " WHERE " + ed.getIdColumn() + "=?", clazz, id);
    }

    /**
     * 
     * @param <T>
     * @param clazz
     * @return
     * @throws Exception
     */
    public <T> List<T> findAll(Class<T> clazz) throws Exception {
        return this.queryForBeanList("SELECT * FROM " + EntityUtil.getTableName(clazz), clazz);
    }

    /**
     * 
     * @param <T>
     * @param clazz
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public <T> List<T> find(Class<T> clazz, String criteria, Object... args) throws Exception {
        return this.queryForBeanList("SELECT * FROM " + EntityUtil.getTableName(clazz) + " WHERE " + criteria, clazz,
                args);
    }

    public <E> E insert(Object entity) throws Exception {
        log.info("Inserting entity ");
        EntityDescriptor ed = EntityUtil.getDescriptor(entity, colInfo -> colInfo.isInsertable() && colInfo.hasValue());
        return _insert(ed);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int update(Object entity) throws Exception {

        EntityDescriptor ed = EntityUtil.getDescriptor(entity, colInfo -> colInfo.isInsertable() && colInfo.hasValue());

        Util.throwIf(!ed.hasId(), "There is no id column");

        return _update(ed, ed.getIdColumn().getName() + "=?", ed.getIdColumn().getValue());
    }

    /**
     * 
     * @param entity
     * @param creteria
     * @param args
     * @return
     * @throws Exception
     */
    public int update(Object entity, String creteria, Object... args) throws Exception {
        EntityDescriptor ed = EntityUtil.getDescriptor(entity, colInfo -> colInfo.isInsertable() && colInfo.hasValue());
        return _update(ed, creteria, args);
    }

    /**
     * 
     * @param clazz
     * @param id
     * @return
     * @throws Exception
     */
    public int delete(Class<?> clazz, Object id) throws Exception {
        EntityDescriptor ed = EntityUtil.getDescriptor(clazz);
        Util.throwIf(!ed.hasId(), "There is no id column");
        String sql = "DELETE FROM " + ed.getTableName() + " WHERE " + ed.getIdColumn().getName() + "=?";
        log.info(sql);
        return this.update(sql, id);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int delete(Object entity) throws Exception {
        EntityDescriptor ed = EntityUtil.getDescriptor(entity, null);
        Util.throwIf(!ed.hasId(), "There is no id column");
        String sql = "DELETE FROM " + ed.getTableName() + " WHERE " + ed.getIdColumn().getName() + "=?";
        log.info(sql);
        return this.update(sql, ed.getIdColumn().getValue());
    }

    /**
     * 
     * @param entity
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public int delete(Object entity, String criteria, Object... args) throws Exception {
        EntityDescriptor ed = EntityUtil.getDescriptor(entity.getClass());
        String sql = "DELETE FROM " + ed.getTableName() + " WHERE " + criteria;
        log.info(sql);
        return this.update(sql, args);
    }

    /**
     * 
     * @param clazz
     * @return
     * @throws Exception
     */
    public long getCount(Class<?> clazz) throws Exception {
        EntityDescriptor ed = EntityUtil.getDescriptor(clazz);
        String idcol = ed.getIdColumn() == null ? "*" : ed.getIdColumn().getName();
        String sql = "SELECT count(" + idcol + ") as total FROM " + ed.getTableName();
        log.info(sql);
        return this.query(sql, new ScalarHandler<Long>());
    }

    /**
     * ------------------- PRIVATE METHODS -------------------
     */
    private int _update(EntityDescriptor ed, String criteria, Object... params) throws Exception {

        Object[] values = new Object[ed.getColumns().size() + params.length];

        String col = "";
        int i = 0;
        for (ColumnInfo ci : ed.getColumns()) {
            col += "," + ci.getName() + "=?";
            values[i++] = ci.getValue();
        }

        for (Object obj : params) {
            values[i++] = obj;
        }

        String sql = "UPDATE " + ed.getTableName() + " SET " + col.substring(1) + " WHERE " + criteria;
        log.info(sql);
        return this.update(sql, values);
    }

    private <E> E _insert(EntityDescriptor ed) throws Exception {

        Object[] values = new Object[ed.getColumns().size()];

        String col = "";
        int i = 0;
        for (ColumnInfo ci : ed.getColumns()) {
            col += "," + ci.getName() + "=?";
            values[i++] = ci.getValue();
        }

        String sql = "INSERT INTO " + ed.getTableName() + " SET " + col.substring(1);
        log.info(sql);
        return this.insert(sql, values);
    }
}
