# Inventory App â€” TODO Task Index

This file is an index of every `TODO` left in the student starter code, grouped by file. Each entry shows the **line number**, the **method**, and a short description of **what you need to do**.

> **Important:** Do not create tracker tasks from `D:\Codes\System-Integration-PreFinal\SysInteg_Student_Prefinals\inventory\target`. That folder is build output only. All actionable TODOs come from `.java` source files under `D:\Codes\System-Integration-PreFinal\SysInteg_Student_Prefinals\inventory\src\main\java\com\inventory`.

> âœ… = already implemented (commit `c293b42`, dashboard fixes) â€” do **not** redo
> â¬œ = still pending â€” student work

---

## Repositories (data layer â€” `JdbcTemplate` + plain SQL)

### `..\inventory\src\main\java\com\inventory\repository\CategoryRepository.java`
- â¬œ **TODO 1** (line 60) â€” `findById(Long id)`
  *Write `SELECT id, name, description FROM categories WHERE id = ?`. Use `jdbcTemplate.query(sql, rowMapper, id)` and wrap the result in `Optional` (empty if the list is empty).*
- â¬œ **TODO 2** (line 68) â€” `findByName(String name)`
  *Same as TODO 1 but filter `WHERE name = ?`. Return `Optional<Category>`.*
- â¬œ **TODO 3** (line 75) â€” `existsByName(String name)`
  *Run `SELECT COUNT(*) FROM categories WHERE name = ?` with `queryForObject(sql, Long.class, name)`. Return `count > 0`.*
- â¬œ **TODO 4** (line 83) â€” `searchByKeyword(String keyword)`
  *Use `LOWER(name) LIKE LOWER(?)` and pass `"%" + keyword + "%"` as the argument. Return the matching list.*
- â¬œ **TODO 5** (line 91) â€” `save(Category)` â€” UPDATE branch
  *INSERT branch is provided. Inside the `else` block write `UPDATE categories SET name = ?, description = ? WHERE id = ?` and execute via `jdbcTemplate.update(...)`.*
- â¬œ **TODO 6** (line 115) â€” `deleteById(Long id)`
  *Execute `DELETE FROM categories WHERE id = ?` with `jdbcTemplate.update(sql, id)`.*
- â¬œ **TODO 7 (BONUS)** (line 123) â€” `count()`
  *Run `SELECT COUNT(*) FROM categories` with `queryForObject(sql, Long.class)` and return the count.*

### `..\inventory\src\main\java\com\inventory\repository\SupplierRepository.java`
- â¬œ **TODO 1** (line 63) â€” `findById(Long id)`
  *`SELECT â€¦ FROM suppliers WHERE id = ?`. Wrap in `Optional`.*
- â¬œ **TODO 2** (line 70) â€” `findByEmail(String email)`
  *`SELECT â€¦ FROM suppliers WHERE email = ?`. Wrap in `Optional`.*
- â¬œ **TODO 3** (line 77) â€” `existsByEmail(String email)`
  *`SELECT COUNT(*) FROM suppliers WHERE email = ?` and return `count > 0`.*
- â¬œ **TODO 4** (line 84) â€” `searchByKeyword(String keyword)`
  *`LOWER(name) LIKE LOWER(?)` with `"%" + keyword + "%"`.*
- â¬œ **TODO 5** (line 91) â€” `save(Supplier)` â€” UPDATE branch
  *Inside the `else`, run `UPDATE suppliers SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?`.*
- â¬œ **TODO 6** (line 116) â€” `deleteById(Long id)`
  *`DELETE FROM suppliers WHERE id = ?`.*
- â¬œ **TODO 7 (BONUS)** (line 123) â€” `count()`
  *`SELECT COUNT(*) FROM suppliers` â†’ return `Long`.*

### `..\inventory\src\main\java\com\inventory\repository\ProductRepository.java`
- â¬œ **TODO 1** (line 137) â€” `findBySku(String sku)`
  *Append `"WHERE p.sku = ?"` to `BASE_SELECT`, run with `rowMapper`, return `Optional`.*
- â¬œ **TODO 2** (line 144) â€” `existsBySku(String sku)`
  *`SELECT COUNT(*) FROM products WHERE sku = ?` â†’ boolean.*
- â¬œ **TODO 3** (line 152) â€” `findByCategory(Long categoryId)`
  *Append `"WHERE p.category_id = ? ORDER BY p.name"` and pass `categoryId`.*
- â¬œ **TODO 4** (line 159) â€” `findBySupplier(Long supplierId)`
  *Append `"WHERE p.supplier_id = ? ORDER BY p.name"`.*
- âœ… **TODO 5** (line 166) â€” `findLowStockProducts()` â€” already implemented.
- âœ… **TODO 6** (line 175) â€” `findOutOfStock()` â€” already implemented.
- â¬œ **TODO 7** (line 184) â€” `searchByKeyword(String keyword)`
  *`LOWER(p.name) LIKE ? OR LOWER(p.sku) LIKE ?`. Pass the same `"%" + keyword.toLowerCase() + "%"` value twice.*
- â¬œ **TODO 8** (line 193) â€” `countByCategory(Long categoryId)`
  *`SELECT COUNT(*) FROM products WHERE category_id = ?`. Use `queryForObject(sql, Long.class, categoryId)`.*
- â¬œ **TODO 9** (line 201) â€” `save(Product)` â€” UPDATE branch
  *Inside the `else`, write `UPDATE products SET name = ?, sku = ?, description = ?, price = ?, stock_quantity = ?, reorder_level = ?, category_id = ?, supplier_id = ? WHERE id = ?`. Mind nullable category/supplier ids.*
- â¬œ **TODO 10** (line 235) â€” `updateStock(Long productId, int newQuantity)`
  *`UPDATE products SET stock_quantity = ? WHERE id = ?`. Used by `StockTransactionService` â€” must work before stock-in/stock-out.*
- â¬œ **TODO 11** (line 243) â€” `deleteById(Long id)`
  *`DELETE FROM products WHERE id = ?`.*
- â¬œ **TODO 12 (BONUS)** (line 250) â€” `count()`
  *`SELECT COUNT(*) FROM products` â†’ return total.*

### `..\inventory\src\main\java\com\inventory\repository\StockTransactionRepository.java`
- â¬œ **TODO 1** (line 97) â€” `findById(Long id)`
  *Append `"WHERE t.id = ?"` to `BASE_SELECT`, wrap result in `Optional`.*
- â¬œ **TODO 2** (line 104) â€” `findByProduct(Long productId)`
  *Append `"WHERE t.product_id = ? ORDER BY t.transaction_date DESC"`.*
- â¬œ **TODO 3** (line 111) â€” `findByType(StockTransaction.Type type)`
  *Append `"WHERE t.type = ? ORDER BY t.transaction_date DESC"`. Pass `type.name()` (a String like `"STOCK_IN"`).*
- â¬œ **TODO 4** (line 119) â€” `findByDateBetween(LocalDateTime from, LocalDateTime to)`
  *Append `"WHERE t.transaction_date BETWEEN ? AND ? ORDER BY t.transaction_date DESC"`. Convert each `LocalDateTime` to `Timestamp.valueOf(...)`.*
- âœ… **TODO 5** (line 127) â€” `findTop10Recent()` â€” already implemented.
- â¬œ **TODO 6** (line 138) â€” `sumStockInByProduct(Long productId)`
  *`SELECT COALESCE(SUM(quantity), 0) FROM stock_transactions WHERE product_id = ? AND type = 'STOCK_IN'`. Use `queryForObject(sql, Integer.class, productId)`.*
- â¬œ **TODO 7 (BONUS)** (line 147) â€” `countByProduct(Long productId)`
  *`SELECT COUNT(*) FROM stock_transactions WHERE product_id = ?`.*
- â¬œ **TODO 8** (line 154) â€” `save(StockTransaction tx)`
  *INSERT into `stock_transactions (product_id, type, quantity, reason, transaction_date)`. Use `KeyHolder` to capture the generated id, set it on `tx`, return `tx`. Use `Timestamp.valueOf(tx.getTransactionDate())` for the date column. Reference `CategoryRepository.save()` for the pattern.*

---

## Services (business logic â€” most are thin delegations)

### `..\inventory\src\main\java\com\inventory\service\CategoryService.java`
- â¬œ **TODO 1** (line 33) â€” `getCategoryById(Long id)` â€” *return `categoryRepository.findById(id)`.*
- â¬œ **TODO 2** (line 40) â€” `saveCategory(Category)` â€” *return `categoryRepository.save(category)`.*
- â¬œ **TODO 3** (line 47) â€” `deleteCategory(Long id)` â€” *call `categoryRepository.deleteById(id)`.*
- â¬œ **TODO 4** (line 54) â€” `isNameTaken(String name)` â€” *return `categoryRepository.existsByName(name)`.*

### `..\inventory\src\main\java\com\inventory\service\SupplierService.java`
- â¬œ **TODO 1** (line 28) â€” `getSupplierById(Long id)` â€” *return `supplierRepository.findById(id)`.*
- â¬œ **TODO 2** (line 34) â€” `saveSupplier(Supplier)` â€” *return `supplierRepository.save(supplier)`.*
- â¬œ **TODO 3** (line 40) â€” `deleteSupplier(Long id)` â€” *call `supplierRepository.deleteById(id)`.*
- â¬œ **TODO 4** (line 46) â€” `isEmailTaken(String email)` â€” *return `supplierRepository.existsByEmail(email)`.*

### `..\inventory\src\main\java\com\inventory\service\ProductService.java`
- â¬œ **TODO 1** (line 42) â€” `getProductById(Long id)` â€” *return `productRepository.findById(id)`.*
- â¬œ **TODO 2** (line 48) â€” `saveProduct(Product)` â€” *return `productRepository.save(product)`.*
- â¬œ **TODO 3** (line 54) â€” `deleteProduct(Long id)` â€” *call `productRepository.deleteById(id)`.*
- â¬œ **TODO 4** (line 60) â€” `searchProducts(String keyword)` â€” *return `productRepository.searchByKeyword(keyword)`.*
- âœ… **TODO 5** (line 67) â€” `getLowStockProducts()` â€” already implemented.
- âœ… **TODO 6** (line 76) â€” `getOutOfStockProducts()` â€” already implemented.
- â¬œ **TODO 7** (line 84) â€” `isSkuTaken(String sku)` â€” *return `productRepository.existsBySku(sku)`.*
- â¬œ **TODO 8 (BONUS)** (line 91) â€” `countAll()` â€” *return `productRepository.count()` (depends on ProductRepository TODO 12).*

### `..\inventory\src\main\java\com\inventory\service\StockTransactionService.java`
- âœ… **TODO 1** (line 62) â€” `getRecentTransactions()` â€” already implemented.
- â¬œ **TODO 2** (line 71) â€” `getTransactionsByProduct(Long productId)` â€” *return `transactionRepository.findByProduct(productId)`.*
- â¬œ **TODO 3** (line 78) â€” `addStock(productId, quantity, reason)` `@Transactional`
  *Steps:*
  *1. Load the product (`productRepository.findById`) â€” throw `RuntimeException` if missing.*
  *2. `newQty = product.getStockQuantity() + quantity`.*
  *3. Call `productRepository.updateStock(productId, newQty)`.*
  *4. Build a new `StockTransaction` (set product, `type = STOCK_IN`, quantity, reason, `transactionDate = LocalDateTime.now()`) and `transactionRepository.save(tx)`.*
  *5. Return the saved transaction.*
- â¬œ **TODO 4** (line 96) â€” `removeStock(productId, quantity, reason)` `@Transactional`
  *Same as TODO 3 but type = STOCK_OUT, and BEFORE updating: if `product.getStockQuantity() < quantity` throw `RuntimeException("Insufficient stock. Available: " + product.getStockQuantity())`. New qty is `current - quantity`.*

> âš ï¸ Service TODOs 3 and 4 require `ProductRepository` TODO 10 (`updateStock`) and `StockTransactionRepository` TODO 8 (`save`) to be implemented first.

---

## Controllers (web layer â€” Spring MVC views & form handlers)

### `..\inventory\src\main\java\com\inventory\controller\CategoryController.java`
- â¬œ **TODO 1** (line 46) â€” `newForm(Model)` â€” *Add a blank `Category` to the model and return view name `"categories/form"`.*
- â¬œ **TODO 2** (line 57) â€” `editForm(Long id, Model)` â€” *Load the category via the service, put it in the model under key `"category"`, return `"categories/form"`.*
- â¬œ **TODO 3** (line 68) â€” `save(@Valid Category, BindingResult, RedirectAttributes)` â€” *If `result.hasErrors()` return `"categories/form"` so the user sees errors; otherwise call `categoryService.saveCategory(category)`, add a success flash message, and redirect to `/categories`.*
- â¬œ **TODO 4** (line 82) â€” `delete(Long id, RedirectAttributes)` â€” *Call `categoryService.deleteCategory(id)`, add flash message, redirect to `/categories`.*

### `..\inventory\src\main\java\com\inventory\controller\SupplierController.java`
- â¬œ **TODO 1** (line 43) â€” `newForm(Model)` â€” *Add a blank `Supplier` to the model, return `"suppliers/form"`.*
- â¬œ **TODO 2** (line 52) â€” `editForm(Long id, Model)` â€” *Load the supplier and put it in the model, return `"suppliers/form"`.*
- â¬œ **TODO 3** (line 60) â€” `save(@Valid Supplier, BindingResult, RedirectAttributes)` â€” *Re-render form on validation errors; otherwise save, flash success, redirect to `/suppliers`.*
- â¬œ **TODO 4** (line 69) â€” `delete(Long id, RedirectAttributes)` â€” *Delete, flash, redirect to `/suppliers`.*

### `..\inventory\src\main\java\com\inventory\controller\ProductController.java`
- â¬œ **TODO 1** (line 52) â€” `detail(Long id, Model)` â€” *Load product via service, put in model under `"product"`, return `"products/detail"`.*
- â¬œ **TODO 2** (line 61) â€” `newForm(Model)` â€” *Add a blank `Product`, plus the categories and suppliers lists (the form's dropdowns need them). Return `"products/form"`.*
- â¬œ **TODO 3** (line 71) â€” `editForm(Long id, Model)` â€” *Load product, manually copy `p.getCategory().getId()` â†’ `categoryId` and `p.getSupplier().getId()` â†’ `supplierId` so the dropdowns pre-select. Add product, categories, and suppliers to the model. Return `"products/form"`.*
- â¬œ **TODO 4** (line 84) â€” `save(@Valid Product, BindingResult, Model, RedirectAttributes)` â€” *On validation errors **re-add categories and suppliers to the model** (they're not session-scoped) and return the form. Otherwise save, flash success, redirect to `/products`.*
- â¬œ **TODO 5** (line 94) â€” `delete(Long id, RedirectAttributes)` â€” *Delete, flash, redirect to `/products`.*

### `..\inventory\src\main\java\com\inventory\controller\TransactionController.java`
- â¬œ **TODO 1** (line 42) â€” `newForm(Model)` â€” *Add the full product list to the model (the form needs it for the product dropdown). Return `"transactions/form"`.*
- â¬œ **TODO 2** (line 52) â€” `stockIn(productId, quantity, reason, RedirectAttributes)` â€” *Wrap in try/catch. On success: call `transactionService.addStock(...)`, flash `"X units added to stock."`. On `RuntimeException`: flash the exception message under key `"error"`. Redirect to `/transactions` either way.*
- â¬œ **TODO 3** (line 67) â€” `stockOut(productId, quantity, reason, RedirectAttributes)` â€” *Same pattern as stock-in but call `removeStock(...)`. The catch block is critical â€” `removeStock` throws on insufficient stock, and that message must reach the user.*

---

## Suggested implementation order

1. **Repositories first** (`CategoryRepository` â†’ `SupplierRepository` â†’ `ProductRepository` â†’ `StockTransactionRepository`) â€” services depend on them.
2. **Services next** â€” most are thin delegations. Do `StockTransactionService` last because TODO 3/4 need `ProductRepository` TODO 10 and `StockTransactionRepository` TODO 8.
3. **Controllers last** â€” they only need a working service layer.

Run `mvn spring-boot:run` after each group and click through the relevant pages to confirm `UnsupportedOperationException` no longer appears.
