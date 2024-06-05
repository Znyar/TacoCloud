package com.znyar.tacos.security;

import com.znyar.tacos.data.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByUsername(email);
        if (user == null) {
            user = new User();
            user.setUsername(email);
            user.setCity(oAuth2User.getAttribute("city"));
            user.setState(oAuth2User.getAttribute("state"));
            user.setZip(oAuth2User.getAttribute("zip"));
            user.setStreet(oAuth2User.getAttribute("street"));
            userRepository.save(user);
        }
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return new CustomOAuth2User(oAuth2User, Collections.singletonList(authority), user);
    }

    @RequiredArgsConstructor
    @Getter
    public static class CustomOAuth2User implements OAuth2User {

        private final OAuth2User oAuth2User;
        private final Collection<? extends GrantedAuthority> authorities;
        private final User user;

        @Override
        public Map<String, Object> getAttributes() {
            return oAuth2User.getAttributes();
        }

        @Override
        public String getName() {
            return oAuth2User.getName();
        }
    }

}
