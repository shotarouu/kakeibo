package com.example.kakeibo.repository;

import com.example.kakeibo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ユーザー(User)のDBアクセスを担当するリポジトリ。
 * ログイン処理で「入力されたユーザー名のユーザーを探す」ために使う。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** ユーザー名でユーザーを検索する。Spring Securityの認証処理から呼ばれる。 */
    Optional<User> findByUsername(String username);
}
