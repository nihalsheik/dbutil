package com.nihalsoft.java.dbutil;

import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

public class DbUtil {

    public static DB configure(String driverClass, String jdbcUrl, String user, String password) throws Exception {
        return DbUtil.configure(driverClass, jdbcUrl, user, password, null);
    }

    public static DB configure(String driverClass, String jdbcUrl, String user, String password, Properties prop)
            throws Exception {

        Properties p = new Properties();
        p.put("user", user);
        p.put("password", password);
        p.put("driverClass", driverClass);

        DataSource uds = DataSources.unpooledDataSource(jdbcUrl, p);

        if (prop != null) {
            for (Object key : prop.keySet()) {
                prop.put(key, prop.get(key).toString());
            }
            prop.forEach((k, v) -> System.out.println(" --> " + k + " = " + v));
            return new DB(DataSources.pooledDataSource(uds, prop));
        } else {
            return new DB(DataSources.pooledDataSource(uds));
        }

    }
}
