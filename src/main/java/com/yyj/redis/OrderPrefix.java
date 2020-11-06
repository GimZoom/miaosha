package com.yyj.redis;

public class OrderPrefix extends BasePrefix {
    private OrderPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    private OrderPrefix(String prefix) {
        super(prefix);
    }

    public static final OrderPrefix getMiaoshaOrderByUserIdAndGoodsId=new OrderPrefix("moug");
}
