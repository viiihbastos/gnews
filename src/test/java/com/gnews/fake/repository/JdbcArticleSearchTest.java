package com.gnews.fake.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JdbcArticleSearchTest {
    private Connection connection;
    private JdbcArticleSearch search;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID());
        try (var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE news (id INT PRIMARY KEY, title VARCHAR(200))");
            statement.execute("INSERT INTO news VALUES (1, 'Java 25'), (2, 'Spring Boot')");
        }
        search = new JdbcArticleSearch(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void findsOnlyTheExactTitleForOrdinaryInput() throws SQLException {
        assertThat(search.findByTitle("Java 25")).containsExactly("Java 25");
        assertThat(search.findByTitle("Nonexistent")).isEmpty();
    }

    @Test
    void demonstratesIntentionalLabInjectionReturningUnrelatedRows() throws SQLException {
        // Characterizes the deliberate flaw for the FIAP exercise; NOT a security acceptance test.
        assertThat(search.findByTitle("' OR '1'='1"))
            .containsExactly("Java 25", "Spring Boot");
    }

    @Test
    void parameterizedControlTreatsTheSamePayloadAsLiteralText() throws SQLException {
        try (var statement = connection.prepareStatement(
                "SELECT title FROM news WHERE title = ? ORDER BY id")) {
            statement.setString(1, "' OR '1'='1");
            try (var results = statement.executeQuery()) {
                assertThat(results.next()).isFalse();
            }
        }
    }
}
