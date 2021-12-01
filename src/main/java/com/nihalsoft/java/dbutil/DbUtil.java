package com.nihalsoft.java.dbutil;

import java.util.Enumeration;
import java.util.Properties;

import javax.sql.DataSource;

import com.nihalsoft.java.dbutil.common.DataMap;
import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PoolBackedDataSource;

public class DbUtil {

    public static DB configure(String jdbcUrl, String user, String password) throws Exception {
        return DbUtil.configure(jdbcUrl, user, password, null);
    }

    public static DB configure(String jdbcUrl, String user, String password, Properties prop) throws Exception {

        DataMap dm = new DataMap();

        Enumeration<Object> keys = prop.keys();

        if (prop != null) {
            while (keys.hasMoreElements()) {
                String k = keys.nextElement().toString();
                if (k.startsWith("db.")) {
                    String[] s = k.split("\\.");
                    if (s.length == 2) {
                        dm.put(k.replace("db.", ""), prop.get(k));
                    }
                } else {
                    dm.put(k, prop.get(k));
                }
            }

            dm.forEach((k, v) -> System.out.println(" --> " + k + " = " + v));
        }

        Properties pp = new Properties();
        pp.put("user", user);
        pp.put("password", password);
        pp.put("driverClass", dm.get("driverClass"));

        DataSource ds = DataSources.unpooledDataSource(jdbcUrl, pp);

        PoolBackedDataSource ds2 = (PoolBackedDataSource) DataSources.pooledDataSource(ds, dm);
        DB db = new DB(ds2);
        db.execute("SELECT 1");
        return db;

    }
}
