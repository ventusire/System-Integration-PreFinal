package com.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockTransactionRepository;

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
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        int newQty = product.getStockQuantity() + quantity;
        productRepository.updateStock(productId, newQty);
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setType(StockTransaction.Type.STOCK_IN);
        tx.setQuantity(quantity);
        tx.setReason(reason);
        tx.setTransactionDate(LocalDateTime.now());
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
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }
        int newQty = product.getStockQuantity() - quantity;
        productRepository.updateStock(productId, newQty);
        StockTransaction tx = new StockTransaction();
        tx.setProduct(product);
        tx.setType(StockTransaction.Type.STOCK_OUT);
        tx.setQuantity(quantity);
        tx.setReason(reason);
        tx.setTransactionDate(LocalDateTime.now());
        return transactionRepository.save(tx);
    }
}
