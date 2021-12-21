package com.nihalsoft.java.dbutil;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.nihalsoft.java.dbutil.common.DataMap;
import com.nihalsoft.java.dbutil.common.EntityDescriptor;
import com.nihalsoft.java.dbutil.common.Util;

public class Repository<T> {

    private Logger log = Logger.getLogger(Repository.class.getName());

    private DB db;
    private Class<T> clazz;
    private EntityDescriptor entityDescriptor;

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
        try {
            entityDescriptor = new EntityDescriptor(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public T findOne(Object id) throws Exception {
        Util.throwIf(!entityDescriptor.hasId(), "There is no id column");
        return db.queryForBean(
                "SELECT * FROM " + entityDescriptor.getTableName() + " WHERE " + entityDescriptor.getIdColumn() + "=?",
                clazz, id);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public List<T> findAll() throws Exception {
        return db.queryForBeanList("SELECT * FROM " + entityDescriptor.getTableName(), clazz);
    }

    /**
     * 
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public List<T> find(String criteria, Object... args) throws Exception {
        return db.queryForBeanList("SELECT * FROM " + entityDescriptor.getTableName() + " WHERE " + criteria, clazz,
                args);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public <E> E insert(T entity) throws Exception {

        DataMap dm = entityDescriptor.loadValues(entity,
                entry -> entry.getKey().isInsertable() && entry.getValue() != null);
        return db.insert(entityDescriptor.getTableName(), dm);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int update(T entity) throws Exception {

        Util.throwIf(!entityDescriptor.hasId(), "There is no id column");

        DataMap dm = entityDescriptor.loadValues(entity, entry -> entry.getValue() != null);

        Object idVal = dm.get(entityDescriptor.getIdColumn());

        dm.remove(entityDescriptor.getIdColumn());

        return db.update(entityDescriptor.getTableName(), dm, entityDescriptor.getIdColumn() + "=?", idVal);
    }

    /**
     * 
     * @param entity
     * @param creteria
     * @param args
     * @return
     * @throws Exception
     */
    public int update(T entity, String creteria, Object... args) throws Exception {
        Util.throwIf(!entityDescriptor.hasId(), "There is no id column");
        DataMap dm = entityDescriptor.loadValues(entity,
                entry -> !entry.getKey().isIdColumn() && entry.getValue() != null);
        return db.update(entityDescriptor.getTableName(), dm, creteria, args);
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteById(Object id) throws Exception {
        Util.throwIf(!entityDescriptor.hasId(), "There is no id column");
        String sql = "DELETE FROM " + entityDescriptor.getTableName() + " WHERE " + entityDescriptor.getIdColumn()
                + "=?";
        System.out.println(sql);
        return db.update(sql, id);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public int getCount() throws Exception {
        String idcol = entityDescriptor.getIdColumn() == null ? "*" : entityDescriptor.getIdColumn();
        String sql = "SELECT count(" + idcol + ") as total FROM " + entityDescriptor.getTableName();
        System.out.println(sql);
        return db.query(sql, new ScalarHandler<Integer>());
    }

    /**
     * 
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public Object getScalar(String sql, Object... args) throws Exception {
        System.out.println(sql);
        return db.query(sql, new ScalarHandler<Object>(), args);
    }
}
