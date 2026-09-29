package com.example.kakeibo.entity;

/**
 * 取引の種別を表す列挙型。
 * 「収入」か「支出」のどちらかしか取らないことをコード上で保証するために使う。
 */
public enum TransactionType {

    /** 収入（給料・お小遣いなど、お金が増える取引） */
    INCOME("収入"),

    /** 支出（食費・日用品など、お金が減る取引） */
    EXPENSE("支出");

    /** 画面表示用の日本語ラベル（例: "収入", "支出"） */
    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
