package com.example.kakeibo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップページ（"/"）用のコントローラー。
 * アプリのトップにアクセスがあったら、取引一覧画面に転送するだけのシンプルな役割。
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/transactions";
    }
}
