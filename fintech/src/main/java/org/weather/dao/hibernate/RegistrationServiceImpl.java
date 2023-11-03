package org.weather.dao.hibernate;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.weather.dto.UserCredentialsDTO;
import org.weather.entity.UserInfoEntity;
import org.weather.repository.UserInfoRepository;
import org.weather.utils.enums.UserRoleEnum;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public void addDefaultUser(UserCredentialsDTO userInfo) {
        UserInfoEntity entity = new UserInfoEntity(
                userInfo.getUsername(),
                passwordEncoder.encode(userInfo.getPassword()),
                UserRoleEnum.ROLE_USER
        );
        userInfoRepository.save(entity);
    }
}
