package com.nihalsoft.java.dbutil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.nihalsoft.java.dbutil.common.BeanInfo;
import com.nihalsoft.java.dbutil.common.ColumnInfo;

public class Dao<T> {

    private DB db;
    private Class<? extends T> clazz;

    public Dao() {

    }

    /**
     * 
     * @param db
     * @param clazz
     */
    public Dao(DB db, Class<? extends T> clazz) {
        this.init(db, clazz);
    }

    /**
     * 
     * @param db
     * @param clazz
     */
    public void init(DB db, Class<? extends T> clazz) {
        this.db = db;
        this.clazz = clazz;
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public T findOne(Object id) throws Exception {
        BeanInfo bi = new BeanInfo(clazz);
        ColumnInfo idCol = bi.getIdColumn();
        if (idCol == null) {
            return null;
        }
        return db.query("SELECT * FROM " + bi.getTableName() + " WHERE " + idCol.getName() + "=?",
                new BeanHandler<T>(clazz, db.getRowProcessor()), id);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public List<T> findAll() throws Exception {
        BeanInfo bi = new BeanInfo(clazz);
        return db.query("SELECT * FROM " + bi.getTableName(), new BeanListHandler<T>(clazz, db.getRowProcessor()));
    }

    /**
     * 
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public List<T> find(String criteria, Object... args) throws Exception {
        BeanInfo bi = new BeanInfo(clazz);
        return db.query("SELECT * FROM " + bi.getTableName() + " WHERE " + criteria,
                new BeanListHandler<T>(clazz, db.getRowProcessor()), args);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public Object insert(T entity) throws Exception {

        BeanInfo bi = new BeanInfo(entity);

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
        return db.insert(sql, new ScalarHandler<Object>(), values);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int update(T entity) throws Exception {
        BeanInfo bi = new BeanInfo(entity);

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
        return db.update(sql, values.toArray());
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int delete(T entity) throws Exception {
        BeanInfo bi = new BeanInfo(entity);
        String sql = "DELETE FROM " + bi.getTableName() + " WHERE " + bi.getIdColumn().getName() + "=?";
        System.out.println(sql);
        return db.update(sql, bi.getIdColumn().getValue());
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteById(Object id) throws Exception {
        BeanInfo bi = new BeanInfo(this.clazz);
        String sql = "DELETE FROM " + bi.getTableName() + " WHERE " + bi.getIdColumn().getName() + "=?";
        System.out.println(sql);
        return db.update(sql, id);
    }

}
