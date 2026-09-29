package com.example.kakeibo.security;

import com.example.kakeibo.entity.User;
import com.example.kakeibo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * AppUserDetailsService の単体テスト。
 * ユーザーが見つかる場合と見つからない場合の両方を検証する。
 */
@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService service;

    @Test
    void loadUserByUsername_returnsUserDetails_whenUserExists() {
        User user = new User("admin", "$2a$10$hashed");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("admin");

        assertThat(details.getUsername()).isEqualTo("admin");
        assertThat(details.getPassword()).isEqualTo("$2a$10$hashed");
        assertThat(details.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
    }

    @Test
    void loadUserByUsername_throwsException_whenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("nobody"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nobody");
    }
}
