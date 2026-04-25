<div align="center">

# 📦 InventoryMS — Systems Integration Activity

### A hands-on group coding activity built with Spring Boot · Spring JDBC · MySQL · Thymeleaf

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

> **No Hibernate. No magic. Just real SQL — written by you.**

</div>

---

## 🎯 What Is This?

This is not a tutorial you watch. This is a system you **build**.

InventoryMS is a real-world inventory management web application — and it's **intentionally incomplete**. The models, templates, database schema, and application wiring are already done. What's missing is the part that actually makes it work: **the queries, the service methods, and the HTTP handlers.**

That's your job.

Working in groups, you will complete the missing code layer by layer — from raw SQL in the repository, up through the service, and all the way to the controller — until the full system comes to life.

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      Browser (HTTP)                     │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│                  Controller Layer                        │
│   HomeController · ProductController · CategoryController│
│   SupplierController · TransactionController            │
│              ← YOUR GROUP COMPLETES THESE →             │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│                   Service Layer                          │
│   ProductService · CategoryService · SupplierService    │
│   StockTransactionService                               │
│              ← YOUR GROUP COMPLETES THESE →             │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────┐
│                 Repository Layer                         │
│   ProductRepository · CategoryRepository                │
│   SupplierRepository · StockTransactionRepository       │
│              ← YOUR GROUP COMPLETES THESE →             │
└──────────────────────────┬──────────────────────────────┘
                           │  Spring JdbcTemplate
┌──────────────────────────▼──────────────────────────────┐
│                  MySQL Database                          │
│   categories · suppliers · products · stock_transactions │
│              ← schema auto-created on startup →         │
└─────────────────────────────────────────────────────────┘
```

**No Hibernate, no JPA, no auto-generated queries.** Every SQL statement is written explicitly by hand — because understanding what your code is actually doing to the database is the whole point.

---

## 👥 Group Assignments

The activity is divided into **4 groups**, each owning one vertical slice of the system:

| Group | Module | Repository | Service | Controller |
|-------|--------|------------|---------|------------|
| **Group 1** | Categories | `CategoryRepository` | `CategoryService` | `CategoryController` |
| **Group 2** | Suppliers | `SupplierRepository` | `SupplierService` | `SupplierController` |
| **Group 3** | Products | `ProductRepository` | `ProductService` | `ProductController` |
| **Group 4** | Stock Transactions | `StockTransactionRepository` | `StockTransactionService` | `TransactionController` |

> The `HomeController` (dashboard) is a shared bonus — all groups contribute once their services are working.

Each group touches **exactly three files**, but those files connect to the work of every other group. **If one group doesn't finish, the whole system is incomplete.** That's systems integration.

---

## ✅ What You Need to Complete

Each file contains one fully-worked **EXAMPLE** at the top, followed by numbered **TODO** items.

### Repository (Raw SQL — hardest, most important)
- Write `SELECT`, `INSERT`, `UPDATE`, `DELETE` SQL using `JdbcTemplate`
- Use the provided `RowMapper` to map result rows into Java objects
- Append `WHERE`, `ORDER BY`, and `LIMIT` clauses to the provided `BASE_SELECT`

### Service (Business Logic)
- Wire repository calls to the controller layer
- Groups 4's `StockTransactionService` has real logic: validate stock levels, calculate new quantities, and keep data consistent across two tables

### Controller (HTTP Handlers)
- Map `@GetMapping` and `@PostMapping` routes to service calls
- Pass data to Thymeleaf templates via the `Model`
- Handle validation errors and redirect with flash messages

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL 8.0+
- An IDE (IntelliJ IDEA or VS Code with Java extensions)

### Setup

**1. Clone the repository**
```bash
https://github.com/ventusire/System-Integration-PreFinal.git
cd inventoryms-activity/inventory
```

**2. Create the database**

Run the following in MySQL Workbench or your terminal:
```sql
CREATE DATABASE IF NOT EXISTS inventorydb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```
> The tables are created automatically when the app first starts.

**3. Configure your database credentials**

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventorydb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password_here
```

**4. Run the application**
```bash
mvn spring-boot:run
```

The app starts at **http://localhost:8080**

> On first startup, `DataSeeder` automatically creates all tables and seeds 3 categories, 2 suppliers, and 5 sample products.

---

## 📁 Project Structure

```
src/
└── main/
    ├── java/com/inventory/
    │   ├── InventoryApplication.java     ← entry point (do not modify)
    │   ├── model/                        ← plain Java classes (provided)
    │   │   ├── Category.java
    │   │   ├── Supplier.java
    │   │   ├── Product.java
    │   │   └── StockTransaction.java
    │   ├── repository/                   ← ★ YOUR SQL GOES HERE
    │   │   ├── CategoryRepository.java   (Group 1)
    │   │   ├── SupplierRepository.java   (Group 2)
    │   │   ├── ProductRepository.java    (Group 3)
    │   │   └── StockTransactionRepository.java (Group 4)
    │   ├── service/                      ← ★ YOUR LOGIC GOES HERE
    │   │   ├── CategoryService.java      (Group 1)
    │   │   ├── SupplierService.java      (Group 2)
    │   │   ├── ProductService.java       (Group 3)
    │   │   └── StockTransactionService.java (Group 4)
    │   ├── controller/                   ← ★ YOUR ROUTES GO HERE
    │   │   ├── HomeController.java       (All groups — bonus)
    │   │   ├── CategoryController.java   (Group 1)
    │   │   ├── SupplierController.java   (Group 2)
    │   │   ├── ProductController.java    (Group 3)
    │   │   └── TransactionController.java (Group 4)
    │   └── config/
    │       └── DataSeeder.java           ← seeds demo data (do not modify)
    └── resources/
        ├── application.properties        ← update DB credentials here
        ├── templates/                    ← Thymeleaf HTML (provided)
        └── static/                       ← CSS + JS (provided)
```

---

## 💡 Tips

- **Start with the Repository** — the service and controller are useless without it
- **Run the app often** — Spring Boot will tell you exactly which TODO caused the error
- **Watch the console** — every SQL query is printed when `logging.level.org.springframework.jdbc.core=DEBUG` is set
- **Read the EXAMPLE first** — every file shows a fully-worked method before the TODOs begin
- **Talk to other groups** — Group 3 (Products) depends on Group 1 (Categories) and Group 2 (Suppliers) being done

---

## 🗄️ Database Schema

```sql
categories        suppliers          products                stock_transactions
──────────        ─────────          ────────                ──────────────────
id (PK)           id (PK)            id (PK)                 id (PK)
name              name               name                    product_id (FK)
description       email              sku                     type (STOCK_IN/OUT)
                  phone              description             quantity
                  address            price                   reason
                                     stock_quantity          transaction_date
                                     reorder_level
                                     created_at
                                     category_id (FK)
                                     supplier_id (FK)
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Spring Boot 3.2** | Application framework |
| **Spring JDBC (JdbcTemplate)** | Database access — no ORM, no Hibernate |
| **MySQL 8** | Relational database |
| **Thymeleaf** | Server-side HTML templating |
| **Bootstrap 5** | UI styling |
| **Maven** | Build and dependency management |

---

## 📜 License

This project is intended for educational use.

---

<div align="center">

**Happy coding. May your queries return rows and your stack traces be short.**

</div>
