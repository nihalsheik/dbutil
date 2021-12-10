package com.nihalsoft.java.dbutil.test;

import com.nihalsoft.java.dbutil.DB;
import com.nihalsoft.java.dbutil.Dao;

public class PersonDao extends Dao<Person>{
    
    public PersonDao(DB db) {
        this.init(db, Person.class);
    }

}
