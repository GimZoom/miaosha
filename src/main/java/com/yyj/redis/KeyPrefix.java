package com.yyj.redis;

public interface KeyPrefix {
    Integer getExpireSeconds();

    String getPrefix();
}
