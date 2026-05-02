package com.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inventory.service.CategoryService;
import com.inventory.service.ProductService;
import com.inventory.service.StockTransactionService;
import com.inventory.service.SupplierService;

/**
 * ┌─────────────────────────────────────────────────────────────────┐
 * │  ALL GROUPS — HomeController (Dashboard)                        │
 * │                                                                 │
 * │  This controller is shared. Complete it together or assign to   │
 * │  one group. It populates the dashboard with summary counts      │
 * │  and the recent-transactions list.                              │
 * │                                                                 │
 * │  MODEL ATTRIBUTES EXPECTED BY index.html                        │
 * │    totalProducts   → productService.getAllProducts().size()      │
 * │    totalCategories → categoryService.getAllCategories().size()   │
 * │    totalSuppliers  → supplierService.getAllSuppliers().size()    │
 * │    lowStockCount   → productService.getLowStockProducts().size() │
 * │    outOfStockCount → productService.getOutOfStockProducts().size()│
 * │    recentTx        → transactionService.getRecentTransactions() │
 * │    lowStockItems   → productService.getLowStockProducts()        │
 * └─────────────────────────────────────────────────────────────────┘
 */
@Controller
public class HomeController {

    // Spring injects these services so the controller can collect dashboard data
    // without directly handling database queries or business rules.
    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private SupplierService supplierService;
    @Autowired private StockTransactionService transactionService;

    // ── TODO ────────────────────────────────────────────────────────────────
    // GET / — populate all model attributes listed above and return "index".
    // ── README ACTIVITY — WHAT'S STILL NEEDED FOR THIS FILE ─────────────────
    // Per README §"Group Assignments", HomeController is the shared dashboard
    // and is treated as a BONUS that all groups contribute to once their own
    // service layers compile. The dashboard method itself is done, but it
    // depends on every group's service being implemented:
    //   • Group 1 (Categories)         → categoryService.getAllCategories()
    //   • Group 2 (Suppliers)          → supplierService.getAllSuppliers()
    //   • Group 3 (Products)           → productService.getAllProducts(),
    //                                    getLowStockProducts(),
    //                                    getOutOfStockProducts()
    //   • Group 4 (Stock Transactions) → transactionService.getRecentTransactions()
    // If any of those still throws UnsupportedOperationException, the dashboard
    // will 500 again. No further code changes are required in this controller.
    // ────────────────────────────────────────────────────────────────────────

    // RECENT FIX (commit c293b42 — "fix dashboard issue"):
    // This method previously threw UnsupportedOperationException, which caused a
    // 500 error on the home page. It now wires up the dashboard view by pushing
    // all 7 attributes that index.html expects: total counts (products, categories,
    // suppliers), stock summary counts (low stock, out of stock), and the two
    // list previews (recent transactions, low stock items).
    //
    // @GetMapping("/") means Spring runs this method when the user opens the
    // home URL of the inventory system.
    @GetMapping("/")
    public String dashboard(Model model) {

        // The Model object is a container for values that index.html can display.
        // Each addAttribute call creates a named value for the Thymeleaf template.

        // These totals are shown as summary cards on the dashboard.
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalCategories", categoryService.getAllCategories().size());
        model.addAttribute("totalSuppliers", supplierService.getAllSuppliers().size());

        // These stock counts help the dashboard warn users about inventory issues.
        model.addAttribute("lowStockCount", productService.getLowStockProducts().size());
        model.addAttribute("outOfStockCount", productService.getOutOfStockProducts().size());

        // These lists provide preview sections for recent stock activity and items
        // that need attention because their stock is already low.
        model.addAttribute("recentTx", transactionService.getRecentTransactions());
        model.addAttribute("lowStockItems", productService.getLowStockProducts());

        // Returning "index" tells Spring to render the index.html view.
        return "index"; // this loads index.html
    }
}
