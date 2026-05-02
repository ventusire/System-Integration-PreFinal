package com.inventory.controller;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 2 — SupplierController                                   │
 * │                                                                 │
 * │  ROUTES                                                         │
 * │    GET  /suppliers              → list all suppliers            │
 * │    GET  /suppliers/new          → blank form                    │
 * │    GET  /suppliers/edit/{id}    → pre-filled form               │
 * │    POST /suppliers/save         → save supplier                 │
 * │    POST /suppliers/delete/{id}  → delete supplier               │
 * │                                                                 │
 * │  TEMPLATES                                                      │
 * │    suppliers/list.html                                          │
 * │    suppliers/form.html                                          │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "suppliers/list";
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // Show blank add-supplier form.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/form";
        
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // Load supplier by id, put in model, show form.
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        model.addAttribute("supplier", supplier);
        return "suppliers/form";
    }

    // ── TODO3 ──────────────────────────────────────────────────────────────
    // Validate, save, redirect with flash message.
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Supplier supplier, BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()){
            return "suppliers/form";
        }
        supplierService.saveSupplier(supplier);
        flash.addFlashAttribute("success","Supplier saved successfully");

        return "redirect:/suppliers";
    }

    // ── TODO 4  ──────────────────────────────────────────────────────────────
    // Delete supplier, redirect with flash message.
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        supplierService.deleteSupplier(id);
        flash.addFlashAttribute("success", "Supplier deleted successfully!");

        return "redirect:/suppliers";
    }
}
