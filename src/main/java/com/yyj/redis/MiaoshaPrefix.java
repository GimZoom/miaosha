package com.yyj.redis;

public class MiaoshaPrefix extends BasePrefix {

    private MiaoshaPrefix(String prefix) {
        super(prefix);
    }

    private MiaoshaPrefix(Integer expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaPrefix isQueueUp = new MiaoshaPrefix("qu");
    public static MiaoshaPrefix MiaoshaPath = new MiaoshaPrefix(60, "mp");
    public static MiaoshaPrefix MiaoshaVerifyCode = new MiaoshaPrefix(300, "mvc");
}
