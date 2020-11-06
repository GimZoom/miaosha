package com.yyj.redis;

public class AccessPrefix extends BasePrefix {
    private AccessPrefix(String prefix) {
        super(prefix);
    }

    private AccessPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static AccessPrefix getAccessPrefix(Integer expire){
        return new AccessPrefix(expire,"access");
    }
}
