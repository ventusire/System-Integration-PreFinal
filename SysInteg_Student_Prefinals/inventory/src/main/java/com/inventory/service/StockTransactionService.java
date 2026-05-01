package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// ── README ACTIVITY — WHAT'S STILL NEEDED FOR THIS FILE ─────────────────────
// Per README §"What You Need to Complete" → Service (business logic), Group 4
// owns the only service with REAL business logic in the project. TODO 1
// (getRecentTransactions) was finished in commit c293b42; the rest is still
// outstanding:
//   • TODO 2 — getTransactionsByProduct(productId): delegate to
//              transactionRepository.findByProduct(productId).
//   • TODO 3 — addStock(productId, quantity, reason) [@Transactional]:
//              1. Load product (throw RuntimeException if missing).
//              2. newQty = current stock + quantity.
//              3. productRepository.updateStock(productId, newQty).
//              4. Build a StockTransaction (type=STOCK_IN, now()) and save it.
//              5. Return the saved transaction.
//   • TODO 4 — removeStock(productId, quantity, reason) [@Transactional]:
//              1. Load product (throw if missing).
//              2. If product.getStockQuantity() < quantity → throw
//                 RuntimeException "Insufficient stock. Available: " + qty.
//              3. newQty = current stock - quantity.
//              4. productRepository.updateStock(...) and save a STOCK_OUT row.
// These methods depend on ProductRepository.updateStock (TODO 10) and
// StockTransactionRepository.save (TODO 8) being implemented first.
// ────────────────────────────────────────────────────────────────────────────

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 4 — StockTransactionService                              │
 * │                                                                 │
 * │  This service has business logic beyond simple delegation:      │
 * │  addStock()    — increases product stock, records a STOCK_IN    │
 * │  removeStock() — decreases product stock, records a STOCK_OUT   │
 * │                  (must throw if there is not enough stock)       │
 * │                                                                 │
 * │  Both methods are @Transactional — if saving the transaction    │
 * │  fails, the stock update is also rolled back automatically.     │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Service
public class StockTransactionService {

    @Autowired private StockTransactionRepository transactionRepository;
    @Autowired private ProductRepository productRepository;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // Returns all transactions, newest first.
    public List<StockTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Return only the 10 most recent transactions (used on the dashboard).
    // RECENT FIX (commit c293b42): this was the exact method that surfaced the
    // 500 error in the original stack trace. Now delegates to the repository's
    // findTop10Recent() so the dashboard's recent-transactions panel renders.
    public List<StockTransaction> getRecentTransactions() {
        return transactionRepository.findTop10Recent();
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Return all transactions for a specific product.
    public List<StockTransaction> getTransactionsByProduct(Long productId) {
        // WHAT: This method returns every stock transaction (STOCK_IN / STOCK_OUT)
        //       that belongs to a single product, identified by its id.
        // WHY:  The product detail / history page needs to show the full audit
        //       trail for one product — when stock came in, when it went out,
        //       how much, and why. Without this, the per-product history view
        //       would have no data source.
        // HOW:  This is a thin service-layer delegation — there is no extra
        //       business logic here, so we simply forward the productId to the
        //       repository's findByProduct(...) which runs the actual SQL
        //       (SELECT ... FROM stock_transactions WHERE product_id = ?).
        //       The repository handles the JdbcTemplate binding safely so we
        //       just return whatever list it produces (possibly empty).
        return transactionRepository.findByProduct(productId);
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    /**
     * Add stock to a product (STOCK_IN).
     *
     * Steps:
     *   1. Load the product by productId — throw RuntimeException if not found.
     *   2. Calculate newQty = current stockQuantity + quantity.
     *   3. Update the product's stock in the DB (productRepository.updateStock).
     *   4. Create a new StockTransaction, set its fields (product, type=STOCK_IN,
     *      quantity, reason, transactionDate=now), and save it.
     *   5. Return the saved transaction.
     */
    @Transactional
    public StockTransaction addStock(Long productId, int quantity, String reason) {
        // WHAT: This method increases a product's stock by `quantity` and writes
        //       a matching STOCK_IN row to the stock_transactions table so the
        //       change is auditable. It returns the saved transaction (with id).
        // WHY:  The "Add Stock" form on the inventory page needs a single
        //       service call that does BOTH the stock update AND the audit log
        //       atomically — if either step fails, neither should persist.
        //       That's why the method is @Transactional: Spring will roll back
        //       the UPDATE if the INSERT (or anything after it) blows up.
        // HOW:  Step 1 — load the product via productRepository.findById(...).
        //       That returns Optional<Product>; we unwrap it with orElseThrow
        //       so a missing id surfaces as a clear RuntimeException instead
        //       of a NullPointerException later on.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Step 2 — compute the new stock level (current + incoming quantity).
        //          Using the product loaded above keeps things consistent with
        //          what the user is about to see in the audit row.
        int newQty = product.getStockQuantity() + quantity;

        // Step 3 — push the new quantity to the DB. updateStock runs a plain
        //          UPDATE products SET stock_quantity = ? WHERE id = ? via
        //          JdbcTemplate, so the bindings are SQL-injection safe.
        productRepository.updateStock(productId, newQty);

        // Step 4 — build the audit row. type = STOCK_IN (the repository will
        //          store this as enum.name() text in the DB), transactionDate
        //          defaults to now() in the model but we set it explicitly so
        //          the intent is obvious. The full Product is attached so the
        //          repository can persist its FK (product_id).
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setType(StockTransaction.Type.STOCK_IN);
        tx.setQuantity(quantity);
        tx.setReason(reason);
        tx.setTransactionDate(LocalDateTime.now());

        // Step 5 — save and return. The repository uses a KeyHolder under the
        //          hood to fetch the generated id and stamps it back onto the
        //          returned object, so callers can reference the new tx row.
        return transactionRepository.save(tx);
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    /**
     * Remove stock from a product (STOCK_OUT).
     *
     * Steps:
     *   1. Load the product by productId — throw RuntimeException if not found.
     *   2. If product.getStockQuantity() < quantity → throw RuntimeException
     *      with message: "Insufficient stock. Available: " + product.getStockQuantity()
     *   3. Calculate newQty = current stockQuantity - quantity.
     *   4. Update the product's stock in the DB (productRepository.updateStock).
     *   5. Create and save a StockTransaction (type = STOCK_OUT).
     *   6. Return the saved transaction.
     */
    @Transactional
    public StockTransaction removeStock(Long productId, int quantity, String reason) {
        // WHAT: This method decreases a product's stock by `quantity` and writes
        //       a matching STOCK_OUT row to the stock_transactions table. It
        //       refuses to over-draw — if there isn't enough stock on hand it
        //       throws and nothing is persisted.
        // WHY:  Removing stock has a real-world business rule the UI alone
        //       can't be trusted to enforce: you cannot ship more than you
        //       physically have. Centralizing the check here means every
        //       caller (form, API, future job) is protected. @Transactional
        //       guarantees the UPDATE and the audit INSERT live or die together.
        // HOW:  Step 1 — load the product via findById(...). It returns an
        //       Optional<Product> so we unwrap with orElseThrow; a missing
        //       id becomes a clear RuntimeException instead of a NPE later.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Step 2 — guard against over-removal. If the requested quantity is
        //          larger than what's on hand we abort BEFORE touching the DB.
        //          The exception message includes the available stock so the
        //          controller can surface a useful error back to the user.
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // Step 3 — compute the new stock level (current - outgoing quantity).
        int newQty = product.getStockQuantity() - quantity;

        // Step 4 — push the new quantity to the DB. updateStock runs a plain
        //          UPDATE products SET stock_quantity = ? WHERE id = ? via
        //          JdbcTemplate, so the bindings are SQL-injection safe.
        productRepository.updateStock(productId, newQty);

        // Step 5 — build the audit row. type = STOCK_OUT (the repository will
        //          store this as enum.name() text in the DB), transactionDate
        //          is set explicitly so the timestamp matches "now" rather than
        //          object-construction time. The full Product is attached so
        //          the repository can persist its FK (product_id).
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setType(StockTransaction.Type.STOCK_OUT);
        tx.setQuantity(quantity);
        tx.setReason(reason);
        tx.setTransactionDate(LocalDateTime.now());

        // Step 6 — save and return. The repository uses a KeyHolder under the
        //          hood to fetch the generated id and stamps it back onto the
        //          returned object, so callers can reference the new tx row.
        return transactionRepository.save(tx);
    }
}
