package com.inventory.service;

import com.inventory.model.Supplier;
import com.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * SupplierService - Handles all supplier data operations
 * 
 * WHAT: This service is the middle layer between the controller and database
 * WHY: We separate business logic from the database to keep code organized
 * HOW: It uses SupplierRepository to read/write supplier data to the database
 */
@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    // EXAMPLE METHOD - Get all suppliers from database
    // WHAT: Returns a list of all suppliers
    // WHY: Needed when showing the supplier list page to users
    // HOW: Calls repository to fetch all suppliers from the database
    //
    // CODE EXPLANATION:
    // supplierRepository.findAll() → asks database: "Give me all supplier records"
    // Returns a complete list of all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // TODO 1 - Find a supplier by their ID
    // WHAT: Returns a supplier if it exists, or returns "not found"
    // WHY: When editing or viewing one supplier, we need to find them by ID
    // HOW: Searches database for the ID. Uses Optional to safely handle if not found
    //      (Optional prevents crashes if the supplier doesn't exist)
    // 
    // CODE EXPLANATION:
    // supplierRepository.findById(id) → asks database: "Do you have a supplier with this ID?"
    // Returns it wrapped in Optional (means: here's the supplier, or here's nothing)
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }   

    // TODO 2 - Save a supplier (new or update existing one)
    // WHAT: Saves supplier data to the database
    // WHY: When user submits the form, we need to store the data
    // HOW: If supplier is new (no ID), it creates a new record.
    //      If supplier exists (has ID), it updates the existing record
    //
    // CODE EXPLANATION:
    // supplierRepository.save(supplier) → sends supplier data to database
    // If it's a new supplier, database creates a new row
    // If supplier already has an ID, database updates that existing row instead
    // Returns the saved supplier back to us
    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // TODO 3 - Delete a supplier from the database
    // WHAT: Removes a supplier permanently
    // WHY: When a supplier is no longer needed, we delete their record
    // HOW: Takes the supplier ID and deletes that record from the database
    //
    // CODE EXPLANATION:
    // supplierRepository.deleteById(id) → asks database: "Delete the supplier with this ID"
    // The supplier record is completely removed from the database
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    // TODO 4 - Check if an email address is already used
    // WHAT: Returns true if email exists, false if it's available
    // WHY: We don't want two suppliers with the same email address
    // HOW: Searches the database to see if this email is already in use.
    //      The controller uses this to show an error if email is taken
    //
    // CODE EXPLANATION:
    // supplierRepository.existsByEmail(email) → searches database: "Is this email already used?"
    // Returns true if email exists (taken), false if email is not found (available)
    public boolean isEmailTaken(String email) {
        return supplierRepository.existsByEmail(email);
    }
}
