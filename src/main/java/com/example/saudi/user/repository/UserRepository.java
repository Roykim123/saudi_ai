package com.example.saudi.user.repository;

import com.example.saudi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 필요한 경우 사용자 정보를 찾는 메서드를 추가할 수 있음.
    User findByOauthId(String oauthId);
}