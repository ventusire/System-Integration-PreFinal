# Inventory Management System — Full Project Guide

> Written for students with no prior Spring Boot experience.
> This guide explains what every file does, how data moves through the system, and what each layer is responsible for.

---

## Table of Contents

1. [What This System Does](#1-what-this-system-does)
2. [How Spring Boot Works (The Big Picture)](#2-how-spring-boot-works-the-big-picture)
3. [Project Folder Structure](#3-project-folder-structure)
4. [The Four Layers — How They Connect](#4-the-four-layers--how-they-connect)
5. [Models — The Data Blueprints](#5-models--the-data-blueprints)
6. [Repositories — Talking to the Database](#6-repositories--talking-to-the-database)
7. [Services — The Business Rules](#7-services--the-business-rules)
8. [Controllers — Handling Web Requests](#8-controllers--handling-web-requests)
9. [HTML Templates — What the User Sees](#9-html-templates--what-the-user-sees)
10. [The Database Schema](#10-the-database-schema)
11. [Static Assets (CSS & JS)](#11-static-assets-css--js)
12. [Full Request Lifecycle — A Step-by-Step Example](#12-full-request-lifecycle--a-step-by-step-example)
13. [The TODOs — What Still Needs to Be Built](#13-the-todos--what-still-needs-to-be-built)
14. [Common Bugs and What to Look For](#14-common-bugs-and-what-to-look-for)

---

## 1. What This System Does

This is an **Inventory Management System** — a web application that lets a business track:

| Feature | Description |
|---|---|
| **Products** | Items the business sells or stores (name, SKU, price, stock count) |
| **Categories** | Groups that organize products (Electronics, Furniture, etc.) |
| **Suppliers** | Companies that supply the products |
| **Transactions** | Every time stock is added (Stock IN) or removed (Stock OUT) |

It runs in a web browser at `http://localhost:8080`. You open it, navigate using the top navbar, and perform CRUD operations (Create, Read, Update, Delete).

---

## 2. How Spring Boot Works (The Big Picture)

Spring Boot is a Java framework for building web applications. Think of it as the engine that:

- Listens on a port (8080) for browser requests
- Routes those requests to the right Java method
- Runs that Java method, which reads/writes the database
- Returns an HTML page back to the browser

Here is the full loop every time you click something:

```
Browser                 Spring Boot Application               MySQL Database
  |                            |                                    |
  |  GET /products             |                                    |
  |--------------------------->|                                    |
  |                     ProductController                           |
  |                     calls ProductService                        |
  |                     calls ProductRepository                     |
  |                                   |   SELECT * FROM products    |
  |                                   |--------------------------->|
  |                                   |   [rows returned]          |
  |                                   |<---------------------------|
  |                     List<Product> built from rows              |
  |                     Thymeleaf renders products/list.html        |
  |  HTML page                 |                                    |
  |<---------------------------|                                    |
```

### Key Terms

| Term | What it means |
|---|---|
| **Spring Boot** | The Java framework running the whole app |
| **Maven (pom.xml)** | The tool that downloads all dependencies (libraries) |
| **Thymeleaf** | The templating engine — takes HTML + Java data and produces a full page |
| **JdbcTemplate** | Spring's tool for running raw SQL queries against MySQL |
| **@Controller** | Marks a class as a request handler |
| **@Service** | Marks a class as business logic |
| **@Repository** | Marks a class as a data-access class |
| **th:** | Thymeleaf attribute prefix — `th:text`, `th:href`, `th:each`, etc. |

---

## 3. Project Folder Structure

```
inventory/
├── pom.xml                          ← Maven config: all dependencies listed here
└── src/main/
    ├── java/com/inventory/
    │   ├── InventoryApplication.java        ← App entry point (main method)
    │   ├── config/
    │   │   └── DataSeeder.java              ← Runs on startup: creates tables, seeds demo data
    │   ├── model/
    │   │   ├── Category.java                ← Data blueprint for a category
    │   │   ├── Supplier.java                ← Data blueprint for a supplier
    │   │   ├── Product.java                 ← Data blueprint for a product
    │   │   └── StockTransaction.java        ← Data blueprint for a transaction
    │   ├── repository/
    │   │   ├── CategoryRepository.java      ← SQL for categories table
    │   │   ├── SupplierRepository.java      ← SQL for suppliers table (has TODOs)
    │   │   ├── ProductRepository.java       ← SQL for products table
    │   │   └── StockTransactionRepository.java ← SQL for stock_transactions table
    │   ├── service/
    │   │   ├── CategoryService.java         ← Business logic for categories
    │   │   ├── SupplierService.java         ← Business logic for suppliers (has TODOs)
    │   │   ├── ProductService.java          ← Business logic for products (has TODOs)
    │   │   └── StockTransactionService.java ← Business logic for stock movements
    │   └── controller/
    │       ├── HomeController.java          ← Handles GET / (dashboard)
    │       ├── CategoryController.java      ← Handles /categories/**
    │       ├── SupplierController.java      ← Handles /suppliers/** (has TODOs)
    │       ├── ProductController.java       ← Handles /products/**
    │       └── TransactionController.java   ← Handles /transactions/**
    └── resources/
        ├── application.properties           ← DB connection string, server port, settings
        ├── static/
        │   ├── favicon.png                  ← Browser tab icon (PNG)
        │   ├── css/style.css                ← All custom CSS styles
        │   └── js/main.js                   ← Small utility JS (auto-dismiss alerts)
        └── templates/
            ├── fragments/
            │   └── layout.html              ← Shared navbar, head, footer
            ├── index.html                   ← Dashboard page
            ├── categories/
            │   ├── list.html                ← Categories table page
            │   └── form.html                ← Add/Edit category form
            ├── suppliers/
            │   ├── list.html                ← Suppliers table page
            │   └── form.html                ← Add/Edit supplier form
            ├── products/
            │   ├── list.html                ← Products table page
            │   ├── detail.html              ← Single product detail page
            │   └── form.html                ← Add/Edit product form
            └── transactions/
                ├── list.html                ← Transactions table page
                └── form.html                ← Stock IN / Stock OUT form
```

---

## 4. The Four Layers — How They Connect

Every feature in this system follows the same pattern. There are **four layers**, each with one job:

```
┌─────────────────────────────────────────────────────────┐
│  LAYER 1 — Controller                                    │
│  Receives HTTP requests. Calls the Service.              │
│  Returns a template name or a redirect.                  │
└───────────────────────┬─────────────────────────────────┘
                        │ calls
┌───────────────────────▼─────────────────────────────────┐
│  LAYER 2 — Service                                       │
│  Contains business rules and validation.                 │
│  Calls the Repository to get/save data.                  │
└───────────────────────┬─────────────────────────────────┘
                        │ calls
┌───────────────────────▼─────────────────────────────────┐
│  LAYER 3 — Repository                                    │
│  Runs the SQL queries using JdbcTemplate.                │
│  Maps database rows to Java objects (RowMapper).         │
└───────────────────────┬─────────────────────────────────┘
                        │ SQL
┌───────────────────────▼─────────────────────────────────┐
│  LAYER 4 — Database (MySQL)                              │
│  Stores all the data in tables.                          │
└─────────────────────────────────────────────────────────┘
```

The HTML templates sit **above** the Controller — they display the data the Controller puts into the `Model`.

---

## 5. Models — The Data Blueprints

Models are plain Java classes. They describe the **shape** of your data — what fields a Product has, what fields a Supplier has, etc. They do NOT talk to the database directly.

### `Category.java`

```
Fields:
  Long   id          — primary key (auto-increment from DB)
  String name        — required, must not be blank
  String description — optional
```

Annotations used:
- `@NotBlank` on `name` — Spring Validation will reject the form if this is empty

---

### `Supplier.java`

```
Fields:
  Long   id      — primary key
  String name    — required
  String email   — must be valid email format
  String phone   — optional
  String address — optional
```

---

### `Product.java`

```
Fields:
  Long       id            — primary key
  String     name          — required
  String     sku           — required, unique across all products
  String     description   — optional
  BigDecimal price         — required, must be > 0
  int        stockQuantity — current units in stock, must be >= 0
  int        reorderLevel  — threshold below which stock is "low"
  LocalDateTime createdAt  — set automatically when saved
  Category   category      — the full Category object (from a JOIN)
  Supplier   supplier      — the full Supplier object (from a JOIN)
  Long       categoryId    — used by HTML form to bind the selected category
  Long       supplierId    — used by HTML form to bind the selected supplier
```

Special method:
- `isLowStock()` — returns `true` if `stockQuantity <= reorderLevel`. Used in templates to show the orange "Low Stock" badge.

---

### `StockTransaction.java`

```
Fields:
  Long             id              — primary key
  Product          product         — the full Product object
  Type             type            — ENUM: either STOCK_IN or STOCK_OUT
  int              quantity        — how many units were moved
  String           reason          — optional note (e.g. "Monthly restock")
  LocalDateTime    transactionDate — defaults to now()
```

The `Type` is an inner enum with two values: `STOCK_IN` and `STOCK_OUT`.

---

## 6. Repositories — Talking to the Database

Repositories contain all the SQL. They use **JdbcTemplate** to send queries to MySQL and a **RowMapper** to turn each row of results into a Java object.

### How a RowMapper works

```java
// Instead of writing row.getString("name") every time, we define a RowMapper once:
private final RowMapper<Category> rowMapper = (rs, rowNum) -> {
    Category c = new Category();
    c.setId(rs.getLong("id"));
    c.setName(rs.getString("name"));
    c.setDescription(rs.getString("description"));
    return c;
};

// Then reuse it in every query:
jdbcTemplate.query("SELECT * FROM categories ORDER BY name", rowMapper);
```

### `CategoryRepository.java` — COMPLETE

| Method | SQL it runs | What it returns |
|---|---|---|
| `findAll()` | `SELECT * FROM categories ORDER BY name` | `List<Category>` |
| `findById(id)` | `SELECT * FROM categories WHERE id = ?` | `Optional<Category>` |
| `findByName(name)` | `SELECT * FROM categories WHERE name = ?` | `Optional<Category>` |
| `existsByName(name)` | `SELECT COUNT(*) FROM categories WHERE name = ?` | `boolean` |
| `searchByKeyword(kw)` | `SELECT * FROM categories WHERE name LIKE ?` | `List<Category>` |
| `save(category)` | INSERT or UPDATE depending on whether id is null | nothing |
| `deleteById(id)` | `DELETE FROM categories WHERE id = ?` | nothing |
| `count()` | `SELECT COUNT(*) FROM categories` | `long` |

---

### `ProductRepository.java` — COMPLETE

Products use a `BASE_SELECT` string that **JOINs** three tables at once:

```sql
SELECT p.*, 
       c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc,
       s.id AS sup_id, s.name AS sup_name, ...
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN suppliers s  ON p.supplier_id = s.id
```

This means when you call `findAll()`, you get back full `Product` objects with a `Category` and `Supplier` already attached — no second query needed.

| Method | Purpose |
|---|---|
| `findAll()` | All products with their category and supplier |
| `findById(id)` | One product by ID |
| `findBySku(sku)` | One product by SKU code |
| `findLowStockProducts()` | Products where `stock_quantity <= reorder_level` |
| `findOutOfStock()` | Products where `stock_quantity = 0` |
| `searchByKeyword(kw)` | Products where name or SKU contains the keyword |
| `save(product)` | INSERT (new) or UPDATE (existing) |
| `updateStock(id, qty)` | Updates only the stock column |
| `count()` | Total number of products |

---

### `SupplierRepository.java` — PARTIALLY INCOMPLETE (has TODOs)

`findAll()` and the INSERT part of `save()` work. The following are **not yet implemented**:

| TODO | Method | What it should do |
|---|---|---|
| TODO 1 | `findById(id)` | `SELECT * FROM suppliers WHERE id = ?` |
| TODO 2 | `findByEmail(email)` | `SELECT * FROM suppliers WHERE email = ?` |
| TODO 3 | `existsByEmail(email)` | `SELECT COUNT(*) FROM suppliers WHERE email = ?` |
| TODO 4 | `searchByKeyword(kw)` | `SELECT * FROM suppliers WHERE name LIKE ?` |
| TODO 5 | `save()` UPDATE block | `UPDATE suppliers SET ... WHERE id = ?` |
| TODO 6 | `deleteById(id)` | `DELETE FROM suppliers WHERE id = ?` |
| TODO 7 | `count()` | `SELECT COUNT(*) FROM suppliers` |

---

### `StockTransactionRepository.java` — COMPLETE

| Method | Purpose |
|---|---|
| `findAll()` | All transactions, newest first |
| `findById(id)` | One transaction by ID |
| `findByProduct(productId)` | All transactions for a specific product |
| `findTop10Recent()` | Last 10 transactions (for the dashboard) |
| `save(tx)` | INSERT a new transaction record |
| `sumStockInByProduct(id)` | Total units added to a product via STOCK_IN |

---

## 7. Services — The Business Rules

Services sit between Controllers and Repositories. They are responsible for:
- Checking if an action is allowed (e.g., can't have duplicate SKU)
- Coordinating multiple repository calls in one action
- Throwing meaningful errors when something goes wrong

### `CategoryService.java` — COMPLETE

Simple pass-through to the repository. Adds a convenience check:
- `isNameTaken(name, excludeId)` — used during save to detect duplicate category names

---

### `ProductService.java` — PARTIALLY INCOMPLETE

Complete methods:
- `getAllProducts()`, `getProductById()`, `saveProduct()`, `deleteProduct()`
- `getLowStockProducts()`, `getOutOfStockProducts()`

Incomplete (TODOs):
| TODO | Method | What it should do |
|---|---|---|
| TODO 4 | `searchProducts(kw)` | Call `repo.searchByKeyword(kw)` and return results |
| TODO 7 | `isSkuTaken(sku, id)` | Call `repo.existsBySku(sku, id)` and return boolean |
| TODO 8 | `countAll()` | Call `repo.count()` and return the number |

---

### `SupplierService.java` — MOSTLY INCOMPLETE

Complete: `getAllSuppliers()`

Incomplete (all throw `UnsupportedOperationException` which crashes the app if called):
| TODO | Method |
|---|---|
| TODO 1 | `getSupplierById(id)` |
| TODO 2 | `saveSupplier(supplier)` |
| TODO 3 | `deleteSupplier(id)` |
| TODO 4 | `isEmailTaken(email, id)` |

---

### `StockTransactionService.java` — COMPLETE

This is the most complex service. It handles two `@Transactional` operations:

**`addStock(productId, quantity, reason)`**
1. Validates productId and quantity (throws if invalid)
2. Calls `productRepo.updateStock(id, currentQty + quantity)` — adds to existing stock
3. Saves a `STOCK_IN` transaction record
4. Both DB writes happen together — if one fails, both are rolled back

**`removeStock(productId, quantity, reason)`**
1. Validates productId and quantity
2. Checks that current stock >= requested quantity (throws `RuntimeException` if not)
3. Calls `productRepo.updateStock(id, currentQty - quantity)` — subtracts stock
4. Saves a `STOCK_OUT` transaction record

The `@Transactional` annotation means both the stock update AND the transaction record are written atomically — they either both succeed or both fail.

---

## 8. Controllers — Handling Web Requests

Controllers are the entry point for every HTTP request. Each method is mapped to a URL with `@GetMapping` or `@PostMapping`.

### `HomeController.java`

```
GET /  →  dashboard page
```

Gathers data for the dashboard and adds it to the `Model` so Thymeleaf can display it:

| Model attribute | What it contains |
|---|---|
| `totalProducts` | `productService.countAll()` |
| `totalCategories` | `categoryRepo.count()` |
| `totalSuppliers` | `supplierRepo.count()` |
| `lowStockCount` | number of low-stock products |
| `outOfStockCount` | number of out-of-stock products |
| `lowStockItems` | List of products below reorder level |
| `recentTx` | Last 10 transactions |

Returns template: `"index"`

---

### `CategoryController.java` — COMPLETE

| Method | URL | HTTP | What it does |
|---|---|---|---|
| `list()` | `/categories` | GET | Fetches all categories, returns list template |
| `newForm()` | `/categories/new` | GET | Creates empty Category, returns form template |
| `editForm()` | `/categories/edit/{id}` | GET | Fetches existing category, returns form template |
| `save()` | `/categories/save` | POST | Validates and saves, redirects to list |
| `delete()` | `/categories/delete/{id}` | POST | Deletes by id, redirects to list |

**How flash attributes work** (used in `save()` and `delete()`):
```java
redirectAttributes.addFlashAttribute("successMessage", "Category saved.");
return "redirect:/categories";
// The message lives for exactly one redirect. The list page reads it and shows the green alert.
```

**How form validation works** (used in `save()`):
```java
public String save(@Valid @ModelAttribute Category category, BindingResult result, ...) {
    if (result.hasErrors()) {
        return "categories/form";  // Stay on form, show errors
    }
    // ... proceed to save
}
```
`@Valid` tells Spring to run the `@NotBlank` / `@Email` checks from the Model class. `BindingResult` holds any validation errors.

---

### `ProductController.java` — COMPLETE

| Method | URL | HTTP | What it does |
|---|---|---|---|
| `list()` | `/products` | GET | All products, or search results if `?search=` param present |
| `detail()` | `/products/{id}` | GET | Single product detail page |
| `newForm()` | `/products/new` | GET | Empty form, loads category/supplier dropdowns |
| `editForm()` | `/products/edit/{id}` | GET | Pre-filled form with existing product data |
| `save()` | `/products/save` | POST | Validates, saves, redirects to list |
| `delete()` | `/products/delete/{id}` | POST | Deletes product, redirects to list |

The `list()` method handles the search bar:
```java
// URL: /products?search=laptop
@RequestParam(required = false) String search
// If search is not null/empty → calls productService.searchProducts(search)
// Otherwise → calls productService.getAllProducts()
```

---

### `SupplierController.java` — PARTIALLY INCOMPLETE

| Method | URL | Status |
|---|---|---|
| `list()` | GET /suppliers | COMPLETE |
| `newForm()` | GET /suppliers/new | TODO 1 — throws exception |
| `editForm()` | GET /suppliers/edit/{id} | TODO 2 — throws exception |
| `save()` | POST /suppliers/save | TODO 3 — throws exception |
| `delete()` | POST /suppliers/delete/{id} | TODO 4 — throws exception |

---

### `TransactionController.java` — COMPLETE

| Method | URL | HTTP | What it does |
|---|---|---|---|
| `list()` | `/transactions` | GET | All transactions, newest first |
| `newForm()` | `/transactions/new` | GET | Form with product dropdown |
| `create()` | `/transactions` | POST | Reads `type` field, calls addStock or removeStock |
| `stockIn()` | `/transactions/stock-in` | POST | Calls `txService.addStock()` |
| `stockOut()` | `/transactions/stock-out` | POST | Calls `txService.removeStock()` |

Error handling in `create()`:
```java
try {
    if ("STOCK_IN".equals(type)) txService.addStock(productId, qty, reason);
    else                          txService.removeStock(productId, qty, reason);
    redirectAttributes.addFlashAttribute("successMessage", "Transaction recorded.");
} catch (RuntimeException e) {
    redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    return "redirect:/transactions/new";
}
```
If `removeStock` throws because there isn't enough stock, the user sees the error on the form instead of a crash page.

---

## 9. HTML Templates — What the User Sees

Templates use **Thymeleaf**, which processes the `th:*` attributes server-side before sending HTML to the browser.

### Common Thymeleaf Syntax

| Syntax | What it does |
|---|---|
| `th:text="${product.name}"` | Writes the value of `product.name` as text content |
| `th:href="@{/products}"` | Generates a correct URL link |
| `th:each="p : ${products}"` | Loops over the `products` list, assigns each to `p` |
| `th:if="${condition}"` | Only renders the element if condition is true |
| `th:classappend="${expr}"` | Adds a CSS class if expression is true |
| `th:replace="~{fragments/layout :: navbar}"` | Replaces this element with the `navbar` fragment from layout.html |
| `th:field="*{name}"` | Binds an input to the `name` field of the form's model object |
| `th:action="@{/categories/save}"` | Sets the form's POST destination |

---

### `fragments/layout.html`

This file has three **reusable fragments** that are included on every page:

**`head(title)`** — included as `<head th:replace="~{fragments/layout :: head('Products')}"></head>`
- Sets the page title
- Loads Bootstrap 5.3 CSS, Bootstrap Icons, SweetAlert2, and the custom style.css

**`navbar`** — included as `<nav th:replace="~{fragments/layout :: navbar}"></nav>`
- The top navigation bar with links to Products, Categories, Suppliers, Transactions
- A small JS snippet in the footer auto-adds the `active` class to the current page's nav link

**`footer`** — included as `<footer th:replace="~{fragments/layout :: footer}"></footer>`
- Shows copyright year (dynamically from Java: `${T(java.time.Year).now()}`)
- Contains the active-link detection script

---

### `index.html` — Dashboard

Uses the model data from `HomeController` to render:
- **KPI cards** — total products, categories, low stock count, out-of-stock count
- **Low Stock Alerts** table — products below reorder level with a Restock button
- **Recent Transactions** table — last 10 transactions
- **Action cards** — shortcuts to Products, Suppliers, Stock Movement

---

### `categories/list.html`

- Loops with `th:each="cat : ${categories}"` to render one table row per category
- Shows a green flash message if `successMessage` is in the model
- Delete button submits a hidden POST form (browsers can't send DELETE from a plain `<form>`)

---

### `categories/form.html`

- Uses `th:object="${category}"` and `th:field="*{name}"` to bind form fields to the Category model
- Shows `th:errors="*{name}"` if validation failed
- The title changes based on `th:if="${category.id != null}"` (Edit mode vs Add mode)

---

### `products/list.html`

Extra features beyond a basic list:
- **Live search** — JavaScript filters rows in real-time by name/SKU without a page reload
- **Pagination** — JavaScript splits rows into pages of 5
- **Stock badges** — uses `th:classappend` to pick the right color: green (in stock), orange (low), red (out of stock)
- **Clickable rows** — clicking any row navigates to that product's detail page

---

### `products/detail.html`

Read-only view of one product. Shows all fields. Has two buttons:
- **Edit** — links to `/products/edit/{id}`
- **Stock Movement** — links to `/transactions/new?productId={id}`

---

### `products/form.html`

- Category dropdown: `th:each="cat : ${categories}"` loops and `th:selected` pre-selects the current category when editing
- Same for suppliers

---

### `transactions/form.html`

The most complex form. It has:
- A **product dropdown** showing name, SKU, and current stock for each option
- A **quantity** field
- An optional **reason** field
- Two **submit buttons**: Stock OUT (red) and Stock IN (green)
- JavaScript intercepts the button click, shows a SweetAlert2 confirmation, then submits a hidden form with `type=STOCK_IN` or `type=STOCK_OUT` included

---

## 10. The Database Schema

Tables are created automatically on first startup by `DataSeeder.java`.

### `categories`
```sql
id          BIGINT AUTO_INCREMENT PRIMARY KEY
name        VARCHAR(255) NOT NULL UNIQUE
description VARCHAR(500)
```

### `suppliers`
```sql
id      BIGINT AUTO_INCREMENT PRIMARY KEY
name    VARCHAR(255) NOT NULL
email   VARCHAR(255)
phone   VARCHAR(50)
address VARCHAR(500)
```

### `products`
```sql
id             BIGINT AUTO_INCREMENT PRIMARY KEY
name           VARCHAR(255) NOT NULL
sku            VARCHAR(100) NOT NULL UNIQUE
description    TEXT
price          DECIMAL(10,2) NOT NULL
stock_quantity INT NOT NULL DEFAULT 0
reorder_level  INT NOT NULL DEFAULT 10
created_at     DATETIME DEFAULT CURRENT_TIMESTAMP
category_id    BIGINT  (FK → categories.id, SET NULL on delete)
supplier_id    BIGINT  (FK → suppliers.id,  SET NULL on delete)
```

### `stock_transactions`
```sql
id               BIGINT AUTO_INCREMENT PRIMARY KEY
product_id       BIGINT NOT NULL  (FK → products.id, CASCADE delete)
type             ENUM('STOCK_IN','STOCK_OUT') NOT NULL
quantity         INT NOT NULL
reason           VARCHAR(500)
transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP
```

### Table Relationships

```
categories ──< products >── suppliers
                  │
                  │ (one product can have many)
                  ▼
           stock_transactions
```

- One Category → Many Products
- One Supplier → Many Products
- One Product → Many Transactions

If a Category is deleted, its products' `category_id` becomes NULL (SET NULL).
If a Product is deleted, all its transactions are also deleted (CASCADE).

---

## 11. Static Assets (CSS & JS)

### `static/css/style.css`

Defines custom design on top of Bootstrap. Key sections:

| CSS class / variable | What it styles |
|---|---|
| `--color-navy`, `--color-blue`, etc. | Color palette used across the app |
| `.navbar-pill` | The rounded, white top navigation bar |
| `.kpi-wrapper` | The dashboard stat cards |
| `.table-custom` | Transparent header, subtle row stripes |
| `.product-cell` | Product icon box + name/SKU layout in tables |
| `.action-card` | The clickable shortcut cards on the dashboard |
| `.alert-card` | The red warning box for out-of-stock alerts |

### `static/js/main.js`

Only 8 lines. Auto-dismisses Bootstrap success alerts after 3.5 seconds:
```javascript
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        document.querySelectorAll('.alert-dismissible').forEach(function (alert) {
            new bootstrap.Alert(alert).close();
        });
    }, 3500);
});
```

---

## 12. Full Request Lifecycle — A Step-by-Step Example

### Scenario: User saves a new product

**Step 1 — User fills the form at `/products/new` and clicks Save**

The browser sends:
```
POST /products/save
Body: name=Laptop&sku=ELEC-999&price=29999&stockQuantity=10&...
```

**Step 2 — Spring routes to `ProductController.save()`**
```java
@PostMapping("/products/save")
public String save(@Valid @ModelAttribute("product") Product product,
                   BindingResult result, ...) {
```
Spring automatically maps the form fields to the `Product` object fields.

**Step 3 — Validation runs**

`@Valid` triggers checks like `@NotBlank` and `@Min`. If anything fails, `result.hasErrors()` is true and the user stays on the form with error messages.

**Step 4 — Controller calls Service**
```java
productService.saveProduct(product);
```

**Step 5 — Service calls Repository**
```java
// In ProductService.saveProduct():
repo.save(product);
```

**Step 6 — Repository runs SQL**
```java
// In ProductRepository.save():
// product.getId() is null (new product), so it runs INSERT:
jdbcTemplate.update(
    "INSERT INTO products (name, sku, price, ...) VALUES (?, ?, ?, ...)",
    product.getName(), product.getSku(), product.getPrice(), ...
);
```

**Step 7 — Controller redirects**
```java
redirectAttributes.addFlashAttribute("successMessage", "Product saved.");
return "redirect:/products";
```

**Step 8 — Browser is sent to `/products`**

`ProductController.list()` runs, fetches all products, renders `products/list.html`. The green "Product saved." alert appears for 3.5 seconds.

---

## 13. The TODOs — What Still Needs to Be Built

These are the incomplete parts of the project. Attempting to use them will either throw an error or silently do nothing.

### Supplier Module (full feature is missing)

**`SupplierRepository.java`** — needs SQL implementations for:
- `findById(id)` → `SELECT * FROM suppliers WHERE id = ?`
- `findByEmail(email)` → `SELECT * FROM suppliers WHERE email = ?`
- `existsByEmail(email)` → `SELECT COUNT(*) ... > 0`
- `searchByKeyword(kw)` → `SELECT * FROM suppliers WHERE name LIKE ?`
- `save(supplier)` UPDATE block → `UPDATE suppliers SET name=?, email=?, ... WHERE id=?`
- `deleteById(id)` → `DELETE FROM suppliers WHERE id = ?`
- `count()` → `SELECT COUNT(*) FROM suppliers`

**`SupplierService.java`** — needs to call those repository methods instead of throwing exceptions.

**`SupplierController.java`** — needs to implement `newForm()`, `editForm()`, `save()`, `delete()` following the same pattern as `CategoryController`.

### Product Module (minor gaps)

**`ProductService.java`**:
- `searchProducts(kw)` — call `repo.searchByKeyword(kw)`
- `isSkuTaken(sku, excludeId)` — call `repo.existsBySku(sku, excludeId)`
- `countAll()` — call `repo.count()`

---

## 14. Common Bugs and What to Look For

| Symptom | Likely Cause | Where to look |
|---|---|---|
| White error page with "UnsupportedOperationException" | A TODO method was called | The Supplier feature — Service or Controller |
| Form saves but validation errors are ignored | `@Valid` missing on the controller parameter, or `BindingResult` not declared right after | Controller `save()` method signature |
| Page shows `null` where a value should be | Model attribute not added in Controller, or wrong attribute name in template | Controller method vs `th:text="${...}"` in template |
| "could not resolve placeholder" or DB connection error | Wrong credentials in `application.properties` | `spring.datasource.username` / `password` |
| Favicon not showing | Browser cached a 404 — do Ctrl+Shift+R | layout.html `<link rel="icon">` |
| `th:each` loop shows nothing | The list is empty OR the variable name doesn't match | Controller model.addAttribute name vs template variable |
| "BindingResult must follow the model attribute" | `BindingResult result` is not the parameter directly after `@ModelAttribute` | Controller method parameter order |
| Stock goes negative | `removeStock()` validation not called, or TODO in SupplierService causing a different code path | `StockTransactionService.removeStock()` |
| `redirect:` shows no flash message | Flash attribute was added to `Model` instead of `RedirectAttributes` | Controller method — should use `RedirectAttributes`, not `Model` |

---

*End of Project Guide*
