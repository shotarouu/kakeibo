package com.example.kakeibo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * 1件の取引（収入または支出の記録）を表すエンティティ。
 * DBの "transactions" テーブルと1対1で対応する。
 *
 * 例: 「2026-06-15 に 食費カテゴリで 1500円 支出した（メモ: ランチ）」という1行分のデータ。
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    /** 主キー。DBが自動採番する。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 取引が発生した日付。 */
    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    /** 収入か支出かの種別。 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /** 金額（円）。0円以下は登録できないようにPositiveで検証している。 */
    @NotNull
    @Positive
    @Column(nullable = false)
    private Long amount;

    /**
     * この取引が属するカテゴリ。
     * 多対1（複数の取引が1つのカテゴリに属する）の関連。
     * EAGERにしているのは、取引一覧表示時に毎回カテゴリ名も使うため、
     * 都度追加クエリを発行せずまとめて取得したいから。
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** 任意のメモ（例: "ランチ"）。未入力でもよい。 */
    @Column(length = 255)
    private String memo;

    /** JPAがインスタンス生成時に使うデフォルトコンストラクタ（必須）。 */
    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
