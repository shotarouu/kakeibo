package com.example.kakeibo.controller;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.Transaction;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.security.AppUserDetailsService;
import com.example.kakeibo.service.CategoryService;
import com.example.kakeibo.service.MonthlySummary;
import com.example.kakeibo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TransactionController の Web 層テスト。
 * 月パラメータの有無、新規・編集・保存・削除の各エンドポイントを検証する。
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    // ---- GET /transactions（月パラメータあり） ----

    @Test
    @WithMockUser
    void getTransactions_withMonthParam_showsListView() throws Exception {
        when(transactionService.findByMonth(any())).thenReturn(List.of());
        when(transactionService.summarize(any())).thenReturn(new MonthlySummary());

        mockMvc.perform(get("/transactions").param("month", "2026-06"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/list"))
                .andExpect(model().attributeExists("transactions", "summary",
                        "currentMonth", "prevMonth", "nextMonth"));
    }

    // ---- GET /transactions（月パラメータなし → 今月） ----

    @Test
    @WithMockUser
    void getTransactions_withoutMonthParam_usesCurrentMonth() throws Exception {
        when(transactionService.findByMonth(any())).thenReturn(List.of());
        when(transactionService.summarize(any())).thenReturn(new MonthlySummary());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/list"));
    }

    // ---- GET /transactions（空文字パラメータ → 今月） ----

    @Test
    @WithMockUser
    void getTransactions_withBlankMonthParam_usesCurrentMonth() throws Exception {
        when(transactionService.findByMonth(any())).thenReturn(List.of());
        when(transactionService.summarize(any())).thenReturn(new MonthlySummary());

        mockMvc.perform(get("/transactions").param("month", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/list"));
    }

    // ---- GET /transactions/new ----

    @Test
    @WithMockUser
    void getNewForm_returns200AndFormView() throws Exception {
        when(categoryService.findByType(TransactionType.EXPENSE)).thenReturn(List.of());
        when(categoryService.findByType(TransactionType.INCOME)).thenReturn(List.of());

        mockMvc.perform(get("/transactions/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/form"))
                .andExpect(model().attributeExists("transaction",
                        "categories", "incomeCategories", "expenseCategories"));
    }

    // ---- GET /transactions/{id}/edit ----

    @Test
    @WithMockUser
    void getEditForm_returns200AndFormView() throws Exception {
        Transaction t = makeTransaction(1L, TransactionType.EXPENSE, 1000L, "食費");
        when(transactionService.findById(1L)).thenReturn(t);
        when(categoryService.findByType(TransactionType.INCOME)).thenReturn(List.of());
        when(categoryService.findByType(TransactionType.EXPENSE)).thenReturn(List.of());

        mockMvc.perform(get("/transactions/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions/form"))
                .andExpect(model().attributeExists("transaction",
                        "incomeCategories", "expenseCategories"));
    }

    // ---- POST /transactions（保存） ----

    @Test
    @WithMockUser
    void postTransactions_savesAndRedirectsToMonth() throws Exception {
        Category category = new Category("食費", TransactionType.EXPENSE);
        category.setId(1L);
        when(categoryService.findById(1L)).thenReturn(category);

        Transaction saved = makeTransaction(1L, TransactionType.EXPENSE, 1500L, "食費");
        saved.setDate(LocalDate.of(2026, 6, 15));
        when(transactionService.save(any())).thenReturn(saved);

        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .param("date", "2026-06-15")
                        .param("type", "EXPENSE")
                        .param("amount", "1500")
                        .param("memo", "ランチ")
                        .param("categoryId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/transactions?month=*"));

        verify(transactionService).save(any());
    }

    // ---- POST /transactions/{id}/delete ----

    @Test
    @WithMockUser
    void deleteTransaction_deletesAndRedirects() throws Exception {
        mockMvc.perform(post("/transactions/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));

        verify(transactionService).delete(1L);
    }

    // ---- ヘルパー ----

    private Transaction makeTransaction(Long id, TransactionType type, long amount, String categoryName) {
        Category category = new Category(categoryName, type);
        category.setId(1L);
        Transaction t = new Transaction();
        t.setId(id);
        t.setDate(LocalDate.of(2026, 6, 1));
        t.setType(type);
        t.setAmount(amount);
        t.setCategory(category);
        return t;
    }
}
