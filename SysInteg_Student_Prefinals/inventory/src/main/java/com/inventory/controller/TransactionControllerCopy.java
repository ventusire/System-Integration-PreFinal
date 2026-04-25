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
        // TODO: model.addAttribute("products", productService.getAllProducts());
        //       return "transactions/form";
        throw new UnsupportedOperationException("TODO 1 — newForm not implemented yet");
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
        // TODO: wrap in try/catch, call addStock, set flash, redirect
        throw new UnsupportedOperationException("TODO 2 — stockIn not implemented yet");
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
        // TODO: wrap in try/catch, call removeStock, set flash, redirect
        throw new UnsupportedOperationException("TODO 3 — stockOut not implemented yet");
    }
}
