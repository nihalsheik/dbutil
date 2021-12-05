package com.nihalsoft.java.dbutil;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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

import com.nihalsoft.java.dbutil.common.ColumnInfo;
import com.nihalsoft.java.dbutil.common.ColumnType;
import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.common.Table;
import com.nihalsoft.java.dbutil.result.BeanProcessorEx;
import com.nihalsoft.java.dbutil.result.handler.DataMapHandler;
import com.nihalsoft.java.dbutil.result.handler.DataMapListHandler;

public class DB extends QueryRunner {

    private BasicRowProcessor basicRowProcessor;

    public DB(DataSource ds) {
        super(ds);
        basicRowProcessor = new BasicRowProcessor(new BeanProcessorEx());
    }

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

    public List<Object[]> queryForList(String sql, Object... args) throws Exception {
        return this.query(sql, new ArrayListHandler(), args);
    }

    public Object[] queryForObject(String sql, Object... args) throws Exception {
        return this.query(sql, new ArrayHandler(), args);
    }

    public List<DataMap> queryForDataMapList(String sql, Object... args) throws Exception {
        return this.query(sql, new DataMapListHandler(), args);
    }

    public DataMap queryForDataMap(String sql, Object... args) throws Exception {
        return this.query(sql, new DataMapHandler(), args);
    }

    public <T> T queryForBean(String sql, Class<? extends T> type, Object... args) throws Exception {
        return this.query(sql, new BeanHandler<T>(type, basicRowProcessor), args);
    }

    public <T> List<T> queryForBeanList(String sql, Class<? extends T> type, Object... args) throws Exception {
        return this.query(sql, new BeanListHandler<T>(type, basicRowProcessor), args);
    }

    /**
     * 
     * @param <T>
     * @param object
     * @return
     * @throws Exception
     */
    public <T> T insert(Object object) throws Exception {

        List<ColumnInfo> oiList = getObjectInfo(object);

        String col = "";
        List<Object> values = new ArrayList<Object>();

        for (ColumnInfo ci : oiList) {
            if (ci.getType() != ColumnType.ID) {
                col += "," + ci.getName() + "=?";
                values.add(ci.getValue());
            }
        }

        Table tbl = object.getClass().getAnnotation(Table.class);
        String sql = "INSERT INTO " + tbl.name() + " SET " + col.substring(1);
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
        List<ColumnInfo> oiList = getObjectInfo(object);

        List<String> cols = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();

        String idName = "";
        Object idValue = null;

        for (ColumnInfo ci : oiList) {
            if (ci.getType() == ColumnType.ID) {
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

        Table tbl = object.getClass().getAnnotation(Table.class);
        String sql = "UPDATE " + tbl.name() + " SET " + String.join(",", cols) + " WHERE " + idName + "=?";
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
        List<ColumnInfo> oiList = getObjectInfo(object);
        String idName = "";
        Object idValue = null;

        for (ColumnInfo ci : oiList) {
            if (ci.getType() == ColumnType.ID) {
                idName = ci.getName();
                idValue = ci.getValue();
                break;
            }
        }

        if (idValue != null) {
            Table tbl = object.getClass().getAnnotation(Table.class);
            String sql = "DELETE FROM " + tbl.name() + " WHERE " + idName + "=?";
            System.out.println(sql);
            return this.update(sql, idValue);
        }
        return -1;
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
     * @param where
     * @param params
     * @return
     * @throws Exception
     */
    public int update(Map<String, Object> dataMap, String tableName, String where, Object... params) throws Exception {

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

        String sql = "UPDATE " + tableName + " SET " + col.substring(1) + " WHERE " + where;
        System.out.println(sql);
        return this.update(sql, values);
    }

    /**
     * 
     * @param object
     * @return
     * @throws Exception
     */
    private List<ColumnInfo> getObjectInfo(Object object) throws Exception {

        List<ColumnInfo> oiList = new ArrayList<ColumnInfo>();

        for (PropertyDescriptor property : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
            if (!property.getName().equals("class")) {
                oiList.add(new ColumnInfo(property, object));
            }
        }

        return oiList;
    }

    public static void main(String[] args) throws Exception {
    }
}
