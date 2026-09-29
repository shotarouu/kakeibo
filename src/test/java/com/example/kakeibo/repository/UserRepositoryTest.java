package com.example.kakeibo.repository;

import com.example.kakeibo.entity.User;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository の統合テスト（DBUnit使用）。
 * findByUsername() メソッドを主な検証対象とする。
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            IDatabaseConnection dbConn = new DatabaseConnection(conn, "PUBLIC");
            IDataSet dataset = new FlatXmlDataSetBuilder()
                    .build(getClass().getResourceAsStream("/dbunit/users.xml"));
            DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataset);
        }
    }

    @Test
    void findByUsername_returnsUser_whenExists() {
        Optional<User> result = userRepository.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByUsername_returnsEmpty_whenNotExists() {
        Optional<User> result = userRepository.findByUsername("noone");

        assertThat(result).isEmpty();
    }

    @Test
    void count_returnsNumberOfUsers() {
        assertThat(userRepository.count()).isEqualTo(1L);
    }
}
