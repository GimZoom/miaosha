package com.yyj.redis;

public class GoodsPrefix extends BasePrefix {

    private GoodsPrefix(String prefix) {
        super(prefix);
    }

    private GoodsPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsPrefix goodsList = new GoodsPrefix(60, "gl");
    public static GoodsPrefix goodsDetail = new GoodsPrefix(60, "gd");
    public static GoodsPrefix goodsStock = new GoodsPrefix("gs");
}
