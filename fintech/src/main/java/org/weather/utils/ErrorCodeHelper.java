package org.weather.utils;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.weather.utils.enums.ErrorCodeEnum;

@Component
@NoArgsConstructor
public class ErrorCodeHelper {
    public int getCode(ErrorCodeEnum codeEnum) {
        return codeEnum.getCode();
    }
}
