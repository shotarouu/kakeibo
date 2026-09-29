package com.example.kakeibo.service;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.Transaction;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TransactionService の単体テスト。
 * summarize() の集計ロジックを含め、すべてのメソッドを検証する。
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    // ---- findAll ----

    @Test
    void findAll_delegatesToRepository() {
        List<Transaction> expected = List.of(makeTransaction(TransactionType.EXPENSE, 1000L, "食費"));
        when(transactionRepository.findAllByOrderByDateDesc()).thenReturn(expected);

        assertThat(transactionService.findAll()).isEqualTo(expected);
        verify(transactionRepository).findAllByOrderByDateDesc();
    }

    // ---- findByMonth ----

    @Test
    void findByMonth_queriesCorrectDateRange() {
        YearMonth month = YearMonth.of(2026, 6);
        when(transactionRepository.findByDateBetweenOrderByDateDesc(
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30)))
                .thenReturn(List.of());

        transactionService.findByMonth(month);

        verify(transactionRepository).findByDateBetweenOrderByDateDesc(
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
    }

    // ---- findById ----

    @Test
    void findById_returnsTransaction_whenFound() {
        Transaction t = makeTransaction(TransactionType.EXPENSE, 500L, "食費");
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(t));

        assertThat(transactionService.findById(1L)).isEqualTo(t);
    }

    @Test
    void findById_throwsException_whenNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    // ---- save ----

    @Test
    void save_delegatesToRepository() {
        Transaction t = makeTransaction(TransactionType.INCOME, 300_000L, "給与");
        when(transactionRepository.save(t)).thenReturn(t);

        assertThat(transactionService.save(t)).isEqualTo(t);
        verify(transactionRepository).save(t);
    }

    // ---- delete ----

    @Test
    void delete_delegatesToRepository() {
        transactionService.delete(2L);
        verify(transactionRepository).deleteById(2L);
    }

    // ---- summarize ----

    @Test
    void summarize_aggregatesExpenseAndIncome() {
        YearMonth month = YearMonth.of(2026, 6);
        Transaction expense1 = makeTransaction(TransactionType.EXPENSE, 1500L, "食費");
        Transaction expense2 = makeTransaction(TransactionType.EXPENSE, 500L, "食費");
        Transaction income  = makeTransaction(TransactionType.INCOME,  300_000L, "給与");

        when(transactionRepository.findByDateBetweenOrderByDateDesc(any(), any()))
                .thenReturn(List.of(expense1, expense2, income));

        MonthlySummary summary = transactionService.summarize(month);

        assertThat(summary.getTotalExpense()).isEqualTo(2000L);
        assertThat(summary.getTotalIncome()).isEqualTo(300_000L);
        assertThat(summary.getBalance()).isEqualTo(298_000L);
        assertThat(summary.getExpenseByCategory()).containsEntry("食費", 2000L);
        assertThat(summary.getIncomeByCategory()).containsEntry("給与", 300_000L);
    }

    @Test
    void summarize_emptyMonth_returnsZeroSummary() {
        when(transactionRepository.findByDateBetweenOrderByDateDesc(any(), any()))
                .thenReturn(List.of());

        MonthlySummary summary = transactionService.summarize(YearMonth.of(2026, 1));

        assertThat(summary.getTotalExpense()).isZero();
        assertThat(summary.getTotalIncome()).isZero();
        assertThat(summary.getExpenseByCategory()).isEmpty();
        assertThat(summary.getIncomeByCategory()).isEmpty();
    }

    @Test
    void summarize_multipleExpenseCategories_aggregatedSeparately() {
        Transaction food    = makeTransaction(TransactionType.EXPENSE, 1000L, "食費");
        Transaction utility = makeTransaction(TransactionType.EXPENSE, 2000L, "日用品");

        when(transactionRepository.findByDateBetweenOrderByDateDesc(any(), any()))
                .thenReturn(List.of(food, utility));

        MonthlySummary summary = transactionService.summarize(YearMonth.of(2026, 6));

        assertThat(summary.getExpenseByCategory())
                .containsEntry("食費", 1000L)
                .containsEntry("日用品", 2000L);
    }

    // ---- ヘルパー ----

    private Transaction makeTransaction(TransactionType type, long amount, String categoryName) {
        Category category = new Category(categoryName, type);
        Transaction t = new Transaction();
        t.setDate(LocalDate.of(2026, 6, 1));
        t.setType(type);
        t.setAmount(amount);
        t.setCategory(category);
        return t;
    }
}
