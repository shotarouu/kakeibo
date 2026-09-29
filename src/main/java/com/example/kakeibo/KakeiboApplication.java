package com.example.kakeibo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 家計簿アプリのエントリーポイント（起動クラス）。
 *
 * {@code @SpringBootApplication} が付いていることで、以下がまとめて有効になる。
 *  - コンポーネントスキャン（@Controller, @Service, @Repository などを自動検出）
 *  - 自動設定（DataSourceやThymeleafなどをクラスパスから自動構成）
 */
@SpringBootApplication
public class KakeiboApplication {

    /**
     * アプリケーションの起動メソッド。
     * 内部でTomcatを立ち上げ、http://localhost:8080 でWebアプリを公開する。
     */
    public static void main(String[] args) {
        SpringApplication.run(KakeiboApplication.class, args);
    }
}
