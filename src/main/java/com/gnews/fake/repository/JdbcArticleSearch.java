package com.gnews.fake.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Experimental JDBC search adapter; not wired into the HTTP application.
 * The caller owns the connection lifecycle.
 */
public final class JdbcArticleSearch {
    private final Connection connection;

    public JdbcArticleSearch(Connection connection) {
        this.connection = Objects.requireNonNull(connection, "connection");
    }

    public List<String> findByTitle(String userInput) throws SQLException {
        String query = "SELECT title FROM news WHERE title = '" + userInput + "' ORDER BY id";
        List<String> titles = new ArrayList<>();
        try (var statement = connection.createStatement();
             var results = statement.executeQuery(query)) {
            while (results.next()) {
                titles.add(results.getString("title"));
            }
        }
        return titles;
    }
}
