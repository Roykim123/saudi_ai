package com.example.saudi.user.repository;

import com.example.saudi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthId(String oauthId);  // 반환 타입을 Optional<User>로 수정
}