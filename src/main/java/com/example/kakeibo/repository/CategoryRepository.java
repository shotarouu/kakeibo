package com.example.kakeibo.repository;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * カテゴリ(Category)のDBアクセスを担当するリポジトリ。
 *
 * {@link JpaRepository} を継承するだけで、findAll/save/deleteById などの
 * 基本的なCRUDメソッドがSpring Data JPAによって自動実装される。
 * メソッド名から自動でSQLを生成してくれる「クエリメソッド」機能も使っている。
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** 指定した種別（収入/支出）のカテゴリだけを取得する。 */
    List<Category> findByType(TransactionType type);

    /** 同名のカテゴリが既に存在するかどうかを確認する。 */
    boolean existsByName(String name);
}
