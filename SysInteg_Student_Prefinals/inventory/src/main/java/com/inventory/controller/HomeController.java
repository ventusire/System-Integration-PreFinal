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

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private SupplierService supplierService;
    @Autowired private StockTransactionService transactionService;

    // GET / — populate all model attributes listed above and return "index".
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalCategories", categoryService.getAllCategories().size());
        model.addAttribute("totalSuppliers", supplierService.getAllSuppliers().size());
        model.addAttribute("lowStockCount", productService.getLowStockProducts().size());
        model.addAttribute("outOfStockCount", productService.getOutOfStockProducts().size());
        model.addAttribute("recentTx", transactionService.getRecentTransactions());
        model.addAttribute("lowStockItems", productService.getLowStockProducts());
        return "index";
    }
}
