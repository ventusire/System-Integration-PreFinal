package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
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
 * │  GROUP 3 — ProductController                                    │
 * │                                                                 │
 * │  ROUTES                                                         │
 * │    GET  /products              → list (supports ?search=keyword)│
 * │    GET  /products/{id}         → product detail page            │
 * │    GET  /products/new          → blank form                     │
 * │    GET  /products/edit/{id}    → pre-filled form                │
 * │    POST /products/save         → save product                   │
 * │    POST /products/delete/{id}  → delete product                 │
 * │                                                                 │
 * │  NOTE: The form needs "categories" and "suppliers" lists        │
 * │  in the model so the dropdowns can populate.                    │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private SupplierService supplierService;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // GET /products — list all, or search if ?search= is provided.
    @GetMapping
    public String list(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("products", productService.searchProducts(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        return "products/list";
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // GET /products/{id} — load product, add to model, return "products/detail".
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(p -> model.addAttribute("product", p));
        return "products/detail";
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // GET /products/new — blank product form.
    // Remember: also add "categories" and "suppliers" lists to the model!
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/form";
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // GET /products/edit/{id} — pre-filled edit form.
    // Hint: after loading the product, manually set categoryId and supplierId
    //       from p.getCategory().getId() and p.getSupplier().getId()
    //       so the dropdowns pre-select the right option.
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        productService.getProductById(id).ifPresent(p -> {
            model.addAttribute("product", p);
            if (p.getCategory() != null) {
                model.addAttribute("categoryId", p.getCategory().getId());
            }
            if (p.getSupplier() != null) {
                model.addAttribute("supplierId", p.getSupplier().getId());
            }
        });
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/form";
    }

    // ── TODO 4 ──────────────────────────────────────────────────────────────
    // POST /products/save — validate, save, redirect.
    // On error: re-add categories and suppliers to model and return the form.
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Product product, BindingResult result,
                       Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "products/form";
        }
        productService.saveProduct(product);
        flash.addFlashAttribute("message", "Product saved successfully!");
        return "redirect:/products";
    }

    // ── TODO 5 ──────────────────────────────────────────────────────────────
    // POST /products/delete/{id} — delete, flash message, redirect.
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        productService.deleteProduct(id);
        flash.addFlashAttribute("message", "Product deleted successfully!");
        return "redirect:/products";
    }
}
