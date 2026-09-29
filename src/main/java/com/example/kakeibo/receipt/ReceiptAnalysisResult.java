package com.example.kakeibo.receipt;

import java.time.LocalDate;

public class ReceiptAnalysisResult {

    private LocalDate date;
    private Integer amount;
    private String storeName;
    private String suggestedCategoryName;
    private String memo;

    public ReceiptAnalysisResult() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getSuggestedCategoryName() { return suggestedCategoryName; }
    public void setSuggestedCategoryName(String suggestedCategoryName) {
        this.suggestedCategoryName = suggestedCategoryName;
    }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
