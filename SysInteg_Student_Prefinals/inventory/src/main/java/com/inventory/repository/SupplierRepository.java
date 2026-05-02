package com.inventory.repository;

import com.inventory.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 2 — SupplierRepository                                   │
 * │  Complete every TODO below using JdbcTemplate.                  │
 * │                                                                 │
 * │  TABLE: suppliers                                               │
 * │    id      BIGINT  (PK, auto-increment)                         │
 * │    name    VARCHAR                                              │
 * │    email   VARCHAR                                              │
 * │    phone   VARCHAR                                              │
 * │    address VARCHAR                                              │
 * │                                                                 │
 * │  USEFUL JdbcTemplate METHODS                                    │
 * │  ─────────────────────────────────────────────────────────────  │
 * │  jdbcTemplate.query(sql, rowMapper)           → List<T>         │
 * │  jdbcTemplate.query(sql, rowMapper, arg)      → List<T>         │
 * │  jdbcTemplate.queryForObject(sql, Long.class) → Long            │
 * │  jdbcTemplate.update(sql, args...)            → int             │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Repository
public class SupplierRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * RowMapper — converts one ResultSet row → Supplier object.
     * Already provided. Study how it maps column names to setters.
     */
    private final RowMapper<Supplier> rowMapper = (rs, rowNum) -> {
        Supplier s = new Supplier();
        s.setId(rs.getLong("id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        return s;
    };

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // Returns all suppliers ordered A → Z by name.
    public List<Supplier> findAll() {
        String sql = "SELECT id, name, email, phone, address FROM suppliers ORDER BY name ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // ── TODO 1 ───done───────────────────────────────────────────────────────────
    // Find ONE supplier by its id. Return Optional.empty() if not found.
    public Optional<Supplier> findById(Long id) {
        // WHAT: This method looks up a single supplier using its unique id (primary key).
        // WHY:  The SupplierService and SupplierController use this whenever they need to
        //       load one specific supplier — for example, when opening the "edit" form or
        //       before deleting a supplier. Without this, we'd have no way to fetch one record.
        // HOW:  The SQL selects all five columns from the suppliers table and uses WHERE id = ?
        //       to filter down to exactly one row. The "?" is a placeholder — JdbcTemplate
        //       safely replaces it with the actual id value we pass in (this prevents SQL injection).
        String sql = "SELECT id, name, email, phone, address FROM suppliers WHERE id = ?";

        // We use query() instead of queryForObject() because queryForObject() throws an
        // exception if no row is found. query() simply returns an empty list, which is safer.
        // The rowMapper (defined at the top of this class) converts each database row into
        // a Supplier Java object.
        List<Supplier> results = jdbcTemplate.query(sql, rowMapper, id);

        // stream().findFirst() takes the first item from the list and wraps it in an Optional.
        // If the list is empty (no supplier with that id), it returns Optional.empty().
        // Optional is used here so the caller can safely check if a supplier was found
        // without risking a NullPointerException.
        return results.stream().findFirst();
    }

    // ── TODO 2 ─done─────────────────────────────────────────────────────────────
    // Find ONE supplier by their exact email address.
    public Optional<Supplier> findByEmail(String email) {
        // WHAT: This method looks up a single supplier by their exact email address.
        // WHY:  This is useful for checking if a supplier with a specific email already exists
        //       in the database — for example, before creating a new supplier, the controller
        //       can call this to avoid duplicate emails. It works together with existsByEmail()
        //       below, but this one returns the full Supplier object instead of just true/false.
        // HOW:  Same pattern as findById() above — we select all columns but filter by email
        //       instead of id. The "?" placeholder gets safely replaced with the email value.
        String sql = "SELECT id, name, email, phone, address FROM suppliers WHERE email = ?";

        // query() returns a list of matching suppliers. Since emails should be unique,
        // this list will have either 0 or 1 items.
        List<Supplier> results = jdbcTemplate.query(sql, rowMapper, email);

        // Wrap the result in Optional — if no supplier has this email, returns Optional.empty().
        // This lets the caller handle the "not found" case gracefully without null checks.
        return results.stream().findFirst();
    }

    // ── TODO 3 ─done─────────────────────────────────────────────────────────────
    // Return true if a supplier with the given email already exists.
    public boolean existsByEmail(String email) {
        // WHAT: This method checks whether any supplier in the database already uses the given email.
        //       It returns true if found, false if not — a simple yes/no answer.
        // WHY:  The SupplierService uses this to prevent duplicate emails. Before saving a new
        //       supplier (or updating one), we call this to make sure no other supplier already
        //       has the same email. This is simpler than findByEmail() when we just need a
        //       true/false check and don't need the full Supplier object.
        // HOW:  COUNT(*) counts how many rows match the WHERE condition. If the email exists,
        //       the count will be 1 (or more). If it doesn't exist, the count will be 0.
        String sql = "SELECT COUNT(*) FROM suppliers WHERE email = ?";

        // queryForObject() is safe to use here because COUNT(*) always returns exactly one row
        // (the count number itself), so it will never throw an "empty result" exception.
        // We map the result to Long.class to get a number back.
        Long count = jdbcTemplate.queryForObject(sql, Long.class, email);

        // If count is greater than 0, at least one supplier has this email → return true.
        // The null check (count != null) is a safety measure — in practice COUNT(*) never returns null.
        return count != null && count > 0;
    }

    // ── TODO 4 ──done────────────────────────────────────────────────────────────
    // Find all suppliers whose name contains the keyword (case-insensitive).
    public List<Supplier> searchByKeyword(String keyword) {
        // WHAT: This method searches for all suppliers whose name contains the given keyword.
        //       The search is case-insensitive, so "apple" will match "Apple", "APPLE", etc.
        // WHY:  This powers the search bar on the suppliers list page. When a user types a
        //       keyword, the controller calls this method to filter the supplier list.
        //       Unlike findAll() which returns everything, this only returns matching results.
        // HOW:  LOWER() converts both the database column and the search keyword to lowercase
        //       before comparing, so the match ignores upper/lower case differences.
        //       LIKE is a SQL pattern-matching operator — the "%" symbols are wildcards that
        //       match any characters before and after the keyword.
        String sql = "SELECT id, name, email, phone, address FROM suppliers " +
                "WHERE LOWER(name) LIKE LOWER(?) " +
                "OR LOWER(email) LIKE LOWER(?) " +
                "OR LOWER(COALESCE(phone, '')) LIKE LOWER(?) " +
                "OR LOWER(COALESCE(address, '')) LIKE LOWER(?) " +
                "ORDER BY name ASC";

        // We wrap the keyword with "%" on both sides so it matches anywhere in the name.
        // For example, if keyword is "tech", wildcardKeyword becomes "%tech%",
        // which matches "TechCorp", "InfoTech", "MyTechStore", etc.
        String wildcardKeyword = "%" + keyword + "%";

        // query() runs the SQL and uses the rowMapper to convert each matching row into a
        // Supplier object. The result is a List because multiple suppliers can match.
        return jdbcTemplate.query(sql, rowMapper, wildcardKeyword, wildcardKeyword, wildcardKeyword, wildcardKeyword);
    }

    // ── TODO 5 ─done─────────────────────────────────────────────────────────────
    // Save a supplier: INSERT if id is null, UPDATE if id exists.
    // The INSERT block is shown — complete the UPDATE block.
    public Supplier save(Supplier supplier) {
        if (supplier.getId() == null) {
            // ── INSERT (provided as example) ─────────────────────────────
            String sql = "INSERT INTO suppliers (name, email, phone, address) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, supplier.getName());
                ps.setString(2, supplier.getEmail());
                ps.setString(3, supplier.getPhone());
                ps.setString(4, supplier.getAddress());
                return ps;
            }, keyHolder);
            supplier.setId(keyHolder.getKey().longValue());
        } else {
            // WHAT: This is the UPDATE branch — it modifies an existing supplier's data in the database.
            // WHY:  When a user edits a supplier through the form and clicks save, the supplier
            //       object already has an id (because it was loaded from the database). So we
            //       don't need to create a new row — we just update the existing one.
            //       The INSERT branch above handles brand-new suppliers (id is null).
            // HOW:  The SQL updates all four editable columns (name, email, phone, address)
            //       for the row that matches the given id. The five "?" placeholders are filled
            //       in order: 1=name, 2=email, 3=phone, 4=address, 5=id (the WHERE filter).
            //       jdbcTemplate.update() runs the SQL and returns the number of rows changed
            //       (we don't need that number here, so we ignore it).
            String sql = "UPDATE suppliers SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?";
            jdbcTemplate.update(sql, supplier.getName(), supplier.getEmail(),
                    supplier.getPhone(), supplier.getAddress(), supplier.getId());
        }
        return supplier;
    }

    // ── TODO 6 ─done─────────────────────────────────────────────────────────────
    // Delete a supplier by its id.
    public void deleteById(Long id) {
        // WHAT: This method permanently removes a supplier from the database using its id.
        // WHY:  When a user clicks the "Delete" button on the suppliers list page, the controller
        //       calls this method to remove that supplier. It uses the id to make sure only the
        //       correct supplier gets deleted — not any other rows.
        // HOW:  DELETE FROM removes the row where id matches. The "?" is safely replaced with
        //       the actual id value. jdbcTemplate.update() is used for DELETE (and INSERT/UPDATE)
        //       because all of these are "write" operations that change the database.
        //       Note: this method returns void — once the supplier is deleted, there's nothing
        //       to return. If the id doesn't exist, nothing happens (no error is thrown).
        String sql = "DELETE FROM suppliers WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // ── TODO 7 (BONUS) ──done────────────────────────────────────────────────────
    // Return the total count of all suppliers.
    public long count() {
        // WHAT: This method returns the total number of suppliers stored in the database.
        // WHY:  This is useful for displaying stats like "Total Suppliers: 25" on a dashboard
        //       or summary page. It gives a quick count without loading all supplier data.
        // HOW:  COUNT(*) is a SQL function that counts all rows in the table. Unlike the other
        //       methods, this doesn't need a WHERE clause because we want to count everything.
        //       queryForObject() is perfect here because COUNT(*) always returns exactly one
        //       value (the number), and we map it to Long.class to get a Java number.
        String sql = "SELECT COUNT(*) FROM suppliers";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);

        // Safety check: if count is somehow null (very unlikely), return 0 instead of crashing.
        // The "? :" is a ternary operator — it means "if count is not null, use count; otherwise use 0".
        return count != null ? count : 0L;
    }
}
