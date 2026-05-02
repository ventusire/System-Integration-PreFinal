package com.inventory.repository;

import com.inventory.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 1 — CategoryRepository                                   │
 * │  Complete every TODO below using JdbcTemplate.                  │
 * │                                                                 │
 * │  TABLE: categories                                              │
 * │    id          BIGINT  (PK, auto-increment)                     │
 * │    name        VARCHAR                                          │
 * │    description VARCHAR                                          │
 * │                                                                 │
 * │  USEFUL JdbcTemplate METHODS                                    │
 * │  ─────────────────────────────────────────────────────────────  │
 * │  jdbcTemplate.query(sql, rowMapper)           → List<T>         │
 * │  jdbcTemplate.query(sql, rowMapper, arg)      → List<T>         │
 * │  jdbcTemplate.queryForObject(sql, Long.class, arg) → Long       │
 * │  jdbcTemplate.update(sql, args...)            → int             │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Repository
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper — converts one row from the ResultSet into a Category object.
     * Already provided for you. Study how it works; your queries will use it.
     */
    private final RowMapper<Category> rowMapper = (rs, rowNum) -> {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        return c;
    };

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // Returns all categories ordered alphabetically.
    // Read this carefully — your TODOs follow the same pattern.
    public List<Category> findAll() {
        String sql = "SELECT id, name, description FROM categories ORDER BY name";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Find ONE category by its id. Return Optional.empty() if not found.
    // Hint: query() returns a List — check if it is empty before wrapping.
    public Optional<Category> findById(Long id) {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";
        List<Category> result = jdbcTemplate.query(sql, rowMapper, id);
        // Wrap the result in Optional; empty if list is empty
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Find ONE category by its exact name. Return Optional.empty() if not found.
    public Optional<Category> findByName(String name) {
        String sql = "SELECT id, name, description FROM categories WHERE name = ?";
        List<Category> result = jdbcTemplate.query(sql, rowMapper, name);
        // Wrap the result in Optional; empty if list is empty
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // Return true if a category with the given name already exists.
    // Hint: SELECT COUNT(*) ... then check if count > 0
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name);
        return count > 0;
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Find all categories whose name contains the keyword (case-insensitive).
    // Hint: use LOWER(name) LIKE LOWER(?) and wrap keyword with %...%
    public List<Category> searchByKeyword(String keyword) {
        String sql = "SELECT id, name, description FROM categories " +
                "WHERE LOWER(name) LIKE LOWER(?) " +
                "OR LOWER(COALESCE(description, '')) LIKE LOWER(?) " +
                "ORDER BY name";
        // Wrap keyword with wildcards for partial matching
        String wildcardKeyword = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, rowMapper, wildcardKeyword, wildcardKeyword);
    }

    // ── TODO 5 ──────────────────────────────────────────────────────────────
    // Save a category: INSERT if id is null, UPDATE if id already exists.
    // The INSERT block is shown as a guide — complete the UPDATE block.
    public Category save(Category category) {
        if (category.getId() == null) {
            // ── INSERT (provided as example) ─────────────────────────────
            String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, category.getName());
                ps.setString(2, category.getDescription());
                return ps;
            }, keyHolder);
            category.setId(keyHolder.getKey().longValue());
        } else {
            // ── TODO: UPDATE ─────────────────────────────────────────────
            // Write the UPDATE SQL and call jdbcTemplate.update(sql, ...)
            // Columns to update: name, description — filter by id
            String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql, category.getName(), category.getDescription(), category.getId());
        }
        return category;
    }

    // ── TODO 6 ──────────────────────────────────────────────────────────────
    // Delete a category by its id.
    // Hint: DELETE FROM categories WHERE id = ?
    public void deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ── TODO 7 (BONUS) ──────────────────────────────────────────────────────
    // Return the total count of all categories.
    // Hint: SELECT COUNT(*) FROM categories
    public long count() {
        String sql = "SELECT COUNT(*) FROM categories";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
