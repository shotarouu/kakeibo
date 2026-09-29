package com.example.kakeibo.controller;

import com.example.kakeibo.entity.Transaction;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.service.CategoryService;
import com.example.kakeibo.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 「取引（収入・支出）」に関する画面の入出力を担当するコントローラー。
 *
 * URLとメソッドの対応：
 *  - GET  /transactions            一覧・月次サマリー画面
 *  - GET  /transactions/new        新規登録フォーム
 *  - GET  /transactions/{id}/edit  編集フォーム
 *  - POST /transactions            登録・更新の確定
 *  - POST /transactions/{id}/delete 削除の確定
 *
 * 画面（Thymeleafテンプレート）とサービス層（業務ロジック）の橋渡し役であり、
 * ここには「画面に何を表示するか」「URLからどう値を受け取るか」だけを書く。
 */
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public TransactionController(TransactionService transactionService, CategoryService categoryService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
    }

    /**
     * 取引一覧 + 月次サマリー画面を表示する。
     * クエリパラメータ ?month=2026-06 で月を指定できる。未指定なら今月を表示する。
     */
    @GetMapping
    public String list(@RequestParam(value = "month", required = false) String monthParam, Model model) {
        YearMonth month = parseMonth(monthParam);
        model.addAttribute("transactions", transactionService.findByMonth(month));
        model.addAttribute("summary", transactionService.summarize(month));
        model.addAttribute("currentMonth", month.toString());
        model.addAttribute("prevMonth", month.minusMonths(1).toString());
        model.addAttribute("nextMonth", month.plusMonths(1).toString());
        return "transactions/list";
    }

    /** 新規取引の登録フォームを表示する。デフォルトで今日の日付・支出種別をセットしておく。 */
    @GetMapping("/new")
    public String newForm(Model model) {
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDate.now());
        transaction.setType(TransactionType.EXPENSE);
        model.addAttribute("transaction", transaction);
        model.addAttribute("categories", categoryService.findByType(TransactionType.EXPENSE));
        model.addAttribute("incomeCategories", categoryService.findByType(TransactionType.INCOME));
        model.addAttribute("expenseCategories", categoryService.findByType(TransactionType.EXPENSE));
        return "transactions/form";
    }

    /** 既存の取引を編集するフォームを表示する。 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("transaction", transactionService.findById(id));
        model.addAttribute("incomeCategories", categoryService.findByType(TransactionType.INCOME));
        model.addAttribute("expenseCategories", categoryService.findByType(TransactionType.EXPENSE));
        return "transactions/form";
    }

    /**
     * フォームから送信された取引を登録・更新する。
     * フォームはカテゴリをID（categoryId）で送ってくるので、ここでCategoryエンティティに変換してから保存する。
     * 保存後は、その取引がある月の一覧画面にリダイレクトする。
     */
    @PostMapping
    public String save(@ModelAttribute Transaction transaction, @RequestParam Long categoryId) {
        transaction.setCategory(categoryService.findById(categoryId));
        transactionService.save(transaction);
        YearMonth month = YearMonth.from(transaction.getDate());
        return "redirect:/transactions?month=" + month;
    }

    /** 取引を削除し、一覧画面にリダイレクトする。 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        transactionService.delete(id);
        return "redirect:/transactions";
    }

    /** "yyyy-MM" 形式の文字列をYearMonthに変換する。未指定（null/空）なら現在の年月を返す。 */
    private YearMonth parseMonth(String monthParam) {
        if (monthParam == null || monthParam.isBlank()) {
            return YearMonth.now();
        }
        return YearMonth.parse(monthParam);
    }
}
