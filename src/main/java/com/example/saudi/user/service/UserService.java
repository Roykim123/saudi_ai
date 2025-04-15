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
            // 이메일이 변경된 경우 갱신
            if (!existingUser.getEmail().equals(email)) {
                existingUser.setEmail(email);
                return userRepository.save(existingUser); // 이메일 갱신 후 저장
            }
            return existingUser; // 이메일이 동일하면 그대로 리턴
        }

        // 새로운 사용자 저장
        User user = new User(oauthId, email);
        return userRepository.save(user);
    }
}
