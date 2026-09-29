package com.example.kakeibo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * アプリ全体のセキュリティ設定（ログイン機能の中心となるクラス）。
 *
 * 設定している内容：
 *  - "/login" や静的ファイル(css)は未ログインでもアクセス可能
 *  - それ以外の全ページ（取引一覧・カテゴリ管理など）はログインが必須
 *  - ログインフォームはThymeleafで自作した "/login" 画面を使う
 *  - ログイン成功後は "/transactions" に遷移する
 *  - ログアウトは "/logout" にPOSTすると行われ、"/login?logout" に戻る
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * パスワードをハッシュ化・照合するためのエンコーダー。
     * ユーザー登録時はこれでハッシュ化した文字列をDBに保存し、
     * ログイン時はSpring Securityがこれを使って入力値とハッシュを比較する。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * URLごとのアクセス制御やログイン/ログアウトの挙動を定義するフィルターチェーン。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/transactions", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // 学習用の単純な構成のため、CSRFはデフォルト(有効)のままフォームにトークンを埋め込んで対応する
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }
}
