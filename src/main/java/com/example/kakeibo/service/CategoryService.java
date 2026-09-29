package com.example.kakeibo.service;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * カテゴリに関する業務処理（サービス層）。
 *
 * Controller（画面の入出力担当）と Repository（DBアクセス担当）の間に立ち、
 * 「カテゴリを取得する」「保存する」といった処理をひとまとめにして提供する。
 * 今のところ単純な委譲が多いが、将来カテゴリ削除時のバリデーションなどを
 * 追加する場合はこのクラスに書く。
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /** SpringがCategoryRepositoryの実装を自動的に注入してくれる（コンストラクタインジェクション）。 */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** 登録されている全カテゴリを取得する（カテゴリ管理画面の一覧表示用）。 */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /** 収入用 or 支出用のカテゴリだけを取得する（取引登録フォームのプルダウン用）。 */
    public List<Category> findByType(TransactionType type) {
        return categoryRepository.findByType(type);
    }

    /** カテゴリを新規登録・更新する。 */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /** カテゴリをIDで削除する。 */
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    /** IDからカテゴリを1件取得する。見つからない場合は例外を投げる。 */
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("カテゴリが見つかりません: " + id));
    }
}
