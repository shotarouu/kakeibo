package com.example.kakeibo.service;

import com.example.kakeibo.entity.Transaction;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 取引（収入・支出）に関する業務処理（サービス層）。
 *
 * 取引の検索・保存・削除に加え、月単位の集計ロジック（{@link #summarize}）も
 * このクラスが担当する。集計処理をControllerではなくServiceに置くことで、
 * 「画面の都合」と「業務ロジック」を分離している。
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** 全期間の取引を新しい日付順に取得する。 */
    public List<Transaction> findAll() {
        return transactionRepository.findAllByOrderByDateDesc();
    }

    /** 指定した年月（例: 2026年6月）に含まれる取引だけを取得する。 */
    public List<Transaction> findByMonth(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return transactionRepository.findByDateBetweenOrderByDateDesc(start, end);
    }

    /** IDから取引を1件取得する。見つからない場合は例外を投げる。 */
    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("取引が見つかりません: " + id));
    }

    /** 取引を新規登録・更新する。 */
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /** 取引をIDで削除する。 */
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    /**
     * 指定した月の収支を集計する。
     *
     * 処理の流れ：
     *  1. その月の取引を全件取得する
     *  2. 1件ずつ見て、収入なら収入合計とカテゴリ別収入に加算、
     *     支出なら支出合計とカテゴリ別支出に加算する
     *  3. 集計結果（{@link MonthlySummary}）を返す
     *
     * この結果が一覧画面のサマリーボックスと円グラフの元データになる。
     */
    public MonthlySummary summarize(YearMonth month) {
        MonthlySummary summary = new MonthlySummary();
        for (Transaction t : findByMonth(month)) {
            String categoryName = t.getCategory().getName();
            if (t.getType() == TransactionType.INCOME) {
                summary.setTotalIncome(summary.getTotalIncome() + t.getAmount());
                summary.getIncomeByCategory().merge(categoryName, t.getAmount(), Long::sum);
            } else {
                summary.setTotalExpense(summary.getTotalExpense() + t.getAmount());
                summary.getExpenseByCategory().merge(categoryName, t.getAmount(), Long::sum);
            }
        }
        return summary;
    }
}
