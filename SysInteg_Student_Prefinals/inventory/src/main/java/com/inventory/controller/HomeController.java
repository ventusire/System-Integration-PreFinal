package com.inventory.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.model.StockTransaction;
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

    @GetMapping("/")
    public String dashboard(Model model) {
        List<Product>  allProducts = productService.getAllProducts();
        List<Category> categories  = categoryService.getAllCategories();
        List<Product>  lowStock    = productService.getLowStockProducts();
        List<Product>  outOfStock  = productService.getOutOfStockProducts();

        model.addAttribute("totalProducts",   allProducts.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("totalSuppliers",  supplierService.getAllSuppliers().size());
        model.addAttribute("lowStockCount",   lowStock.size());
        model.addAttribute("outOfStockCount", outOfStock.size());
        model.addAttribute("recentTx",        transactionService.getRecentTransactions());
        model.addAttribute("lowStockItems",   lowStock);

        // ── Category pie chart ─────────────────────────────────────────────
        Map<Long, Long> countByCatId = allProducts.stream()
            .filter(p -> p.getCategory() != null)
            .collect(Collectors.groupingBy(p -> p.getCategory().getId(), Collectors.counting()));

        List<String> catLabels = new ArrayList<>();
        List<Long>   catCounts = new ArrayList<>();
        for (Category c : categories) {
            catLabels.add(c.getName());
            catCounts.add(countByCatId.getOrDefault(c.getId(), 0L));
        }
        model.addAttribute("categoryLabels", catLabels);
        model.addAttribute("categoryCounts", catCounts);

        // ── 7-day movement trend chart ─────────────────────────────────────
        LocalDateTime now     = LocalDateTime.now();
        LocalDateTime weekAgo = LocalDate.now().minusDays(6).atStartOfDay();
        List<StockTransaction> weekTx = transactionService.getTransactionsByDateRange(weekAgo, now);

        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
        List<String>  trendLabels = new ArrayList<>();
        List<Integer> trendIn     = new ArrayList<>();
        List<Integer> trendOut    = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now().minusDays(i);
            trendLabels.add(day.format(dayFmt));

            int inQty = weekTx.stream()
                .filter(tx -> tx.getTransactionDate().toLocalDate().equals(day)
                           && tx.getType() == StockTransaction.Type.STOCK_IN)
                .mapToInt(StockTransaction::getQuantity).sum();
            int outQty = weekTx.stream()
                .filter(tx -> tx.getTransactionDate().toLocalDate().equals(day)
                           && tx.getType() == StockTransaction.Type.STOCK_OUT)
                .mapToInt(StockTransaction::getQuantity).sum();

            trendIn.add(inQty);
            trendOut.add(outQty);
        }
        model.addAttribute("trendLabels", trendLabels);
        model.addAttribute("trendIn",     trendIn);
        model.addAttribute("trendOut",    trendOut);

        return "index";
    }
}
