package org.weather.dto;

import lombok.Value;

@Value
public class UserCredentialsDTO {
    String username;
    char[] password;
}
