package com.nihalsoft.java.dbutil.result.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.dbutils.PropertyHandler;

public class LocalDateTimePropertyHandler implements PropertyHandler {

    @Override
    public boolean match(Class<?> parameter, Object value) {
        if (value instanceof LocalDateTime) {
            final String targetType = parameter.getName();
            if ("java.util.Date".equals(targetType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object apply(Class<?> parameter, Object value) {
        final String targetType = parameter.getName();
        if ("java.util.Date".equals(targetType)) {
            LocalDateTime ldt = (LocalDateTime) value;
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        }
        return value;
    }
}
