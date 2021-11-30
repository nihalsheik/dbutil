package com.nihalsoft.java.dbutil;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.nihalsoft.java.dbutil.common.Column;
import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.common.ObjectInfo;
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

    public List<Object[]> queryForObjectList(String sql, Object... args) throws Exception {
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

    public long insert(Object object) throws Exception {
        List<ObjectInfo> oiList = getObjectInfo(object);

        List<String> cols = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();

        oiList.stream().filter(oi -> !oi.getType().equalsIgnoreCase("id")).forEach(oi -> {
            cols.add(oi.getName() + "=?");
            values.add(oi.getValue());
        });

        Table tbl = object.getClass().getAnnotation(Table.class);
        String sql = "INSERT INTO " + tbl.name() + " SET " + String.join(",", cols);
        System.out.println(sql);
        return this.insert(sql, new ScalarHandler<Long>(), values);
    }

    public long update(Object object) throws Exception {
        List<ObjectInfo> oiList = getObjectInfo(object);

        List<String> cols = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();

        String idName = "";
        Object idValue = null;

        for (ObjectInfo oi : oiList) {
            if (oi.getType().equalsIgnoreCase("id")) {
                idName = oi.getName();
                idValue = oi.getValue();
            } else {
                cols.add(oi.getName() + "=?");
                values.add(oi.getValue());
            }
        }

        if (idName.equals("")) {
            return -1;
        }
        values.add(idValue);

        Table tbl = object.getClass().getAnnotation(Table.class);
        String sql = "UPDATE " + tbl.name() + " SET " + String.join(",", cols) + " WHERE " + idName + "=?";
        System.out.println(sql);
        return this.update(sql, new ScalarHandler<Long>(), values);
    }

    public int delete(Object object) throws Exception {
        List<ObjectInfo> oiList = getObjectInfo(object);
        String idName = "";
        Object idValue = null;

        for (ObjectInfo oi : oiList) {
            if (oi.getType().equalsIgnoreCase("id")) {
                idName = oi.getName();
                idValue = oi.getValue();
                break;
            }
        }

        if (idValue != null) {
            Table tbl = object.getClass().getAnnotation(Table.class);
            String sql = "DELETE " + tbl.name() + " WHERE " + idName + "=?";
            System.out.println(sql);
            return this.update(sql, new ScalarHandler<Long>(), idValue);
        }
        return -1;
    }

    private List<ObjectInfo> getObjectInfo(Object object) throws Exception {

        PropertyDescriptor[] ps = Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors();
        List<ObjectInfo> oiList = new ArrayList<ObjectInfo>();

        String type = "";
        for (PropertyDescriptor property : ps) {
            String colName = property.getName();
            if (colName.equals("class")) {
                continue;
            }

            Method getter = property.getReadMethod();
            Column e = getter.getAnnotation(Column.class);
            if (e != null) {
                if (!e.name().equals("")) {
                    colName = e.name();
                }
                type = e.type();
            }
            oiList.add(new ObjectInfo(colName, getter.invoke(object), type, getter));
        }

        return oiList;
    }

    public static void main(String[] args) throws Exception {
    }
}
