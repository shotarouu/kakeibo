package com.example.kakeibo.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionType 列挙型のテスト。
 * getLabel() の戻り値と enum の宣言順序を確認する。
 */
class TransactionTypeTest {

    @Test
    void getLabel_income() {
        assertThat(TransactionType.INCOME.getLabel()).isEqualTo("収入");
    }

    @Test
    void getLabel_expense() {
        assertThat(TransactionType.EXPENSE.getLabel()).isEqualTo("支出");
    }

    @Test
    void values_containsBothTypes() {
        TransactionType[] values = TransactionType.values();
        assertThat(values).containsExactly(TransactionType.INCOME, TransactionType.EXPENSE);
    }

    @Test
    void valueOf_income() {
        assertThat(TransactionType.valueOf("INCOME")).isEqualTo(TransactionType.INCOME);
    }

    @Test
    void valueOf_expense() {
        assertThat(TransactionType.valueOf("EXPENSE")).isEqualTo(TransactionType.EXPENSE);
    }
}
