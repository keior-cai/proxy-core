package com.socks.proxy.util;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;

import java.lang.invoke.SerializedLambda;
import java.util.Locale;

/**
 * @Description: field name utils
 */
public final class FieldNameUtils{

    private FieldNameUtils(){

    }


    public static <T> String getFieldName(Func1<T, ?> func){
        SerializedLambda resolve = LambdaUtil.resolve(func);
        return methodToProperty(resolve.getImplMethodName());
    }


    public static String methodToProperty(String name){
        if(name.startsWith("is")){
            name = name.substring(2);
        } else {
            if(!name.startsWith("get") && !name.startsWith("set")){
                throw new RuntimeException(
                        "Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if(name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))){
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }
}
