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
    public User saveUser(String oauthId, String email, String nickname, String profileImage) {
        User existingUser = userRepository.findByOauthId(oauthId);
        if (existingUser != null) {
            // 이메일 변경 여부 체크
            if (!existingUser.getEmail().equals(email)) {
                existingUser.setEmail(email);
            }

            // 닉네임 변경 여부 체크 (null 체크 추가)
            if (existingUser.getNickname() == null || !existingUser.getNickname().equals(nickname)) {
                existingUser.setNickname(nickname);
            }

            // 프로필 이미지 변경 여부 체크 (null 체크 추가)
            if (existingUser.getProfileImage() == null || !existingUser.getProfileImage().equals(profileImage)) {
                existingUser.setProfileImage(profileImage);
            }

            return userRepository.save(existingUser); // 갱신 후 저장
        }

        // 새로운 사용자 저장
        User user = new User(oauthId, email, nickname, profileImage);
        return userRepository.save(user);
    }
}
