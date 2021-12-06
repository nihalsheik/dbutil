package com.nihalsoft.java.dbutil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.nihalsoft.java.dbutil.common.BeanInfo;
import com.nihalsoft.java.dbutil.common.ColumnInfo;
import com.nihalsoft.java.dbutil.common.ColumnType;
import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.result.BeanProcessorEx;
import com.nihalsoft.java.dbutil.result.handler.DataMapHandler;
import com.nihalsoft.java.dbutil.result.handler.DataMapListHandler;

public class DB extends QueryRunner {

    private BasicRowProcessor basicRowProcessor;

    public DB(DataSource ds) {
        super(ds);
        basicRowProcessor = new BasicRowProcessor(new BeanProcessorEx());
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
    public <T> T queryForBean(String sql, Class<? extends T> clazz, Object... args) throws Exception {
        return this.query(sql, new BeanHandler<T>(clazz, basicRowProcessor), args);
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
    public <T> T queryForBean(Class<? extends T> clazz, String criteria, Object... args) throws Exception {
        BeanInfo bi = new BeanInfo(clazz);
        return this.queryForBean("SELECT * FROM " + bi.getTableName() + " WHERE " + criteria, clazz, args);
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
        return this.query(sql, new BeanListHandler<T>(type, basicRowProcessor), args);
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
    public <T> List<T> queryForBeanList(Class<? extends T> clazz, String criteria, Object... args) throws Exception {
        BeanInfo bi = new BeanInfo(clazz);
        return this.queryForBeanList("SELECT * FROM " + bi.getTableName() + " WHERE " + criteria, clazz, args);
    }

    /**
     * 
     * @param <T>
     * @param clazz
     * @param id
     * @return
     * @throws Exception
     */
    public <T> T load(Class<? extends T> clazz, Object id) throws Exception {
        BeanInfo bi = new BeanInfo(clazz);
        ColumnInfo idCol = bi.getIdColumn();
        if (idCol == null) {
            return null;
        }
        return this.query("SELECT * FROM " + bi.getTableName() + " WHERE " + idCol.getName() + "=?",
                new BeanHandler<T>(clazz, basicRowProcessor), id);
    }

    /**
     * 
     * @param <T>
     * @param object
     * @return
     * @throws Exception
     */
    public <T> T insert(Object object) throws Exception {

        BeanInfo bi = new BeanInfo(object);

        String col = "";
        List<Object> values = new ArrayList<Object>();

        for (ColumnInfo ci : bi.getColumns()) {
            if (!ci.isIdColumn()) {
                col += "," + ci.getName() + "=?";
                values.add(ci.getValue());
            }
        }

        String sql = "INSERT INTO " + bi.getTableName() + " SET " + col.substring(1);
        System.out.println(sql);
        return this.insert(sql, new ScalarHandler<T>(), values);
    }

    /**
     * 
     * @param object
     * @return
     * @throws Exception
     */
    public int update(Object object) throws Exception {
        BeanInfo bi = new BeanInfo(object);

        List<String> cols = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();

        String idName = "";
        Object idValue = null;

        for (ColumnInfo ci : bi.getColumns()) {
            if (ci.isIdColumn()) {
                idName = ci.getName();
                idValue = ci.getValue();
            } else {
                cols.add(ci.getName() + "=?");
                values.add(ci.getValue());
            }
        }

        if (idName.equals("")) {
            return -1;
        }
        values.add(idValue);

        String sql = "UPDATE " + bi.getTableName() + " SET " + String.join(",", cols) + " WHERE " + idName + "=?";
        System.out.println(sql);
        return this.update(sql, values.toArray());
    }

    /**
     * 
     * @param object
     * @return
     * @throws Exception
     */
    public int delete(Object object) throws Exception {
        BeanInfo bi = new BeanInfo(object);
        String idName = "";
        Object idValue = null;

        for (ColumnInfo ci : bi.getColumns()) {
            if (ci.getType() == ColumnType.ID) {
                idName = ci.getName();
                idValue = ci.getValue();
                break;
            }
        }

        if (idValue != null) {
            String sql = "DELETE FROM " + bi.getTableName() + " WHERE " + idName + "=?";
            System.out.println(sql);
            return this.update(sql, idValue);
        }
        return -1;
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
    public <T> T insert(Map<String, Object> dataMap, String tableName) throws Exception {

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
    public int update(Map<String, Object> dataMap, String tableName, String criteria, Object... params)
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

    public static void main(String[] args) throws Exception {
    }
}
