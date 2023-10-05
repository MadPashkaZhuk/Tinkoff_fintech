package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
public class BaseWeatherApiExceptionDTO {
    @Schema(name = "Status", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Status for thrown exception", example = "404")
    HttpStatus status;
    @Schema(name = "Exception message", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Message for thrown exception", example = "Page doesn't exist")
    String exceptionMessage;
    @Schema(name = "Error code", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Code for specific error", example = "7777")
    int errorCode;
}
