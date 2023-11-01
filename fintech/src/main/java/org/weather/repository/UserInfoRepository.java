package org.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.weather.entity.UserInfoEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, UUID> {
    Optional<UserInfoEntity> findUserInfoEntityByUsername(String username);
}
