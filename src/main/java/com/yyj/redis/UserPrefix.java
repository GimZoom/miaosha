package com.yyj.redis;

public class UserPrefix extends BasePrefix {
    private final static Integer TOKEN_EXPIRE = 3600 * 24 * 2;  //redis中储存的用户信息默认维持2天

    private UserPrefix(String prefix) {
        super(prefix);
    }

    private UserPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserPrefix token = new UserPrefix(TOKEN_EXPIRE, "tk");
    public static UserPrefix id = new UserPrefix("id");
    public static UserPrefix test = new UserPrefix("test");
}
