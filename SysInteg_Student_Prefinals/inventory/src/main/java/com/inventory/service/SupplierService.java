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
    public Optional<Supplier> getSupplierById(Long id) {
        // Calls the repository to retrieve a supplier matching the given ID
        return supplierRepository.findById(id);
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    public Supplier saveSupplier(Supplier supplier) {
        // Calls the repository to save (insert or update) the supplier details and returns the saved entity
        return supplierRepository.save(supplier);
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    public void deleteSupplier(Long id) {
        // Calls the repository to remove the supplier with the specified ID
        supplierRepository.deleteById(id);
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // Return true if the given email is already registered to another supplier.
    public boolean isEmailTaken(String email) {
        // Calls the repository to check if any supplier already exists with this exact email
        return supplierRepository.existsByEmail(email);
    }
}
