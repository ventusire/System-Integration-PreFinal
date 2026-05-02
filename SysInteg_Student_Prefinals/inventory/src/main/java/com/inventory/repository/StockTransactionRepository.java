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

// ── README ACTIVITY — WHAT'S STILL NEEDED FOR THIS FILE ─────────────────────
// Per README §"Group Assignments", this is Group 4's repository. TODO 5
// (findTop10Recent) was finished in commit c293b42, but the remaining items
// still need real SQL via JdbcTemplate:
//   • TODO 6 — sumStockInByProduct(productId): SELECT COALESCE(SUM(quantity),0)
//              FROM stock_transactions WHERE product_id = ? AND type='STOCK_IN'
//              via queryForObject(sql, Integer.class, productId).
//   • TODO 7 (BONUS) — countByProduct(productId): SELECT COUNT(*) FROM
//              stock_transactions WHERE product_id = ?
//   • TODO 8 — save(tx): INSERT into stock_transactions (product_id, type,
//              quantity, reason, transaction_date). Use KeyHolder /
//              GeneratedKeyHolder to capture the generated id, set it on tx,
//              and return tx. Use CategoryRepository.save() as a reference.
// Group 4's addStock / removeStock service methods cannot work until save()
// is implemented here.
// ────────────────────────────────────────────────────────────────────────────

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

    public List<StockTransaction> searchByKeyword(String keyword) {
        String term = "%" + keyword.trim().toLowerCase() + "%";
        String sql = BASE_SELECT +
                "WHERE LOWER(p.name) LIKE ? " +
                "OR LOWER(p.sku) LIKE ? " +
                "OR LOWER(t.type) LIKE ? " +
                "OR LOWER(REPLACE(t.type, '_', ' ')) LIKE ? " +
                "OR LOWER(COALESCE(t.reason, '')) LIKE ? " +
                "ORDER BY t.transaction_date DESC";
        return jdbcTemplate.query(sql, rowMapper, term, term, term, term, term);
    }

   // ── TODO 1 — DONE ───────────────────────────────────────────────────────
   // Find ONE transaction by its id. Return Optional.empty() if not found.
   // Appends "WHERE t.id = ?" to BASE_SELECT and wraps result in Optional.
    public Optional<StockTransaction> findById(Long id) {
    String sql = BASE_SELECT + " WHERE t.id = ?";
    List<StockTransaction> results = jdbcTemplate.query(sql, rowMapper, id);
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
}

    // ── TODO 2 — DONE ───────────────────────────────────────────────────────
    // Find all transactions for a specific product, newest first.
    // Appends "WHERE t.product_id = ? ORDER BY t.transaction_date DESC".
    public List<StockTransaction> findByProduct(Long productId) {
    String sql = BASE_SELECT + " WHERE t.product_id = ? ORDER BY t.transaction_date DESC";
    return jdbcTemplate.query(sql, rowMapper, productId);
}

    // ── TODO 3 — DONE ───────────────────────────────────────────────────────
    // Find all transactions of a given type (STOCK_IN or STOCK_OUT), newest first.
    // type.name() converts enum to "STOCK_IN" / "STOCK_OUT" for SQL binding.
    public List<StockTransaction> findByType(StockTransaction.Type type) {
    String sql = BASE_SELECT + " WHERE t.type = ? ORDER BY t.transaction_date DESC";
    return jdbcTemplate.query(sql, rowMapper, type.name());
}

    // ── TODO 4 — DONE ───────────────────────────────────────────────────────
    // Find all transactions between two dates (inclusive), newest first.
    // LocalDateTime values converted to Timestamp via Timestamp.valueOf() for SQL binding.
    public List<StockTransaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
    String sql = BASE_SELECT + " WHERE t.transaction_date BETWEEN ? AND ? ORDER BY t.transaction_date DESC";
    return jdbcTemplate.query(sql, rowMapper, Timestamp.valueOf(startDate), Timestamp.valueOf(endDate));
}

    // ── TODO 5 — DONE ───────────────────────────────────────────────────────
    // Return only the 10 most recent transactions (dashboard preview).
    // Hint: append "ORDER BY t.transaction_date DESC LIMIT 10"
    // RECENT FIX (commit c293b42): replaced the TODO stub with a real query
    // ordering transactions by date DESC and limiting to 10 rows. This was the
    // root cause of the 500 error — the dashboard called this method and got
    // an UnsupportedOperationException.
    public List<StockTransaction> findTop10Recent() {
        return jdbcTemplate.query(BASE_SELECT + "ORDER BY t.transaction_date DESC LIMIT 10", rowMapper);
    }

    // ── TODO 6 — DONE ───────────────────────────────────────────────────────
    // Calculate the total quantity of STOCK_IN for a specific product.
    // COALESCE ensures 0 is returned instead of null when no STOCK_IN rows exist.
    public int sumStockInByProduct(Long productId) {
    String sql = "SELECT COALESCE(SUM(quantity), 0) FROM stock_transactions WHERE product_id = ? AND type = 'STOCK_IN'";
    Integer result = jdbcTemplate.queryForObject(sql, Integer.class, productId);
    return result != null ? result : 0;
}

    // ── TODO 7 (BONUS) — DONE ───────────────────────────────────────────────
    // Count how many transactions exist for a specific product.
    public long countByProduct(Long productId) {
    String sql = "SELECT COUNT(*) FROM stock_transactions WHERE product_id = ?";
    Long result = jdbcTemplate.queryForObject(sql, Long.class, productId);
    return result != null ? result : 0L;
}

    // ── TODO 8 — DONE ───────────────────────────────────────────────────────
    // INSERT a new transaction record. Uses KeyHolder to capture auto-generated id.
    // Pattern follows CategoryRepository.save().
    // Columns: product_id, type (via type.name()), quantity, reason, transaction_date (via Timestamp.valueOf).
    public StockTransaction save(StockTransaction tx) {
    String sql = "INSERT INTO stock_transactions (product_id, type, quantity, reason, transaction_date) VALUES (?, ?, ?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, tx.getProduct().getId());
        ps.setString(2, tx.getType().name());
        ps.setInt(3, tx.getQuantity());
        ps.setString(4, tx.getReason());
        ps.setTimestamp(5, Timestamp.valueOf(tx.getTransactionDate()));
        return ps;
    }, keyHolder);
    Number generatedId = keyHolder.getKey();
    if (generatedId != null) {
        tx.setId(generatedId.longValue());
    }
    return tx;
}
}
