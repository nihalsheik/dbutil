package com.nihalsoft.java.dbutil.test;

import com.nihalsoft.java.dbutil.Repository;

public class PersonRepos extends Repository<Person> {

    public PersonRepos(Class<?> clazz) {
        super(Person.class);
    }

}
