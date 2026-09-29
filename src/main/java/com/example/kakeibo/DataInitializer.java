package com.example.kakeibo;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import com.example.kakeibo.entity.User;
import com.example.kakeibo.repository.CategoryRepository;
import com.example.kakeibo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * アプリ起動時に、初期データ（カテゴリの初期セット・デフォルトログインユーザー）が
 * まだ無ければ自動で登録しておくクラス。
 * {@link CommandLineRunner} を実装すると、Spring Boot起動完了後に自動でrun()が呼ばれる。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepository, UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initCategories();
        initDefaultUser();
    }

    /** 初回起動時のみ、収入・支出の初期カテゴリを登録する。 */
    private void initCategories() {
        if (categoryRepository.count() > 0) {
            return;
        }
        String[] expenseCategories = {"食費", "日用品", "交通費", "趣味・娯楽", "住居費", "通信費", "医療費", "その他支出"};
        String[] incomeCategories = {"給与", "ボーナス", "副業", "その他収入"};

        for (String name : expenseCategories) {
            categoryRepository.save(new Category(name, TransactionType.EXPENSE));
        }
        for (String name : incomeCategories) {
            categoryRepository.save(new Category(name, TransactionType.INCOME));
        }
    }

    /**
     * 初回起動時のみ、ログイン用のデフォルトユーザーを作成する。
     * パスワードは平文のまま保存せず、PasswordEncoder（BCrypt）でハッシュ化して保存する。
     *
     * ユーザー名: admin / パスワード: admin123
     * 本番運用する場合は、初回ログイン後に必ずパスワードを変更すること。
     */
    private void initDefaultUser() {
        if (userRepository.count() > 0) {
            return;
        }
        User admin = new User("admin", passwordEncoder.encode("admin123"));
        userRepository.save(admin);
    }
}
