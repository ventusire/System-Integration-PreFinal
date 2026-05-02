package com.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 1 — CategoryService                                      │
 * │  Wire the repository to the controller layer.                   │
 * │  Each method delegates to CategoryRepository.                   │
 * │                                                                 │
 * │  Pattern: call the matching repository method and return        │
 * │  its result. No extra logic needed unless stated.               │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // Fetch all categories — delegates directly to the repository.
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> searchCategories(String keyword) {
        return categoryRepository.searchByKeyword(keyword);
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Return a single category wrapped in Optional (it may not exist).
    public Optional<Category> getCategoryById(Long id) {
        // Return the category wrapped in Optional; empty if not found.
        return categoryRepository.findById(id);
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Save (insert or update) a category and return the saved object.
    public Category saveCategory(Category category) {
        // Return the saved category with generated ID if inserted.
        return categoryRepository.save(category);
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // Delete a category by id.
    public void deleteCategory(Long id) {
        // Delete the category by ID.
        categoryRepository.deleteById(id);
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Return true if the given category name is already taken.
    public boolean isNameTaken(String name) {
        // Return true if name exists in database.
        return categoryRepository.existsByName(name);
    }
}
