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

    private static final String PRODUCT_NOT_FOUND = "Product not Found";

    @Autowired private StockTransactionService transactionService;
    @Autowired private ProductService productService;

    // ── EXAMPLE ─────────────────────────────────────────────────────────────
    // GET /transactions — list all transactions.
    @GetMapping
    public String list(Model model, @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("transactions", transactionService.searchTransactions(search));
            model.addAttribute("search", search);
        } else {
            model.addAttribute("transactions", transactionService.getAllTransactions());
        }
        return "transactions/list";
    }

    // ── TODO 1 ──────────────────────────────────────────────────────────────
    // GET /transactions/new — show the stock movement form.
    // The form needs all products in the model (for the product dropdown).
    @GetMapping("/new")
    public String newForm(Model model) {
        // COMPLETED: Delegate to ProductService to fetch all products from database
        // and attach them to the Model so the form template can populate the dropdown.
        // The form will iterate over this list to allow users to select a product.
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
    public String stockIn(@RequestParam(required = false) Long productId,
                          @RequestParam int quantity,
                          @RequestParam(required = false) String reason,
                          RedirectAttributes flash) {
        try {
            if (productId == null) {
                throw new RuntimeException(PRODUCT_NOT_FOUND);
            }

            // COMPLETED: Attempt to add stock via service. The service handles the business
            // logic: creates a stock transaction record and updates product quantity.
            // If product not found or other error occurs, the service throws RuntimeException.
            transactionService.addStock(productId, quantity, reason);
            // SUCCESS: Flash a user-friendly message showing the quantity added.
            // This message will display on the next page via Spring's flash mechanism.
            flash.addFlashAttribute("message", quantity + " units added to stock.");
        } catch (RuntimeException e) {
            // ERROR HANDLING: Catch any exception from the service (e.g., product not found)
            // and flash the error message so the user sees what went wrong without crashing.
            flash.addFlashAttribute("error", e.getMessage());
        }
        // REDIRECT: Always return to the transactions list, whether success or failure.
        // Flash attributes persist across the redirect so messages appear on the next page.
        return "redirect:/transactions";
    }

    // ── TODO 3 ──────────────────────────────────────────────────────────────
    // POST /transactions/stock-out
    // Same pattern as stock-in but calls transactionService.removeStock().
    // Remember: removeStock throws if there is insufficient stock — catch it!
    @PostMapping("/stock-out")
    public String stockOut(@RequestParam(required = false) Long productId,
                           @RequestParam int quantity,
                           @RequestParam(required = false) String reason,
                           RedirectAttributes flash) {
        try {
            if (productId == null) {
                throw new RuntimeException(PRODUCT_NOT_FOUND);
            }

            // COMPLETED: Attempt to remove stock via service. The service validates that
            // the product has sufficient stock before creating a stock-out transaction.
            // If stock is insufficient, the service throws a RuntimeException with a
            // descriptive message (e.g., "Insufficient stock to remove X units").
            transactionService.removeStock(productId, quantity, reason);
            // SUCCESS: Flash a user-friendly message showing the quantity removed.
            // This confirms the operation completed without issues.
            flash.addFlashAttribute("message", quantity + " units removed from stock.");
        } catch (RuntimeException e) {
            // ERROR HANDLING: Catch exceptions from the service. Common cases include:
            // - Insufficient stock (most common for stock-out)
            // - Product not found
            // Flash the error message so the user understands what went wrong.
            flash.addFlashAttribute("error", e.getMessage());
        }
        // REDIRECT: Always return to the transactions list for consistency.
        // The flash attributes carry error/success messages across the redirect.
        return "redirect:/transactions";
    }
}
