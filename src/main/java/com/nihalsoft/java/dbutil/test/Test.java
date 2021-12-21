package com.nihalsoft.java.dbutil.test;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import com.nihalsoft.java.dbutil.DB;
import com.nihalsoft.java.dbutil.Repository;

public class Test {

    public static void main2(String[] args) throws Exception {

//        System.out.println("Loading...");
//        ServiceLoader<PropertyHandler> propertyHandlers = ServiceLoader.load(PropertyHandler.class);
//        for (PropertyHandler p : propertyHandlers) {
//            System.out.println(p.getClass().getName());
//        }

        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    Test2.call();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    Test2.call();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        t1.start();
        t2.start();

    }

    public static void main(String[] args) throws Exception {

        Properties p = new Properties();
        p.put("minPoolSize", 5);
        p.put("initialPoolSize", 5);
        p.put("maxPoolSize", 10);
        p.put("checkoutTimeout", 1000);
        p.put("maxStatements", 10);
        p.put("autoCommitOnClose", true);

        DB db = new DB("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/repos", "root", "", p);
        trans(db);
    }

    public static void list1(DB db) throws Exception {

        Repository<Person> pr = db.repository(Person.class);

        List<Person> p3 = pr.findAll();

        for (Person per : p3) {
            System.out.println(String.format("%-10s - %-20s - %-10s - %-10s", per.getId(), per.getName(), per.getAge(),
                    per.getCreateTime()));
        }

        Person per2 = pr.findOne(8);
        System.out.println(String.format("%-10s - %-20s - %-10s - %-10s", per2.getId(), per2.getName(), per2.getAge(),
                per2.getCreateTime()));
    }

    public static void insert(PersonRepos pr) throws Exception {
        Person p = new Person();
        p.setName("sheik");
        p.setAge(40);
        p.setCreateTime(Calendar.getInstance().getTime());
        pr.insert(p);
    }

    public static void update1(PersonRepos pr) throws Exception {
        Person p = new Person();
        p.setId(8L);
        p.setName("Kavitha");
        pr.update(p);

    }

    public static void trans(DB db) throws Exception {

        db //
                .trans() //
                .withIsolation(Connection.TRANSACTION_READ_COMMITTED)//
                .exec(() -> {

                    System.out.println("test");

                    Person p = new Person();
                    p.setName("sheik");
                    p.setAge(40);
                    p.setCreateTime(Calendar.getInstance().getTime());
                    BigInteger id = db.repository(Person.class).insert(p);

                    Thread t1 = new Thread() {
                        @Override
                        public void run() {
                            try {
                                p.setId(id.longValue());
                                p.setName("updated");
                                db.repository(Person.class).update(p);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    t1.start();

                    p.setId(id.longValue());
                    p.setName("updated");
                    db.repository(Person.class).update(p);

                });

    }

}
