package com.nihalsoft.java.dbutil.test;

import com.nihalsoft.java.dbutil.DB;
import com.nihalsoft.java.dbutil.Repository;

public class PersonRepos extends Repository<Person>{
    
    public PersonRepos(DB db) {
        this.init(db, Person.class);
    }

}
