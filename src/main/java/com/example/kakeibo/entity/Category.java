package com.example.kakeibo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * 取引のカテゴリ（例: 食費、給料、日用品など）を表すエンティティ。
 * DBの "categories" テーブルと1対1で対応する。
 *
 * 1つのカテゴリは「収入用」か「支出用」のどちらかに属する（{@link TransactionType}）。
 */
@Entity
@Table(name = "categories")
public class Category {

    /** 主キー。DBが自動採番する（IDENTITY = テーブルのAUTO_INCREMENTを利用）。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** カテゴリ名（例: "食費"）。空文字は不可、かつ重複登録を防ぐためunique制約を付けている。 */
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    /** このカテゴリが収入用か支出用かを示す種別。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /** JPAがインスタンス生成時に使うデフォルトコンストラクタ（必須）。 */
    public Category() {
    }

    /** 名前と種別を指定してカテゴリを作成するためのコンストラクタ。 */
    public Category(String name, TransactionType type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
