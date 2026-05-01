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
 * │  Repository class for managing Category entities in the database│
 * │                                                                 │
 * │  TABLE: categories                                              │
 * │    id          BIGINT  (PK, auto-increment)                     │
 * │    name        VARCHAR                                          │
 * │    description VARCHAR                                          │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Repository
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper — converts one row from the ResultSet into a Category object.
     */
    private final RowMapper<Category> rowMapper = (rs, rowNum) -> {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        return c;
    };

    // Returns all categories ordered alphabetically.
    public List<Category> findAll() {
        String sql = "SELECT id, name, description FROM categories ORDER BY name";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // Find ONE category by its id. Return Optional.empty() if not found.
    public Optional<Category> findById(Long id) {
        String sql = "SELECT id, name, description FROM categories WHERE id = ?";
        List<Category> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Find ONE category by its exact name. Return Optional.empty() if not found.
    public Optional<Category> findByName(String name) {
        String sql = "SELECT id, name, description FROM categories WHERE name = ?";
        List<Category> results = jdbcTemplate.query(sql, rowMapper, name);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Return true if a category with the given name already exists.
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, name);
        return count != null && count > 0;
    }

    // Find all categories whose name contains the keyword (case-insensitive).
    public List<Category> searchByKeyword(String keyword) {
        String sql = "SELECT id, name, description FROM categories WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        String term = "%" + keyword + "%";
        return jdbcTemplate.query(sql, rowMapper, term);
    }

    // Save a category: INSERT if id is null, UPDATE if id already exists.
    public Category save(Category category) {
        if (category.getId() == null) {
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
            String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql, category.getName(), category.getDescription(), category.getId());
        }
        return category;
    }

    // Delete a category by its id.
    public void deleteById(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Return the total count of all categories.
    public long count() {
        String sql = "SELECT COUNT(*) FROM categories";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
}
