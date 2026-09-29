package com.example.kakeibo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * アプリケーションコンテキスト起動テスト。
 *
 * @SpringBootTest でフルコンテキストを起動し、すべてのBeanが正常に
 * 初期化できることを確認する（=設定ミスの早期発見）。
 * テスト用プロファイル(test)を有効にしてH2インメモリDBを使用する。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class KakeiboApplicationTest {

    @Test
    void contextLoads() {
        // コンテキストが正常に起動すればこのテストはパスする
    }

    @Test
    void main_startsApplicationWithoutException() {
        // main() メソッドのカバレッジ取得
        // H2インメモリDB + ランダムポートで起動し、すぐに終了
        KakeiboApplication.main(new String[]{
                "--spring.main.web-application-type=none",
                "--spring.profiles.active=test"
        });
    }
}
