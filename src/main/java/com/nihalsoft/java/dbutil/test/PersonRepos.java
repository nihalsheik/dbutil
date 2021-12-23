package com.nihalsoft.java.dbutil.test;

import com.nihalsoft.java.dbutil.Dao;

public class PersonRepos extends Dao<Person> {

    public PersonRepos(Class<?> clazz) {
        super(Person.class);
    }

}
