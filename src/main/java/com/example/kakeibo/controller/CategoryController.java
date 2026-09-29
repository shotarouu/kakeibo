package com.example.kakeibo.controller;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 「カテゴリ管理」画面の入出力を担当するコントローラー。
 *
 * URLとメソッドの対応：
 *  - GET  /categories            カテゴリ一覧 + 新規登録フォーム
 *  - POST /categories            カテゴリ登録
 *  - POST /categories/{id}/delete カテゴリ削除
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** カテゴリ一覧を表示する。同じ画面に新規登録用の空フォームも一緒に表示する。 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("category", new Category());
        model.addAttribute("types", TransactionType.values());
        return "categories/list";
    }

    /** フォームから送信された新しいカテゴリを保存する。 */
    @PostMapping
    public String save(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/categories";
    }

    /** カテゴリを削除する。 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
