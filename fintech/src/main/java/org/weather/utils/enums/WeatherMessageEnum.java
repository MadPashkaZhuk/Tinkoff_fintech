package org.weather.utils.enums;

import lombok.Getter;

@Getter
public enum WeatherMessageEnum {
    WEATHER_ALREADY_EXISTS("weather.already.exists.message"),
    REGION_NOT_FOUND("weather.not.found.message"),
    API_KEY_INVALID("weatherapi.wrong.key.message"),
    API_KEY_DISABLED("weatherapi.disabled.key.message"),
    INVALID_URL("weatherapi.invalid.url.message"),
    UNKNOWN_EXCEPTION("weatherapi.unknown.exception.message"),
    TOO_MANY_REQUESTS("weatherapi.too.many.requests.message");

    private final String code;
    WeatherMessageEnum(String code) {
        this.code = code;
    }
}
