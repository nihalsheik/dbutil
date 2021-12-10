package com.nihalsoft.java.dbutil.test;

import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import org.apache.commons.dbutils.PropertyHandler;

import com.nihalsoft.java.dbutil.DB;
import com.nihalsoft.java.dbutil.DbUtil;
import com.nihalsoft.java.dbutil.common.DataMap;

public class Test {

    public static void main2(String[] args) throws Exception {

        System.out.println("Loading...");
        ServiceLoader<PropertyHandler> propertyHandlers = ServiceLoader.load(PropertyHandler.class);
        for (PropertyHandler p : propertyHandlers) {
            System.out.println(p.getClass().getName());
        }
    }

    public static void main(String[] args) throws Exception {

        Properties p = new Properties();
        p.put("minPoolSize", 5);
        p.put("initialPoolSize", 5);
        p.put("maxPoolSize", 10);
        p.put("checkoutTimeout", 1000);
        p.put("maxStatements", 10);

        DB db = DbUtil.configure("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/repos", "root", "", p);

//        DataMap list = db.queryForDataMap("select * from tbl_client");
//
//        System.out.println(list.get("email"));
//        System.out.println("---------------------");
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

        PersonDao pd = new PersonDao(db);
        List<Person> p3 = pd.findAll();

        for (Person per : p3) {
            System.out.println("---------------------");
            System.out.println(per.getId());
            System.out.println(per.getName());
            System.out.println(per.getAge());
            System.out.println(per.getCreateTime());
        }

        p3.get(0).setName("updated");
        
        pd.update(p3.get(0));
    }

}
