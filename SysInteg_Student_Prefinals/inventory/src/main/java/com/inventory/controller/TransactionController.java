package com.inventory.controller;

import com.inventory.service.ProductService;
import com.inventory.service.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  GROUP 4 — TransactionController                                │
 * │                                                                 │
 * │  ROUTES                                                         │
 * │    GET  /transactions          → list all transactions          │
 * │    GET  /transactions/new      → stock movement form            │
 * │    POST /transactions/stock-in → add stock to a product         │
 * │    POST /transactions/stock-out→ remove stock from a product    │
 * │                                                                 │
 * │  NOTE: Wrap stock-in and stock-out in try/catch.                │
 * │  The service throws RuntimeException for invalid operations     │
 * │  (e.g. insufficient stock). Use flash.addFlashAttribute to      │
 * │  show the error message in the UI without crashing the app.     │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired private StockTransactionService transactionService;
    @Autowired private ProductService productService;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // GET /transactions — list all transactions.
    @GetMapping
    public String list(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions/list";
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // GET /transactions/new — show the stock movement form.
    // The form needs all products in the model (for the product dropdown).
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "transactions/form";
    }

    // ── TODO 2 ──────────────────────────────────────────────────────────────
    // POST /transactions/stock-in
    // Call transactionService.addStock(productId, quantity, reason).
    // On success → flash "X units added to stock."
    // On failure (RuntimeException) → flash the exception message as "error".
    // Redirect to /transactions either way.
    @PostMapping("/stock-in")
    public String stockIn(@RequestParam Long productId,
                          @RequestParam int quantity,
                          @RequestParam(required = false) String reason,
                          RedirectAttributes flash) {
        try {
            transactionService.addStock(productId, quantity, reason);
            flash.addFlashAttribute("success", quantity + " units added to stock.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // POST /transactions/stock-out
    // Same pattern as stock-in but calls transactionService.removeStock().
    // Remember: removeStock throws if there is insufficient stock — catch it!
    @PostMapping("/stock-out")
    public String stockOut(@RequestParam Long productId,
                           @RequestParam int quantity,
                           @RequestParam(required = false) String reason,
                           RedirectAttributes flash) {
        try {
            transactionService.removeStock(productId, quantity, reason);
            flash.addFlashAttribute("success", quantity + " units removed from stock.");
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }

    // POST /transactions → create new transaction (unified endpoint)
    // Params: productId, quantity, type (STOCK_IN/STOCK_OUT), optional reason
    @PostMapping
    public String create(@RequestParam Long productId,
                         @RequestParam int quantity,
                         @RequestParam String type,
                         @RequestParam(required = false) String reason,
                         RedirectAttributes flash) {
        try {
            if ("STOCK_IN".equalsIgnoreCase(type)) {
                transactionService.addStock(productId, quantity, reason);
                flash.addFlashAttribute("success", quantity + " units added to stock.");
            } else if ("STOCK_OUT".equalsIgnoreCase(type)) {
                transactionService.removeStock(productId, quantity, reason);
                flash.addFlashAttribute("success", quantity + " units removed from stock.");
            } else {
                flash.addFlashAttribute("error", "Invalid transaction type.");
            }
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/transactions";
    }
}
