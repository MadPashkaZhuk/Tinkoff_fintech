package org.weather.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.weather.entity.UserInfoEntity;
import org.weather.repository.UserInfoRepository;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserInfoUserDetailsService implements UserDetailsService {
    private final UserInfoRepository userInfoRepository;
    private final MessageSourceWrapper messageSourceWrapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfoEntity> userInfoEntity = userInfoRepository.findUserInfoEntityByUsername(username);
        return userInfoEntity.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSourceWrapper.getMessageCode(WeatherMessageEnum.USER_NOT_FOUND) + username));
    }
}
