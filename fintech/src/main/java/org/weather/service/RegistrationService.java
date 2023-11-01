package org.weather.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.weather.dto.UserCredentialsDTO;
import org.weather.entity.UserInfoEntity;
import org.weather.repository.UserInfoRepository;
import org.weather.utils.enums.UserRoleEnum;

@Service
public class RegistrationService {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addDefaultUser(UserCredentialsDTO userInfo) {
        UserInfoEntity entity = new UserInfoEntity(
                userInfo.getUsername(),
                passwordEncoder.encode(userInfo.getPassword()),
                UserRoleEnum.USER.getRole()
        );
        userInfoRepository.save(entity);
    }
}
