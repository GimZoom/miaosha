package com.yyj.exception;

import com.yyj.enums.StatusEnum;

public class GlobalException extends RuntimeException {

    private StatusEnum statusEnum;

    public GlobalException(StatusEnum statusEnum) {
        super(statusEnum.toString());
        this.statusEnum = statusEnum;
    }

    public StatusEnum getStatusEnum() {
        return statusEnum;
    }
}
