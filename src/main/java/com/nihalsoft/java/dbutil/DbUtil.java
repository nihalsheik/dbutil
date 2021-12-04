package com.nihalsoft.java.dbutil;

import java.util.Enumeration;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PoolBackedDataSource;

public class DbUtil {

    public static DB configure(Properties prop) throws Exception {
        return DbUtil.configure(prop, "");

    }

    public static DB configure(Properties prop, String prefix) throws Exception {

        Enumeration<Object> keys = prop.keys();

        Properties p2 = new Properties();

        while (keys.hasMoreElements()) {
            String k = keys.nextElement().toString();
            if (k.startsWith(prefix)) {
                p2.put(k.replace(prefix, ""), prop.get(k).toString());
            } else {
                p2.put(k, prop.get(k).toString());
            }
        }
        

        Properties p1 = new Properties();
        p1.put("user", p2.get("user"));
        p1.put("password", p2.get("password"));
        if (p2.containsKey("driverClass")) {
            System.out.println("Setting driver class");
            p1.put("driverClass", p2.get("driverClass"));
        }

        p2.remove("user");
        p2.remove("password");

        p2.forEach((k, v) -> System.out.println(" --> " + k + " = " + v));
        
        DataSource ds = DataSources.unpooledDataSource(p2.get("jdbcUrl").toString(), p1);

        PoolBackedDataSource ds2 = (PoolBackedDataSource) DataSources.pooledDataSource(ds, p2);
        DB db = new DB(ds2);
        db.execute("SELECT 1");
        return db;

    }
}
