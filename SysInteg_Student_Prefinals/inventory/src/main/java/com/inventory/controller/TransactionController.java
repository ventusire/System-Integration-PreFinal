package com.inventory.controller;

import com.inventory.service.ProductService;
import com.inventory.service.StockTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * TransactionController - Handles stock movements (add or remove stock)
 * 
 * WHAT: This controller manages when products are added or removed from inventory
 * WHY: We need to track every time stock goes in or out so we know how much we have
 * HOW: Users submit forms to add/remove stock, and this controller saves those changes
 * 
 * ROUTES (web addresses this controller listens to):
 *   GET  /transactions         - Show list of all stock movements
 *   GET  /transactions/new     - Show form to add or remove stock
 *   POST /transactions/stock-in  - Add stock to a product
 *   POST /transactions/stock-out - Remove stock from a product
 * 
 * IMPORTANT: Both stock-in and stock-out use try/catch blocks because:
 * - The service checks if the operation is valid (e.g., is there enough stock?)
 * - If something goes wrong, it throws an error
 * - We catch that error and show it as a message instead of crashing the app
 */
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired private StockTransactionService transactionService;
    @Autowired private ProductService productService;

    // EXAMPLE METHOD - Display all stock transactions
    // WHAT: Gets all transactions from database and shows them on a list page
    // WHY: Users need to see the history of all stock movements
    // HOW: Calls transactionService to get all transactions, adds them to the page
    //
    // CODE EXPLANATION:
    // model.addAttribute("transactions", transactionService.getAllTransactions())
    //   → Gets all transactions from database and puts them in the page so HTML can display them
    // return "transactions/list" → Shows the transactions/list.html page
    @GetMapping
    public String list(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions/list";
    }

    // TODO 1 - Show form for adding or removing stock
    // WHAT: Displays a form where users can choose a product and enter quantity
    // WHY: User needs a form to tell us which product and how much to add/remove
    // HOW: Gets all products from database and puts them in a dropdown list
    //
    // CODE EXPLANATION:
    // model.addAttribute("products", productService.getAllProducts())
    //   → Gets all products from database and adds them to the page
    //   → The HTML form will use these products to create a dropdown menu for user to pick from
    // return "transactions/form" → Shows the transactions/form.html page with the form
    @GetMapping("/new")
    public String newForm(Model model) {
        // Get all products and pass them to the form so user can pick one from dropdown
        model.addAttribute("products", productService.getAllProducts());
        return "transactions/form";
    }

    // TODO 2 - Add stock to a product
    // WHAT: Increases the quantity of a product in inventory
    // WHY: When we receive new products from suppliers, we need to add them to stock
    // HOW: Validates the operation → saves it to database → shows success/error message
    //
    // CODE EXPLANATION:
    // @RequestParam extracts productId, quantity, and reason from the form submission
    // try { } → Attempts the operation, if it fails we catch the error
    // transactionService.addStock(...) → Calls service to add stock to database
    // flash.addFlashAttribute("message", ...) → Stores success message to show on next page
    // catch(RuntimeException e) → If service throws an error, we catch it here
    // flash.addFlashAttribute("error", e.getMessage()) → Shows error message instead of crashing
    // return "redirect:/transactions" → After saving, go back to transactions list
    @PostMapping("/stock-in")
    public String stockIn(@RequestParam Long productId,
                          @RequestParam int quantity,
                          @RequestParam(required = false) String reason,
                          RedirectAttributes flash) {
        try {
            // Try to add stock to the product
            transactionService.addStock(productId, quantity, reason);
            // If it worked, show success message
            flash.addFlashAttribute("message", quantity + " units added to stock.");
        } catch (RuntimeException e) {
            // If something went wrong (e.g., product not found), show the error message
            // This prevents the app from crashing and tells user what happened
            flash.addFlashAttribute("error", e.getMessage());
        }
        // Go back to the transactions list page
        return "redirect:/transactions";
    }

    // TODO 3 - Remove stock from a product
    // WHAT: Decreases the quantity of a product in inventory
    // WHY: When products are sold or used, we need to remove them from stock
    // HOW: Checks if enough stock exists → removes it from database → shows message
    //      (If not enough stock, shows error instead)
    //
    // CODE EXPLANATION:
    // Same structure as stock-in but calls removeStock instead:
    // transactionService.removeStock(...) → Service checks: "Is there enough stock to remove?"
    //   If not enough → throws error → we catch it → show error message
    //   If enough → removes from database → show success message
    // The try/catch prevents the app from crashing if there's not enough stock
    @PostMapping("/stock-out")
    public String stockOut(@RequestParam Long productId,
                           @RequestParam int quantity,
                           @RequestParam(required = false) String reason,
                           RedirectAttributes flash) {
        try {
            // Try to remove stock from the product
            // The service checks if there's enough stock before removing
            transactionService.removeStock(productId, quantity, reason);
            // If it worked, show success message
            flash.addFlashAttribute("message", quantity + " units removed from stock.");
        } catch (RuntimeException e) {
            // If something went wrong (e.g., not enough stock), show the error message
            // This prevents the app from crashing and tells user what the problem is
            flash.addFlashAttribute("error", e.getMessage());
        }
        // Go back to the transactions list page
        return "redirect:/transactions";
    }
}
