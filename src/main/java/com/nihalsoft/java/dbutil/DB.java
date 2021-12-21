package com.nihalsoft.java.dbutil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Consumer;

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
import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.common.EntityDescriptor;
import com.nihalsoft.java.dbutil.common.Transaction;
import com.nihalsoft.java.dbutil.common.TransactionManager;
import com.nihalsoft.java.dbutil.common.Util;
import com.nihalsoft.java.dbutil.result.BeanProcessorEx;
import com.nihalsoft.java.dbutil.result.handler.DataMapHandler;
import com.nihalsoft.java.dbutil.result.handler.DataMapListHandler;

public class DB {

    private QueryRunner qr;
    private RowProcessor rowProcessor;
    private TransactionManager tm;
    private EntityDescriptor entityDescriptor;

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
            prop.forEach((k, v) -> System.out.println(" --> " + k + " = " + v));
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
        tm = new TransactionManager(ds);
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

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<Object[]> queryForList(String sql, Object... args) throws Exception {
        return qr.query(sql, new ArrayListHandler(), args);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public Object[] queryForObject(String sql, Object... args) throws Exception {
        return qr.query(sql, new ArrayHandler(), args);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public List<DataMap> queryForDataMapList(String sql, Object... args) throws Exception {
        return qr.query(sql, new DataMapListHandler(), args);
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public DataMap queryForDataMap(String sql, Object... args) throws Exception {
        return qr.query(sql, new DataMapHandler(), args);
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
    public <T> T queryForBean(String sql, Class<? extends T> clazz, Object... args) throws Exception {
        return qr.query(sql, new BeanHandler<T>(clazz, rowProcessor), args);
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
    public <T> List<T> queryForBeanList(String sql, Class<? extends T> type, Object... args) throws Exception {
        return qr.query(sql, new BeanListHandler<T>(type, rowProcessor), args);
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
        return qr.query(sql, new ScalarHandler<T>(), args);
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
        System.out.println(sql);
        return _update(sql, args);
    }

    public <T> T findOne(Class<T> entity, Object id) throws Exception {
        Util.throwIf(!entityDescriptor.hasId(), "There is no id column");
        return this.queryForBean(
                "SELECT * FROM " + entityDescriptor.getTableName() + " WHERE " + entityDescriptor.getIdColumn() + "=?",
                entity, id);
    }

    public <T> List<T> findAll(Class<T> clazz) throws Exception {
        return this.queryForBeanList("SELECT * FROM " + entityDescriptor.getTableName(), clazz);
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
        System.out.println(sql);

        Connection conn = tm.getConnection();
        if (conn != null) {
            return qr.insert(conn, sql, new ScalarHandler<T>(), values);
        } else {
            return qr.insert(sql, new ScalarHandler<T>(), values);
        }
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
        System.out.println(sql);
        return _update(sql, values);
    }

    public int update(String sql, Object... params) throws SQLException {
        return _update(sql, params);
    }

    private int _update(String sql, Object... params) throws SQLException {
        Connection conn = tm.getConnection();
        if (conn != null) {
            return qr.update(conn, sql, params);
        } else {
            return qr.update(sql, params);
        }
    }

    public Transaction trans() throws Exception {
        return new Transaction(tm);
    }

    public void trans(Consumer<Transaction> cons) throws Exception {
        cons.accept(new Transaction(tm));
    }
    
    public <T> Repository<T> repository(Class<T> clazz) {
        return new Repository<T>(this, clazz);
    }

    public RowProcessor getRowProcessor() {
        return rowProcessor;
    }

    public static void main(String[] args) throws Exception {
    }

    public <T> int query(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
        return (int) qr.query(sql, handler, params);
    }
}
