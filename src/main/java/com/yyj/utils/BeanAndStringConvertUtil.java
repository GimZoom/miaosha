package com.yyj.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

public class BeanAndStringConvertUtil {
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    public static <T> String beanToString(T t) {
        if (t == null) {
            return null;
        }
        Class<?> clazz = t.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + t;
        } else if (clazz == String.class) {
            return (String) t;
        } else if (clazz == long.class || clazz == Long.class) {
            return "" + t;
        } else {
            return JSON.toJSONString(t);
        }
    }
}
