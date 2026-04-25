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
        // TODO: return transactionRepository.findByProduct(productId)
        throw new UnsupportedOperationException("TODO 2 — getTransactionsByProduct not implemented yet");
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
        // TODO: follow the steps above
        throw new UnsupportedOperationException("TODO 3 — addStock not implemented yet");
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
        // TODO: follow the steps above — pay attention to the stock check in step 2
        throw new UnsupportedOperationException("TODO 4 — removeStock not implemented yet");
    }
}
