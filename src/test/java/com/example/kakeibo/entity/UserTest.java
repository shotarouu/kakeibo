package com.example.kakeibo.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User エンティティのテスト。
 */
class UserTest {

    @Test
    void defaultConstructor_createsEmptyObject() {
        User user = new User();
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
    }

    @Test
    void paramConstructor_setsUsernameAndPassword() {
        User user = new User("admin", "hashed_password");
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("hashed_password");
    }

    @Test
    void setters_updateFields() {
        User user = new User();
        user.setId(5L);
        user.setUsername("user1");
        user.setPassword("secret");

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getPassword()).isEqualTo("secret");
    }
}
