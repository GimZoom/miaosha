package com.yyj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MiaoshaStatusEnum {
    NOT_STARTED(0),
    BE_IN_PROGRESS(1),
    HAS_ENDED(2);

    private Integer code;
}
