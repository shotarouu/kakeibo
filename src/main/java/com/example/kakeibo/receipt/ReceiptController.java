package com.example.kakeibo.receipt;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptAnalysisService receiptAnalysisService;
    private final CategoryService categoryService;

    public ReceiptController(ReceiptAnalysisService receiptAnalysisService,
                             CategoryService categoryService) {
        this.receiptAnalysisService = receiptAnalysisService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String uploadForm() {
        return "receipts/upload";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "ファイルを選択してください。");
            return "receipts/upload";
        }

        ReceiptAnalysisResult result;
        try {
            result = receiptAnalysisService.analyze(file);
        } catch (Exception e) {
            model.addAttribute("error", "レシートの解析に失敗しました: " + e.getMessage());
            return "receipts/upload";
        }

        List<Category> expenseCategories = categoryService.findByType(TransactionType.EXPENSE);

        // 提案カテゴリ名と一致するカテゴリIDを探す
        Long suggestedCategoryId = expenseCategories.stream()
                .filter(c -> c.getName().equals(result.getSuggestedCategoryName()))
                .map(Category::getId)
                .findFirst()
                .orElse(expenseCategories.isEmpty() ? null : expenseCategories.get(0).getId());

        model.addAttribute("result", result);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("suggestedCategoryId", suggestedCategoryId);
        return "receipts/confirm";
    }
}
