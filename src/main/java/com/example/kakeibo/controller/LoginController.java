package com.example.kakeibo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ログイン画面を表示するだけのコントローラー。
 * ログインの認証処理自体はSpring Security（{@code SecurityConfig}）が行うため、
 * ここでは画面を返すことだけを担当する。
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
