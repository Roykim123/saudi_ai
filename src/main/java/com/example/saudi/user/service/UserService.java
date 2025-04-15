package com.example.saudi.user.service;

import com.example.saudi.user.entity.User;
import com.example.saudi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User saveUser(String oauthId, String email) {
        User existingUser = userRepository.findByOauthId(oauthId);
        if (existingUser != null) {
            return existingUser; // 이미 존재하는 경우 기존 사용자 리턴
        }

        User user = new User(oauthId, email);
        return userRepository.save(user); // 새로운 사용자 저장
    }
}
