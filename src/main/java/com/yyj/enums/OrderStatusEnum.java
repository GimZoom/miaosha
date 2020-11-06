package com.yyj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    NEW_UNPAID(0,"新建未支付"),
    PAID(1,"已支付"),
    DELIVERED(2,"已发货"),
    RECEIVED(3,"已收货"),
    REFUNDED(4,"已退款"),
    FINISHED(5,"已完成");

    private Integer code;
    private String msg;
}
