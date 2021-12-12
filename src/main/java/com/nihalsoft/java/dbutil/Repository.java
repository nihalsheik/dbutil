package com.nihalsoft.java.dbutil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.nihalsoft.java.dbutil.common.ColumnInfo;
import com.nihalsoft.java.dbutil.common.EntityDescriptor;
import com.nihalsoft.java.dbutil.common.EntityUtil;

public class Repository<T> {

    private DB db;
    private Class<T> clazz;

    public Repository() {
    }

    /**
     * 
     * @param db
     * @param clazz
     */
    public Repository(DB db, Class<T> clazz) {
        this.init(db, clazz);
    }

    /**
     * 
     * @param db
     * @param clazz
     */
    public void init(DB db, Class<T> clazz) {
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
        EntityDescriptor ed = EntityUtil.getEntityDescriptor(clazz);
        ColumnInfo idCol = ed.getIdColumn();
        if (idCol == null) {
            return null;
        }
        return db.queryForBean("SELECT * FROM " + ed.getTableName() + " WHERE " + idCol.getName() + "=?", clazz, id);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public List<T> findAll() throws Exception {
        String tn = EntityUtil.getTableName(clazz);
        if (tn.equals("")) {
            return null;
        }
        return db.queryForBeanList("SELECT * FROM " + tn, clazz);
    }

    /**
     * 
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public List<T> find(String criteria, Object... args) throws Exception {
        EntityDescriptor ed = EntityUtil.getEntityDescriptor(clazz);
        return db.queryForBeanList("SELECT * FROM " + ed.getTableName() + " WHERE " + criteria, clazz, args);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public Object insert(T entity) throws Exception {

        EntityDescriptor ed = EntityUtil.getEntityDescriptor(entity);

        String col = "";
        List<Object> values = new ArrayList<Object>();

        for (ColumnInfo ci : ed.getColumns()) {
            if (!ci.isIdColumn() && ci.isInsertable()) {
                col += "," + ci.getName() + "=?";
                values.add(ci.getValue());
            }
        }

        String sql = "INSERT INTO " + ed.getTableName() + " SET " + col.substring(1);
        System.out.println(sql);
        return db.insert(sql, new ScalarHandler<Object>(), values.toArray());
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int update(T entity) throws Exception {
        EntityDescriptor ed = EntityUtil.getEntityDescriptor(entity);

        List<String> cols = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();

        String idName = "";
        Object idValue = null;

        for (ColumnInfo ci : ed.getColumns()) {
            if (ci.isIdColumn()) {
                idName = ci.getName();
                idValue = ci.getValue();
            } else if (ci.getValue() != null && !ci.getValue().toString().isEmpty()) {
                cols.add(ci.getName() + "=?");
                values.add(ci.getValue());
            }
        }

        if (idName.equals("")) {
            return -1;
        }
        values.add(idValue);

        String sql = "UPDATE " + ed.getTableName() + " SET " + String.join(",", cols) + " WHERE " + idName + "=?";
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
        EntityDescriptor ed = EntityUtil.getEntityDescriptor(entity);
        String sql = "DELETE FROM " + ed.getTableName() + " WHERE " + ed.getIdColumn().getName() + "=?";
        System.out.println(sql);
        return db.update(sql, ed.getIdColumn().getValue());
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteById(Object id) throws Exception {
        EntityDescriptor ed = EntityUtil.getEntityDescriptor(clazz);
        String sql = "DELETE FROM " + ed.getTableName() + " WHERE " + ed.getIdColumn().getName() + "=?";
        System.out.println(sql);
        return db.update(sql, id);
    }

    public int getCount() throws Exception {
        EntityDescriptor ed = EntityUtil.getEntityDescriptor(clazz);
        String idcol = ed.getIdColumn() == null ? "*" : ed.getIdColumn().getName();
        String sql = "SELECT count(" + idcol + ") as total FROM " + ed.getTableName();
        System.out.println(sql);
        return db.query(sql, new ScalarHandler<Integer>());
    }

    public Object getScalar(String sql, Object... args) throws Exception {
        System.out.println(sql);
        return db.query(sql, new ScalarHandler<Object>(), args);
    }
}
