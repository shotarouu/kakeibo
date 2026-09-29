package com.example.kakeibo.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Category エンティティのテスト。
 * コンストラクタ・getter・setter が正しく動作することを確認する。
 */
class CategoryTest {

    @Test
    void defaultConstructor_createsEmptyObject() {
        Category category = new Category();
        assertThat(category.getId()).isNull();
        assertThat(category.getName()).isNull();
        assertThat(category.getType()).isNull();
    }

    @Test
    void paramConstructor_setsNameAndType() {
        Category category = new Category("食費", TransactionType.EXPENSE);
        assertThat(category.getName()).isEqualTo("食費");
        assertThat(category.getType()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    void setters_updateFields() {
        Category category = new Category();
        category.setId(10L);
        category.setName("給与");
        category.setType(TransactionType.INCOME);

        assertThat(category.getId()).isEqualTo(10L);
        assertThat(category.getName()).isEqualTo("給与");
        assertThat(category.getType()).isEqualTo(TransactionType.INCOME);
    }
}
