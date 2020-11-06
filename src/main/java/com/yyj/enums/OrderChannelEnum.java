package com.yyj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderChannelEnum {
    PC(1, "pc"),
    ANDROID(2, "android"),
    IOS(3, "ios");

    private Integer code;
    private String msg;
}
