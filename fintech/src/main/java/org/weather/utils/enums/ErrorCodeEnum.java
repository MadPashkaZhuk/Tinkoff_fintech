package org.weather.utils.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    API_KEY_DISABLED(1111),
    INVALID_URL(2222),
    API_KEY_INVALID(3333),
    TOO_MANY_REQUESTS(4444),
    UNKNOWN_ERROR_CODE(5555);
    private final int code;
    ErrorCodeEnum(int code) {
        this.code = code;
    }
}
