package com.inventory.repository;

import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 4 — StockTransactionRepository                           │
 * │  Complete every TODO below using JdbcTemplate.                  │
 * │                                                                 │
 * │  TABLE: stock_transactions                                      │
 * │    id               BIGINT  (PK)                                │
 * │    product_id       BIGINT  (FK → products.id)                  │
 * │    type             ENUM('STOCK_IN', 'STOCK_OUT')               │
 * │    quantity         INT                                         │
 * │    reason           VARCHAR                                     │
 * │    transaction_date DATETIME                                    │
 * │                                                                 │
 * │  NOTE: Use BASE_SELECT for all read queries (it JOINs products).│
 * │  For INSERT use the products.id from tx.getProduct().getId().   │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Repository
public class StockTransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper — provided for you.
     * Maps one result row → StockTransaction with embedded Product.
     */
    private final RowMapper<StockTransaction> rowMapper = (rs, rowNum) -> {
        StockTransaction tx = new StockTransaction();
        tx.setId(rs.getLong("tx_id"));
        tx.setType(StockTransaction.Type.valueOf(rs.getString("type")));
        tx.setQuantity(rs.getInt("quantity"));
        tx.setReason(rs.getString("reason"));
        Timestamp ts = rs.getTimestamp("transaction_date");
        if (ts != null) tx.setTransactionDate(ts.toLocalDateTime());

        Product p = new Product();
        p.setId(rs.getLong("p_id"));
        p.setName(rs.getString("p_name"));
        p.setSku(rs.getString("p_sku"));
        p.setStockQuantity(rs.getInt("p_stock"));
        tx.setProduct(p);
        return tx;
    };

    /**
     * BASE_SELECT — provided for you.
     * Append WHERE / ORDER BY / LIMIT clauses to complete each query.
     */
    private static final String BASE_SELECT =
        "SELECT t.id AS tx_id, t.type, t.quantity, t.reason, t.transaction_date, " +
        "       p.id AS p_id, p.name AS p_name, p.sku AS p_sku, p.stock_quantity AS p_stock " +
        "FROM stock_transactions t " +
        "JOIN products p ON t.product_id = p.id ";

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // Returns ALL transactions, newest first.
    public List<StockTransaction> findAll() {
        return jdbcTemplate.query(BASE_SELECT + "ORDER BY t.transaction_date DESC", rowMapper);
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Find ONE transaction by its id. Return Optional.empty() if not found.
    public Optional<StockTransaction> findById(Long id) {
        // TODO: append "WHERE t.id = ?" and wrap result in Optional
        throw new UnsupportedOperationException("TODO 1 — findById not implemented yet");
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Find all transactions for a specific product, newest first.
    public List<StockTransaction> findByProduct(Long productId) {
        // TODO: append "WHERE t.product_id = ? ORDER BY t.transaction_date DESC"
        throw new UnsupportedOperationException("TODO 2 — findByProduct not implemented yet");
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // Find all transactions of a given type (STOCK_IN or STOCK_OUT), newest first.
    // Hint: type is an enum — pass type.name() as the SQL argument (gives "STOCK_IN" string)
    public List<StockTransaction> findByType(StockTransaction.Type type) {
        // TODO: append "WHERE t.type = ? ORDER BY t.transaction_date DESC"
        throw new UnsupportedOperationException("TODO 3 — findByType not implemented yet");
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Find all transactions between two dates (inclusive), newest first.
    // Hint: convert LocalDateTime → Timestamp using Timestamp.valueOf(localDateTime)
    public List<StockTransaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: append "WHERE t.transaction_date BETWEEN ? AND ? ORDER BY t.transaction_date DESC"
        throw new UnsupportedOperationException("TODO 4 — findByDateBetween not implemented yet");
    }

    // ── TODO 5 ──────────────────────────────────────────────────────────────
    // Return only the 10 most recent transactions (dashboard preview).
    // Hint: append "ORDER BY t.transaction_date DESC LIMIT 10"
    public List<StockTransaction> findTop10Recent() {
        return jdbcTemplate.query(BASE_SELECT + "ORDER BY t.transaction_date DESC LIMIT 10", rowMapper);
    }

    // ── TODO 6 ──────────────────────────────────────────────────────────────
    // Calculate the total quantity of STOCK_IN for a specific product.
    // Hint: SELECT COALESCE(SUM(quantity), 0) FROM stock_transactions
    //       WHERE product_id = ? AND type = 'STOCK_IN'
    public int sumStockInByProduct(Long productId) {
        // TODO: use jdbcTemplate.queryForObject(sql, Integer.class, productId)
        throw new UnsupportedOperationException("TODO 6 — sumStockInByProduct not implemented yet");
    }

    // ── TODO 7 (BONUS) ──────────────────────────────────────────────────────
    // Count how many transactions exist for a specific product.
    public long countByProduct(Long productId) {
        // TODO: SELECT COUNT(*) FROM stock_transactions WHERE product_id = ?
        throw new UnsupportedOperationException("TODO 7 — countByProduct not implemented yet");
    }

    // ── TODO 8 ──────────────────────────────────────────────────────────────
    // INSERT a new transaction record. The save pattern is the same as in
    // other repositories — study CategoryRepository.save() as a reference.
    //
    // Columns to insert:
    //   product_id       → tx.getProduct().getId()
    //   type             → tx.getType().name()
    //   quantity         → tx.getQuantity()
    //   reason           → tx.getReason()
    //   transaction_date → Timestamp.valueOf(tx.getTransactionDate())
    public StockTransaction save(StockTransaction tx) {
        // TODO: write INSERT SQL, use KeyHolder to get generated id,
        //       set tx.setId(...) and return tx
        throw new UnsupportedOperationException("TODO 8 — save not implemented yet");
    }
}
