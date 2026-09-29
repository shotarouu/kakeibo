package com.example.kakeibo.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ある月の収支をまとめた集計結果を保持するクラス（DTO）。
 * DBのテーブルとは対応しない、画面表示専用のデータの入れ物。
 *
 * {@link TransactionService#summarize} で計算され、
 * 一覧画面の円グラフやサマリーボックスの表示に使われる。
 */
public class MonthlySummary {

    /** その月の収入の合計金額。 */
    private long totalIncome;

    /** その月の支出の合計金額。 */
    private long totalExpense;

    /** カテゴリ名 → 支出合計金額。円グラフの「支出」側のデータ元になる。
     *  LinkedHashMapを使い、取引が登場した順序を保ったまま集計する。 */
    private final Map<String, Long> expenseByCategory = new LinkedHashMap<>();

    /** カテゴリ名 → 収入合計金額。円グラフの「収入」側のデータ元になる。 */
    private final Map<String, Long> incomeByCategory = new LinkedHashMap<>();

    public long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public long getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(long totalExpense) {
        this.totalExpense = totalExpense;
    }

    /** 収支（収入 - 支出）。マイナスなら使い過ぎということになる。 */
    public long getBalance() {
        return totalIncome - totalExpense;
    }

    public Map<String, Long> getExpenseByCategory() {
        return expenseByCategory;
    }

    public Map<String, Long> getIncomeByCategory() {
        return incomeByCategory;
    }
}
