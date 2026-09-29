package com.example.kakeibo.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MonthlySummary DTO のテスト。
 * getBalance() の計算ロジックと各フィールドの getter/setter を確認する。
 */
class MonthlySummaryTest {

    @Test
    void getBalance_returnsIncomeMinusExpense() {
        MonthlySummary summary = new MonthlySummary();
        summary.setTotalIncome(100_000L);
        summary.setTotalExpense(40_000L);

        assertThat(summary.getBalance()).isEqualTo(60_000L);
    }

    @Test
    void getBalance_negativeWhenExpenseExceedsIncome() {
        MonthlySummary summary = new MonthlySummary();
        summary.setTotalIncome(10_000L);
        summary.setTotalExpense(50_000L);

        assertThat(summary.getBalance()).isEqualTo(-40_000L);
    }

    @Test
    void getBalance_zeroWhenBothZero() {
        MonthlySummary summary = new MonthlySummary();
        assertThat(summary.getBalance()).isZero();
    }

    @Test
    void expenseByCategory_isModifiable() {
        MonthlySummary summary = new MonthlySummary();
        summary.getExpenseByCategory().put("食費", 5000L);
        summary.getExpenseByCategory().merge("食費", 3000L, Long::sum);

        assertThat(summary.getExpenseByCategory()).containsEntry("食費", 8000L);
    }

    @Test
    void incomeByCategory_isModifiable() {
        MonthlySummary summary = new MonthlySummary();
        summary.getIncomeByCategory().put("給与", 300_000L);

        assertThat(summary.getIncomeByCategory()).containsEntry("給与", 300_000L);
    }

    @Test
    void totalIncome_getterAndSetter() {
        MonthlySummary summary = new MonthlySummary();
        summary.setTotalIncome(999L);
        assertThat(summary.getTotalIncome()).isEqualTo(999L);
    }

    @Test
    void totalExpense_getterAndSetter() {
        MonthlySummary summary = new MonthlySummary();
        summary.setTotalExpense(888L);
        assertThat(summary.getTotalExpense()).isEqualTo(888L);
    }
}
