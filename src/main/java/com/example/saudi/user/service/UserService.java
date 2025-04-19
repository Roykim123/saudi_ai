package com.example.saudi.user.service;

import com.example.saudi.user.entity.User;
import com.example.saudi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // UserRepository를 주입받는 생성자
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // @Transactional: 트랜잭션을 관리하여 DB 작업이 일관되게 처리됨
    @Transactional
    public User saveUser(String oauthId, String email, String nickname, String profileImage) {
        // 기존 사용자 찾기 (OauthId로 찾기)
        Optional<User> existingUserOptional = userRepository.findByOauthId(oauthId);

        if (existingUserOptional.isPresent()) {
            // 기존 사용자 정보가 있으면, 이메일, 닉네임, 프로필 이미지 갱신
            User existingUser = existingUserOptional.get(); // Optional에서 User 객체 꺼내기

            if (!existingUser.getEmail().equals(email)) {
                existingUser.setEmail(email); // 이메일 변경
            }

            // 닉네임 변경 여부 체크 (null 체크 추가)
            if (existingUser.getNickname() == null || !existingUser.getNickname().equals(nickname)) {
                existingUser.setNickname(nickname); // 닉네임 변경
            }

            // 프로필 이미지 변경 여부 체크 (null 체크 추가)
            if (existingUser.getProfileImage() == null || !existingUser.getProfileImage().equals(profileImage)) {
                existingUser.setProfileImage(profileImage); // 프로필 이미지 변경
            }

            // 변경된 사용자 저장 (DB에 갱신)
            return userRepository.save(existingUser);
        } else {
            // 기존 사용자 없으면 새로운 사용자 생성 후 저장
            User newUser = new User(oauthId, email, nickname, profileImage);
            return userRepository.save(newUser); // 새 사용자 저장
        }
    }
}
