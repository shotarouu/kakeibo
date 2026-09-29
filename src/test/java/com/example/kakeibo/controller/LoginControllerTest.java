package com.example.kakeibo.controller;

import com.example.kakeibo.config.SecurityConfig;
import com.example.kakeibo.security.AppUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * LoginController の Web 層テスト。
 * GET /login が 200 を返し、login テンプレートを使用することを確認する。
 *
 * @Import(SecurityConfig.class) でカスタムSecurityConfigを読み込む。
 * これにより loginPage("/login") が有効になり、LoginControllerにリクエストが届く。
 */
@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // SecurityConfigがUserDetailsServiceを必要とするためモック化
    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void getLogin_returns200AndLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
