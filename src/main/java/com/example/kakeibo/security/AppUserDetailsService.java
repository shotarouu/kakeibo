package com.example.kakeibo.security;

import com.example.kakeibo.entity.User;
import com.example.kakeibo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Securityが認証時に呼び出す「ユーザー検索処理」の実装。
 *
 * ログインフォームで入力されたユーザー名をもとに、{@link UserRepository} でDBを検索し、
 * 見つかった{@link User}をSpring Security用の{@link UserDetails}に変換して返す。
 * パスワードの一致確認自体はSpring Securityが内部で（PasswordEncoderを使って）行う。
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
