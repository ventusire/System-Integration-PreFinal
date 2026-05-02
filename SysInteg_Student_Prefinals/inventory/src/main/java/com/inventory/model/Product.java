package com.inventory.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Plain Java class — no JPA/Hibernate annotations.
 * Maps to the 'products' table in MySQL.
 */
public class Product {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity = 0;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel = 10;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Populated by JOIN queries in the repository
    private Category category;
    private Supplier supplier;

    // Used for form binding (dropdowns send IDs, not objects)
    @NotNull(message = "Category is required")
    private Long categoryId;
    @NotNull(message = "Supplier is required")
    private Long supplierId;

    public boolean isLowStock() {
        return stockQuantity != null && reorderLevel != null && stockQuantity <= reorderLevel;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public Integer getReorderLevel() { return reorderLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Category getCategory() { return category; }
    public Supplier getSupplier() { return supplier; }
    public Long getCategoryId() { return categoryId; }
    public Long getSupplierId() { return supplierId; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSku(String sku) { this.sku = sku; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setCategory(Category category) { this.category = category; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
}
