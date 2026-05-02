package com.inventory.controller;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SupplierController - Handles all supplier management
 * 
 * WHAT: This controller manages supplier data (create, read, update, delete)
 * WHY: Users need to add, view, edit, and remove suppliers from the system
 * HOW: It responds to web requests and uses SupplierService to work with the database
 * 
 * ROUTES (web addresses this controller listens to):
 *   GET  /suppliers           - Show list of all suppliers
 *   GET  /suppliers/new       - Show empty form to add new supplier
 *   GET  /suppliers/edit/{id} - Show form with supplier details filled in (for editing)
 *   POST /suppliers/save      - Save the supplier to database (new or update)
 *   POST /suppliers/delete/{id} - Remove supplier from database
 * 
 * TEMPLATES (HTML pages used):
 *   suppliers/list.html  - Page showing all suppliers
 *   suppliers/form.html  - Page with form for adding/editing suppliers
 */
@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // EXAMPLE METHOD - Get and display all suppliers
    // WHAT: Gets all suppliers from database and shows them on a list page
    // WHY: Users need to see all suppliers to manage them
    // HOW: Calls supplierService to get all suppliers, adds them to the page
    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "suppliers/list";
    }

    // TODO 1 - Show blank form for adding a new supplier
    // WHAT: Displays an empty supplier form
    // WHY: User needs a form to enter new supplier information
    // HOW: Creates a new empty Supplier object and passes it to the form page
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/form";
        
    }

    // TODO 2 - Show form pre-filled with supplier details for editing
    // WHAT: Finds a specific supplier by ID and shows the edit form
    // WHY: User needs to see the current supplier data before making changes
    // HOW: Takes the supplier ID from the URL, finds that supplier in database, 
    //      fills the form with their current information
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Supplier supplier = supplierService.getSupplierById(id).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
        model.addAttribute("supplier", supplier);
        return "suppliers/form";
    }

    // TODO 3 - Save supplier (new or updated) to the database
    // WHAT: Takes form data, checks if it's correct, saves it to database
    // WHY: We need to store supplier information permanently
    // HOW: Validates the data → if no errors, saves to database → shows success message
    //      If validation fails, shows form again with error messages
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Supplier supplier, BindingResult result,
                       RedirectAttributes flash) {
        if (result.hasErrors()){
            return "suppliers/form";
        }
        supplierService.saveSupplier(supplier);
        flash.addFlashAttribute("success","Supplier saved successfully");

        return "redirect:/suppliers";
    }

    // TODO 4 - Delete a supplier from the database
    // WHAT: Removes a supplier permanently from the system
    // WHY: When a supplier is no longer needed, we should remove their record
    // HOW: Takes the supplier ID from the URL, deletes that supplier from database,
    //      then shows a success message
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        supplierService.deleteSupplier(id);
        flash.addFlashAttribute("success", "Supplier deleted successfully!");

        return "redirect:/suppliers";
    }
}
