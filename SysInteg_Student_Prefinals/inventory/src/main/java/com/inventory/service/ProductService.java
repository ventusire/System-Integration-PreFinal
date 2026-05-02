package com.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;

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
        // TODO: return productRepository.searchByKeyword(keyword)
        throw new UnsupportedOperationException("TODO 4 — searchProducts not implemented yet");
    }

    // ── TODO 5 ──────────────────────────────────────────────────────────────
    // Return all products that are at or below their reorder level.
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    // ── TODO 6 ──────────────────────────────────────────────────────────────
    // Return all products with zero stock.
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStock();
    }

    // ── TODO 7 ──────────────────────────────────────────────────────────────
    // Return true if the given SKU is already used by another product.
    public boolean isSkuTaken(String sku) {
        return productRepository.existsBySku(sku);
    }

    /**
     * Returns true if the given SKU is already used by a product OTHER than
     * the one with excludeId.  Pass excludeId = null when creating a new product.
     */
    public boolean isSkuTakenByAnother(String sku, Long excludeId) {
        return productRepository.findBySku(sku)
                .map(existing -> !existing.getId().equals(excludeId))
                .orElse(false);
    }

    // ── TODO 8 (BONUS) ──────────────────────────────────────────────────────
    // Return the total number of products in the system.
    public long countAll() {
        // TODO: return productRepository.count()
        throw new UnsupportedOperationException("TODO 8 — countAll not implemented yet");
    }
}
