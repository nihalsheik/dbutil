package com.nihalsoft.java.dbutil.test;

import java.util.List;
import java.util.Properties;

import com.nihalsoft.java.dbutil.DB;
import com.nihalsoft.java.dbutil.DbUtil;

public class Test {

    public static void main(String[] args) throws Exception {

        final Properties p = new Properties();
        p.put("user", "root");
        p.put("password", "Welcome@1");
        p.put("jdbcUrl", "jdbc:mysql://localhost:3306/repos");
        p.put("driverClass", "com.mysql.jdbc.Driver");
        p.put("minPoolSize", 5);
        p.put("initialPoolSize", 5);
        p.put("maxPoolSize", 10);
        p.put("checkoutTimeout", 1000);

        DB db = DbUtil.configure(p);

//        DataMap list = db.queryForDataMap("select * from tbl_client");

//        System.out.println(list.get("email"));

//        DataMap dm = new DataMap();
//        dm.put("name", "sheik");
//        dm.put("email", "email-333");
////        BigInteger iid = db.insert(dm, "tbl_client");
//
//        db.update(dm, "tbl_client", "id=?", 13);

//        Person p2 = new Person();
//        p2.setName("sheik");
//        p2.setAge(40);
//        p2.setCreateTime(Calendar.getInstance().getTime());
//        pr.insert(p2);

        List<Person> p3 = db.queryForBeanList("select create_time,id,name,age from tbl_person", Person.class);

        for (Person per : p3) {
            System.out.println("---------------------");
            System.out.println(per.getId());
            System.out.println(per.getName());
            System.out.println(per.getAge());
            System.out.println(per.getCreateTime());
        }

    }

}
