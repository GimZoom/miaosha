package com.yyj.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private final static String SALT="1a2b3c4d";

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    public static String inputPassToFormPass(String pass){
        String str=SALT.charAt(0)+SALT.charAt(2)+pass+SALT.charAt(5)+SALT.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String pass,String salt){
        String str=salt.charAt(0)+salt.charAt(2)+pass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String pass,String salt){
        String formPass = inputPassToFormPass(pass);
        String dbPass = formPassToDBPass(formPass, salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("annijun1999"));
        System.out.println(inputPassToDBPass("annijun1999","bwsezdg1"));
        System.out.println(formPassToDBPass("61fbe35c5b5de56ca074d1ad039e20da","bwsezdg1"));
        System.out.println(formPassToDBPass("ccdd55400f9065eb76748926344f3121","bwsezdg1"));
    }
}
