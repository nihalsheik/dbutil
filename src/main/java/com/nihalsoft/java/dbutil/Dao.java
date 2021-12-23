package com.nihalsoft.java.dbutil;

import java.util.List;

public class Dao<T> {

    private DB db;
    private Class<T> clazz;

    public Dao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 
     * @param db
     * @param clazz
     */
    public Dao<T> setDB(DB db) {
        this.db = db;
        return this;
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public T findOne(Object id) throws Exception {
        return db.findOne(clazz, id);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public List<T> findAll() throws Exception {
        return db.findAll(clazz);
    }

    /**
     * 
     * @param criteria
     * @param args
     * @return
     * @throws Exception
     */
    public List<T> find(String criteria, Object... args) throws Exception {
        return db.find(clazz, criteria, args);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public <E> E insert(T entity) throws Exception {
        return db.insert(entity);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int update(T entity) throws Exception {
        return db.update(entity);
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
        return db.update(entity, creteria, args);
    }

    /**
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public int deleteById(Object id) throws Exception {
        return db.delete(clazz, id);
    }

    /**
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public int delete(Object entity) throws Exception {
        return db.delete(entity);
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public long getCount() throws Exception {
        return db.getCount(clazz);
    }

}
