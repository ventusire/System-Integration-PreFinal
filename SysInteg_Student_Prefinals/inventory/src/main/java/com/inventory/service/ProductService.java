package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        // TODO: return productRepository.findById(id)
        throw new UnsupportedOperationException("TODO 1 — getProductById not implemented yet");
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    public Product saveProduct(Product product) {
        // TODO: return productRepository.save(product)
        throw new UnsupportedOperationException("TODO 2 — saveProduct not implemented yet");
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    public void deleteProduct(Long id) {
        // TODO: call productRepository.deleteById(id)
        throw new UnsupportedOperationException("TODO 3 — deleteProduct not implemented yet");
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
        // TODO: return productRepository.existsBySku(sku)
        throw new UnsupportedOperationException("TODO 7 — isSkuTaken not implemented yet");
    }

    // ── TODO 8 (BONUS) ──────────────────────────────────────────────────────
    // Return the total number of products in the system.
    public long countAll() {
        // TODO: return productRepository.count()
        throw new UnsupportedOperationException("TODO 8 — countAll not implemented yet");
    }
}
