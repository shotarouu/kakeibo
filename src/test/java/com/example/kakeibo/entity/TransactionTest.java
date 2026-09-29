package com.example.kakeibo.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Transaction エンティティのテスト。
 * すべてのフィールドの getter・setter が正しく動作することを確認する。
 */
class TransactionTest {

    @Test
    void defaultConstructor_createsEmptyObject() {
        Transaction t = new Transaction();
        assertThat(t.getId()).isNull();
        assertThat(t.getDate()).isNull();
        assertThat(t.getType()).isNull();
        assertThat(t.getAmount()).isNull();
        assertThat(t.getCategory()).isNull();
        assertThat(t.getMemo()).isNull();
    }

    @Test
    void setters_updateAllFields() {
        Category category = new Category("食費", TransactionType.EXPENSE);
        LocalDate date = LocalDate.of(2026, 6, 1);

        Transaction t = new Transaction();
        t.setId(1L);
        t.setDate(date);
        t.setType(TransactionType.EXPENSE);
        t.setAmount(1500L);
        t.setCategory(category);
        t.setMemo("ランチ");

        assertThat(t.getId()).isEqualTo(1L);
        assertThat(t.getDate()).isEqualTo(date);
        assertThat(t.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(t.getAmount()).isEqualTo(1500L);
        assertThat(t.getCategory()).isEqualTo(category);
        assertThat(t.getMemo()).isEqualTo("ランチ");
    }
}
