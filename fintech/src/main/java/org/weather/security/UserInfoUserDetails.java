package org.weather.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.weather.entity.UserInfoEntity;

import java.util.Collection;
import java.util.List;

public class UserInfoUserDetails implements UserDetails {
    private final String username;
    private final String password;
    private final List<GrantedAuthority> grantedAuthorities;

    public UserInfoUserDetails(UserInfoEntity userInfo) {
        this.username = userInfo.getUsername();
        this.password = userInfo.getPassword();
        this.grantedAuthorities = List.of(new SimpleGrantedAuthority(userInfo.getRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
