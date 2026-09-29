package com.example.kakeibo.repository;

import com.example.kakeibo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * 取引(Transaction)のDBアクセスを担当するリポジトリ。
 * 基本のCRUDは{@link JpaRepository}が提供し、月次集計などに必要な検索は
 * メソッド名ベースのクエリメソッドで実現している。
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** 指定した日付範囲（start〜end）の取引を、日付の新しい順に取得する。月次の絞り込みに使う。 */
    List<Transaction> findByDateBetweenOrderByDateDesc(LocalDate start, LocalDate end);

    /** 全期間の取引を、日付の新しい順に取得する。 */
    List<Transaction> findAllByOrderByDateDesc();
}
