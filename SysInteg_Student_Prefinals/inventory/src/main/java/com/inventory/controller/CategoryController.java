package com.inventory.controller;

import com.inventory.model.Category;
import com.inventory.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 1 — CategoryController                                   │
 * │                                                                 │
 * │  Maps HTTP requests → service calls → Thymeleaf template.       │
 * │                                                                 │
 * │  ROUTES                                                         │
 * │    GET  /categories          → list all categories              │
 * │    GET  /categories/new      → show blank form                  │
 * │    GET  /categories/edit/{id}→ show pre-filled form             │
 * │    POST /categories/save     → save (insert or update)          │
 * │    POST /categories/delete/{id} → delete                        │
 * │                                                                 │
 * │  TEMPLATES (already built — do not modify)                      │
 * │    categories/list.html                                         │
 * │    categories/form.html                                         │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // GET /categories — fetch all categories and pass them to the list template.
    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    // ── New form ────────────────────────────────────────────────────────────
    // GET /categories/new
    // Add a blank Category object to the model (the form needs it for binding),
    // then return the template name "categories/form".
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        return "categories/form";
    }
    

    // ── Edit form ───────────────────────────────────────────────────────────
    // GET /categories/edit/{id}
    // Load the category from the service and put it in the model,
    // then return "categories/form".
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        categoryService.getCategoryById(id).ifPresent(c -> model.addAttribute("category", c));
        return "categories/form";
    }

    // ── Save ────────────────────────────────────────────────────────────────
    // POST /categories/save
    // If validation errors exist → return the form again.
    // Otherwise save and redirect to /categories with a success message.
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Category category, BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        categoryService.saveCategory(category);
        flash.addFlashAttribute("success", "Category saved!");
        return "redirect:/categories";
    }

    // ── Delete ──────────────────────────────────────────────────────────────
    // POST /categories/delete/{id}
    // Delete the category and redirect back to /categories with a message.
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        categoryService.deleteCategory(id);
        flash.addFlashAttribute("success", "Category deleted.");
        return "redirect:/categories";
    }
}
