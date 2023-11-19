package org.weather.utils.enums;

import lombok.Getter;

@Getter
public enum WeatherMessageEnum {
    WEATHER_ALREADY_EXISTS("weather.already.exists.message"),
    WEATHER_NOT_FOUND("weather.not.found.message"),
    API_KEY_INVALID("weatherapi.wrong.key.message"),
    API_KEY_DISABLED("weatherapi.disabled.key.message"),
    INVALID_URL("weatherapi.invalid.url.message"),
    UNKNOWN_EXCEPTION("weatherapi.unknown.exception.message"),
    TOO_MANY_REQUESTS("weatherapi.too.many.requests.message"),
    CITY_NOT_FOUND("city.not.found.message"),
    CITY_ALREADY_EXISTS("city.already.exists.message"),
    HANDBOOK_ALREADY_EXISTS("handbook.already.exists.message"),
    HANDBOOK_NOT_FOUND("handbook.not.found.message"),
    USER_NOT_FOUND("security.user.not.found.message");
    private final String code;
    WeatherMessageEnum(String code) {
        this.code = code;
    }
}
