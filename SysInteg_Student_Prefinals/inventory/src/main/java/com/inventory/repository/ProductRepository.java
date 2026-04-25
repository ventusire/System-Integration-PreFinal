package com.inventory.repository;

import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 3 — ProductRepository                                    │
 * │  Complete every TODO below using JdbcTemplate.                  │
 * │                                                                 │
 * │  TABLE: products                                                │
 * │    id              BIGINT  (PK)                                 │
 * │    name            VARCHAR                                      │
 * │    sku             VARCHAR (unique)                             │
 * │    description     TEXT                                         │
 * │    price           DECIMAL(10,2)                                │
 * │    stock_quantity  INT                                          │
 * │    reorder_level   INT                                          │
 * │    created_at      DATETIME                                     │
 * │    category_id     BIGINT  (FK → categories.id)                 │
 * │    supplier_id     BIGINT  (FK → suppliers.id)                  │
 * │                                                                 │
 * │  NOTE: All SELECT queries should append to BASE_SELECT.         │
 * │  This joins categories and suppliers in one query automatically. │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper — provided for you. It reads all JOIN columns and
     * builds a fully-populated Product with Category and Supplier.
     * Do NOT modify this. Use it in every query below.
     */
    private final RowMapper<Product> rowMapper = (rs, rowNum) -> {
        Product p = new Product();
        p.setId(rs.getLong("p_id"));
        p.setName(rs.getString("p_name"));
        p.setSku(rs.getString("sku"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setReorderLevel(rs.getInt("reorder_level"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) p.setCreatedAt(ts.toLocalDateTime());

        long catId = rs.getLong("cat_id");
        if (!rs.wasNull()) {
            Category cat = new Category();
            cat.setId(catId);
            cat.setName(rs.getString("cat_name"));
            cat.setDescription(rs.getString("cat_desc"));
            p.setCategory(cat);
            p.setCategoryId(catId);
        }

        long supId = rs.getLong("sup_id");
        if (!rs.wasNull()) {
            Supplier sup = new Supplier();
            sup.setId(supId);
            sup.setName(rs.getString("sup_name"));
            p.setSupplier(sup);
            p.setSupplierId(supId);
        }
        return p;
    };

    /**
     * BASE_SELECT — provided for you.
     * This is the base SQL shared by all read queries.
     * Append a WHERE or ORDER BY clause to complete each query.
     *
     * Example:
     *   jdbcTemplate.query(BASE_SELECT + "ORDER BY p.name", rowMapper)
     *   jdbcTemplate.query(BASE_SELECT + "WHERE p.id = ?", rowMapper, id)
     */
    private static final String BASE_SELECT =
        "SELECT p.id AS p_id, p.name AS p_name, p.sku, p.description, p.price, " +
        "       p.stock_quantity, p.reorder_level, p.created_at, " +
        "       c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc, " +
        "       s.id AS sup_id, s.name AS sup_name " +
        "FROM products p " +
        "LEFT JOIN categories c ON p.category_id = c.id " +
        "LEFT JOIN suppliers  s ON p.supplier_id  = s.id ";

    // ── EXAMPLE 1 ───────────────────────────────────────────────────────────
    // Returns all products ordered by name.
    public List<Product> findAll() {
        return jdbcTemplate.query(BASE_SELECT + "ORDER BY p.name", rowMapper);
    }

    // ── EXAMPLE 2 ───────────────────────────────────────────────────────────
    // Finds one product by id. Returns Optional.empty() if not found.
    public Optional<Product> findById(Long id) {
        List<Product> results = jdbcTemplate.query(
            BASE_SELECT + "WHERE p.id = ?", rowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Find a product by its SKU code. Return Optional.empty() if not found.
    public Optional<Product> findBySku(String sku) {
        // TODO: append "WHERE p.sku = ?" to BASE_SELECT
        throw new UnsupportedOperationException("TODO 1 — findBySku not implemented yet");
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Return true if a product with the given SKU already exists.
    // Hint: SELECT COUNT(*) FROM products WHERE sku = ?
    public boolean existsBySku(String sku) {
        // TODO: use jdbcTemplate.queryForObject(sql, Long.class, sku)
        throw new UnsupportedOperationException("TODO 2 — existsBySku not implemented yet");
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // Find all products that belong to the given category.
    public List<Product> findByCategory(Long categoryId) {
        // TODO: append "WHERE p.category_id = ? ORDER BY p.name"
        throw new UnsupportedOperationException("TODO 3 — findByCategory not implemented yet");
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Find all products supplied by the given supplier.
    public List<Product> findBySupplier(Long supplierId) {
        // TODO: append "WHERE p.supplier_id = ? ORDER BY p.name"
        throw new UnsupportedOperationException("TODO 4 — findBySupplier not implemented yet");
    }

    // ── TODO 5 ──────────────────────────────────────────────────────────────
    // Find all products where stock_quantity <= reorder_level (low stock alert).
    public List<Product> findLowStockProducts() {
        return jdbcTemplate.query(BASE_SELECT + "WHERE p.stock_quantity <= p.reorder_level ORDER BY p.stock_quantity", rowMapper);
    }

    // ── TODO 6 ──────────────────────────────────────────────────────────────
    // Find all products where stock_quantity = 0 (completely out of stock).
    public List<Product> findOutOfStock() {
        return jdbcTemplate.query(BASE_SELECT + "WHERE p.stock_quantity = 0 ORDER BY p.name", rowMapper);
    }

    // ── TODO 7 ──────────────────────────────────────────────────────────────
    // Search products by keyword — match against name OR sku (case-insensitive).
    // Hint: LOWER(p.name) LIKE ? OR LOWER(p.sku) LIKE ?
    //       Pass the same "%" + keyword.toLowerCase() + "%" for both ? markers.
    public List<Product> searchByKeyword(String keyword) {
        // TODO: build the LIKE query and pass two arguments
        throw new UnsupportedOperationException("TODO 7 — searchByKeyword not implemented yet");
    }

    // ── TODO 8 ──────────────────────────────────────────────────────────────
    // Count how many products belong to the given category.
    // Hint: SELECT COUNT(*) FROM products WHERE category_id = ?
    public long countByCategory(Long categoryId) {
        // TODO: use jdbcTemplate.queryForObject(sql, Long.class, categoryId)
        throw new UnsupportedOperationException("TODO 8 — countByCategory not implemented yet");
    }

    // ── TODO 9 ──────────────────────────────────────────────────────────────
    // INSERT a new product OR UPDATE an existing one.
    // The INSERT block is provided as an example — complete the UPDATE block.
    public Product save(Product product) {
        if (product.getId() == null) {
            // ── INSERT (provided as example) ─────────────────────────────
            String sql = "INSERT INTO products (name, sku, description, price, stock_quantity, " +
                         "reorder_level, category_id, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, product.getName());
                ps.setString(2, product.getSku());
                ps.setString(3, product.getDescription());
                ps.setBigDecimal(4, product.getPrice());
                ps.setInt(5, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
                ps.setInt(6, product.getReorderLevel() != null ? product.getReorderLevel() : 10);
                if (product.getCategoryId() != null) ps.setLong(7, product.getCategoryId());
                else ps.setNull(7, java.sql.Types.BIGINT);
                if (product.getSupplierId() != null) ps.setLong(8, product.getSupplierId());
                else ps.setNull(8, java.sql.Types.BIGINT);
                return ps;
            }, keyHolder);
            product.setId(keyHolder.getKey().longValue());
        } else {
            // ── TODO: UPDATE ─────────────────────────────────────────────
            // Columns: name, sku, description, price, stock_quantity,
            //          reorder_level, category_id, supplier_id
            // Filter:  WHERE id = ?
            throw new UnsupportedOperationException("TODO 9 — UPDATE in save() not implemented yet");
        }
        return product;
    }

    // ── TODO 10 ─────────────────────────────────────────────────────────────
    // Update ONLY the stock_quantity for a product (called by StockTransactionService).
    // Hint: UPDATE products SET stock_quantity = ? WHERE id = ?
    public void updateStock(Long productId, int newQuantity) {
        // TODO: use jdbcTemplate.update(sql, newQuantity, productId)
        throw new UnsupportedOperationException("TODO 10 — updateStock not implemented yet");
    }

    // ── TODO 11 ─────────────────────────────────────────────────────────────
    // Delete a product by its id.
    public void deleteById(Long id) {
        // TODO: DELETE FROM products WHERE id = ?
        throw new UnsupportedOperationException("TODO 11 — deleteById not implemented yet");
    }

    // ── TODO 12 (BONUS) ─────────────────────────────────────────────────────
    // Return the total count of all products.
    public long count() {
        // TODO: SELECT COUNT(*) FROM products
        throw new UnsupportedOperationException("TODO 12 — count not implemented yet");
    }
}
