package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.config.auth.dto.OAuthAttributes;
import com.jojoldu.book.springboot.config.auth.dto.SessionUser;
import com.jojoldu.book.springboot.domain.user.UserInfo;
import com.jojoldu.book.springboot.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //로그인 진행 중인 서비스 구분 (구글, 네이버, 카카오,...)
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // registrationId : 현재 로그인 진행 중인 서비스를 구분하는 코드
        String userNameAttributeName = userRequest. // userNameAttributeName : OAuth2 로그인 진행 시 키가 되는 필드값, Primary Key와 같은 의미 (구글 = sub)
                getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2User 의 attribute 등을 담을 클래스
        OAuthAttributes attributes = OAuthAttributes. // OAuthAttributes : OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
                of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 사용자 저장, 세션에 사용자 정보 저장
        UserInfo user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user)); // SessionUser : 세션에 사용자 정보를 저장하기 위한 Dto 클래스

        return new DefaultOAuth2User(
                Collections.singleton(
                        new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private UserInfo saveOrUpdate(OAuthAttributes attributes) {
        UserInfo user = userRepository.findByEmail(attributes.getEmail())
                // 가입 O -> 정보 업데이트
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                // 가입 X -> User 엔티티 생성
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}