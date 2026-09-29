package com.example.kakeibo.repository;

import com.example.kakeibo.entity.Transaction;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TransactionRepository の統合テスト（DBUnit使用）。
 * 日付範囲検索と全件取得（日付降順）を検証する。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TransactionRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionRepository transactionRepository;

    /** XMLデータセット: categories(2件) + transactions(3件) を投入 */
    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            IDatabaseConnection dbConn = new DatabaseConnection(conn, "PUBLIC");
            IDataSet dataset = new FlatXmlDataSetBuilder()
                    .build(getClass().getResourceAsStream("/dbunit/transactions.xml"));
            DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataset);
        }
    }

    @Test
    void findAllByOrderByDateDesc_returnsAllInDescendingOrder() {
        List<Transaction> result = transactionRepository.findAllByOrderByDateDesc();

        // 3件あり、日付降順 (2026-06-15 → 2026-06-01 → 2026-05-20)
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(result.get(1).getDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(result.get(2).getDate()).isEqualTo(LocalDate.of(2026, 5, 20));
    }

    @Test
    void findByDateBetween_returnsTransactionsInRange() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end   = LocalDate.of(2026, 6, 30);

        List<Transaction> result =
                transactionRepository.findByDateBetweenOrderByDateDesc(start, end);

        // 6月の取引は2件
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(t ->
                !t.getDate().isBefore(start) && !t.getDate().isAfter(end));
    }

    @Test
    void findByDateBetween_returnsEmpty_whenNoTransactionsInRange() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end   = LocalDate.of(2020, 1, 31);

        List<Transaction> result =
                transactionRepository.findByDateBetweenOrderByDateDesc(start, end);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDateBetween_eachTransactionHasCategory() {
        List<Transaction> result = transactionRepository.findByDateBetweenOrderByDateDesc(
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        assertThat(result).allMatch(t -> t.getCategory() != null);
        assertThat(result).allMatch(t -> t.getCategory().getName() != null);
    }
}
