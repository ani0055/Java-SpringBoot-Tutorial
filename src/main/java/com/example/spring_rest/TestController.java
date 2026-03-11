package com.example.spring_rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    public TestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/test-db")
    public String testDatabase() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Database Connection Test</h2>");

        try (Connection conn = dataSource.getConnection()) {
            result.append("<p style='color:green'>✅ Connected successfully!</p>");
            result.append("<p>Database: ").append(conn.getCatalog()).append("</p>");
            result.append("<p>URL: ").append(dbUrl).append("</p>");
            result.append("<p>User: ").append(dbUser).append("</p>");

            // Test query
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT version()");
                if (rs.next()) {
                    result.append("<p>PostgreSQL Version: ").append(rs.getString(1)).append("</p>");
                }
            }

        } catch (Exception e) {
            result.append("<p style='color:red'>❌ Connection failed: ").append(e.getMessage()).append("</p>");
        }

        return result.toString();
    }

    @GetMapping("/env-check")
    public String checkEnv() {
        return String.format("""
            <h2>Environment Variables Loaded</h2>
            <ul>
                <li>POSTGRES_HOST: ${POSTGRES_HOST}</li>
                <li>POSTGRES_PORT: ${POSTGRES_PORT}</li>
                <li>POSTGRES_DB: ${POSTGRES_DB}</li>
                <li>POSTGRES_USER: ${POSTGRES_USER}</li>
                <li>POSTGRES_PASSWORD: %s</li>
            </ul>
            """,
                "${POSTGRES_PASSWORD}" != null ? "********" : "not set");
    }
}
