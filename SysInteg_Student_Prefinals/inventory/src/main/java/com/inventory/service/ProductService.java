package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// ── README ACTIVITY — WHAT'S STILL NEEDED FOR THIS FILE ─────────────────────
// Per README §"What You Need to Complete" → Service (business logic), this is
// Group 3's service layer. TODOs 5 and 6 were filled in by commit c293b42.
// What is still required here:
//   • TODO 7 — isSkuTaken(sku): return productRepository.existsBySku(sku).
//              ProductController uses this for duplicate-SKU validation when
//              creating/updating products.
//   • TODO 8 (BONUS) — countAll(): return productRepository.count(); depends
//              on ProductRepository TODO 12 being implemented first.
// Note that getLowStockProducts / getOutOfStockProducts only delegate to the
// repository — they will still 500 at runtime if Group 3's repository TODOs
// are not finished.
// ────────────────────────────────────────────────────────────────────────────

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 3 — ProductService                                       │
 * │  Wire the repository to the controller layer.                   │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Search products by keyword (name or SKU).
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    // ── TODO 5 ──────────────────────────────────────────────────────────────
    // Return all products that are at or below their reorder level.
    // RECENT FIX (commit c293b42): service now delegates to the repository
    // instead of throwing UnsupportedOperationException. Used by the dashboard
    // to populate both the "low stock" count and the low-stock items list.
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    // ── TODO 6 ──────────────────────────────────────────────────────────────
    // Return all products with zero stock.
    // RECENT FIX (commit c293b42): now delegates to the repository's
    // findOutOfStock() so the dashboard can display the out-of-stock count.
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStock();
    }

    // ── TODO 7 ──────────────────────────────────────────────────────────────
    // Return true if the given SKU is already used by another product.
    public boolean isSkuTaken(String sku) {
        return productRepository.existsBySku(sku);
    }

    // ── TODO 8 (BONUS) ──────────────────────────────────────────────────────
    // Return the total number of products in the system.
    public long countAll() {
        return productRepository.count();
    }
}
