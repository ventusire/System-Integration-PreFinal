package com.inventory.service;

import com.inventory.model.Supplier;
import com.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 2 — SupplierService                                      │
 * │  Wire the repository to the controller layer.                   │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Retrieve a single supplier by ID. Returns Optional to safely handle cases
    // where the supplier does not exist, avoiding null pointer exceptions.
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Persist supplier data to the database. Automatically handles both INSERT
    // (new supplier, no ID) and UPDATE (existing supplier with ID) operations.
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // Remove a supplier record from the database by its unique ID.
    // This operation is delegated to the repository layer for safe SQL execution.
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Validate email uniqueness by checking if it already exists in the database.
    // Returns true if the email is taken, false if available. Used during
    // supplier creation/edit to enforce email constraints and prevent duplicates.
    public boolean isEmailTaken(String email) {
        return supplierRepository.existsByEmail(email);
    }
}
