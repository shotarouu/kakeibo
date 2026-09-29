package com.example.kakeibo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * ログインするユーザーを表すエンティティ。
 * DBの "users" テーブルと1対1で対応する。
 *
 * パスワードは平文では保存せず、BCryptでハッシュ化した文字列を保存する
 * （{@code SecurityConfig}のPasswordEncoderでハッシュ化・照合する）。
 */
@Entity
@Table(name = "users")
public class User {

    /** 主キー。DBが自動採番する。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ログインID。重複登録できないようにunique制約を付けている。 */
    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    /** BCryptでハッシュ化されたパスワード文字列（平文は保存しない）。 */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /** JPAがインスタンス生成時に使うデフォルトコンストラクタ（必須）。 */
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
