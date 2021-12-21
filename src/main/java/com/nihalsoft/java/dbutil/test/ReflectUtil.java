package com.nihalsoft.java.dbutil.test;

import java.lang.reflect.Method;

public class ReflectUtil {
    
    public static String parametersAsString(Method method) {
        return parametersAsString(method, false);
    }

    public static String getSignature(Method method, boolean longTypeNames) {
        return method.getName() + "(" + parametersAsString(method, longTypeNames) + ")";
    }

    public static String parametersAsString(Method method, boolean longTypeNames) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0)
            return "";
        StringBuilder paramString = new StringBuilder();
        paramString.append(longTypeNames ? parameterTypes[0].getName() : parameterTypes[0].getSimpleName());
        for (int i = 1; i < parameterTypes.length; i++) {
            paramString.append(",")
                    .append(longTypeNames ? parameterTypes[i].getName() : parameterTypes[i].getSimpleName());
        }
        return paramString.toString();
    }

    public static String getSignature(Method method) {
        return getSignature(method, false);
    }

}
