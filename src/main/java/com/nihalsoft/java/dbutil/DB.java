package com.nihalsoft.java.dbutil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.result.BeanProcessorEx;
import com.nihalsoft.java.dbutil.result.handler.DataMapListHandler;

public class DB extends QueryRunner {

    private RowProcessor rowProcessor;

    public DB(DataSource ds) {
        super(ds);
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
            con = this.getDataSource().getConnection();
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
        return new DataMap(this.query(sql, new MapHandler(), args));
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
    public <T> List<T> queryForBeanList(String sql, Class<? extends T> type, Object... args) throws Exception {
        return this.query(sql, new BeanListHandler<T>(type, rowProcessor), args);
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
        System.out.println(sql);
        return this.insert(sql, new ScalarHandler<T>(), values);
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
        return this.update(sql, values);
    }

    public <T> Dao<T> dao(Class<T> clazz) {
        return new Dao<T>(this, clazz);
    }

    public RowProcessor getRowProcessor() {
        return rowProcessor;
    }

    public static void main(String[] args) throws Exception {
    }
}
