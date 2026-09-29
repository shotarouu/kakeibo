package com.example.kakeibo.repository;

import com.example.kakeibo.entity.Category;
import com.example.kakeibo.entity.TransactionType;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CategoryRepository の統合テスト（DBUnit使用）。
 * H2インメモリDBにXMLデータセットを投入し、クエリメソッドを検証する。
 *
 * @Transactional(NOT_SUPPORTED) → DBUnitの操作が同一トランザクションに巻き込まれず
 * クリーンな状態でデータが見えることを保証するため。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CategoryRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CategoryRepository categoryRepository;

    /** 各テスト前にDBUnitでカテゴリデータをCLEAN_INSERT（既存データ削除→投入） */
    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // H2ではスキーマを"PUBLIC"に明示してAmbiguousTableNameExceptionを防ぐ
            IDatabaseConnection dbConn = new DatabaseConnection(conn, "PUBLIC");
            IDataSet dataset = new FlatXmlDataSetBuilder()
                    .build(getClass().getResourceAsStream("/dbunit/categories.xml"));
            DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataset);
        }
    }

    @Test
    void findByType_expense_returnsOnlyExpenseCategories() {
        List<Category> result = categoryRepository.findByType(TransactionType.EXPENSE);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.getType() == TransactionType.EXPENSE);
        assertThat(result).extracting(Category::getName)
                .containsExactlyInAnyOrder("食費", "日用品");
    }

    @Test
    void findByType_income_returnsOnlyIncomeCategories() {
        List<Category> result = categoryRepository.findByType(TransactionType.INCOME);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(c -> c.getType() == TransactionType.INCOME);
        assertThat(result).extracting(Category::getName)
                .containsExactlyInAnyOrder("給与", "ボーナス");
    }

    @Test
    void existsByName_returnsTrue_whenCategoryExists() {
        assertThat(categoryRepository.existsByName("食費")).isTrue();
    }

    @Test
    void existsByName_returnsFalse_whenCategoryNotExists() {
        assertThat(categoryRepository.existsByName("存在しないカテゴリ")).isFalse();
    }

    @Test
    void deleteById_removesCategory() {
        // 先に ID を取得してから削除する
        Category food = categoryRepository.findByType(TransactionType.EXPENSE)
                .stream().filter(c -> c.getName().equals("食費")).findFirst().orElseThrow();
        categoryRepository.deleteById(food.getId());

        assertThat(categoryRepository.existsByName("食費")).isFalse();
    }
}
