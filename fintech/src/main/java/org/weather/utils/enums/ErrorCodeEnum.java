package org.weather.utils.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    TOO_MANY_REQUESTS(7777),
    UNKNOWN_ERROR_CODE(8888);
    private final int code;
    ErrorCodeEnum(int code) {
        this.code = code;
    }
}
