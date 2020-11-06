package com.yyj.enums;

public enum StatusEnum {
    SUCCESS(0, "成功"),
    SERVER_ERROR(500100, "服务端异常"),
    BIND_ERROR(500101, "参数校验异常：%s"),
    REQUEST_ILLEGAL(500102, "请求非法"),
    ACCESS_LIMIT(500103, "访问太频繁"),

    //登录模块 5002XX
    SESSION_ERROR(500200, "SESSION不存在或已失效"),
    PASSWORD_EMPTY(500201, "登录密码不能为空"),
    MOBILE_EMPTY(500202, "手机号码不能为空"),
    MOBILE_ERROR(500203, "手机号码格式错误"),
    MOBILE_NOT_EXIST(500204, "手机号码不存在"),
    PASSWORD_ERROR(500205, "密码错误"),

    //商品模块 5003XX
    GOODS_NOT_EXIST(500300,"商品不存在"),

    //订单模块 5004XX
    ORDER_NOT_EXIST(500400, "订单不存在"),

    //秒杀模块 5005XX
    MIAOSHA_OVER(500500, "秒杀已结束"),
    MIAOSHA_REPEAT(500501, "不能重复秒杀"),
    VERIFY_CODE_ERROR(500502, "验证码有误，请重新输入");

    private Integer code;
    private String msg;

    public StatusEnum fillArgs(Object... objects) {
        Integer code = this.code;
        String format = String.format(this.msg, objects);
        StatusEnum bindError = StatusEnum.BIND_ERROR;
        bindError.setMsg(format);
        return bindError;
    }

    StatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "StatusEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
