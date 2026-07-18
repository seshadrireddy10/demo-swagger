package com.example_swagger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // ==========================================
    // EXISTING GET APIs
    // ==========================================

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return Arrays.asList(
            new Product(1L, "Laptop", 999.99),
            new Product(2L, "Smartphone", 499.99)
        );
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return new Product(id, "Sample Product", 150.00);
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String name) {
        return "Searching for product: " + name;
    }

    // ==========================================
    // NEW CRUD APIs (POST, PUT, PATCH, DELETE)
    // ==========================================

    // 1. POST - Create a new product
    // Expects JSON body: { "name": "Tablet", "price": 299.99 }
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product newProduct) {
        // Mocking database save by assigning a dummy ID
        newProduct.setId(99L); 
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED); // Returns 201 Created
    }

    // 2. PUT - Update an entire existing product by ID
    // Expects JSON body with full updated object details
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        updatedProduct.setId(id); // Ensure the ID matches the path variable
        return ResponseEntity.ok(updatedProduct); // Returns 200 OK
    }

    // 3. PATCH - Partially update a product property (e.g., just the price)
    // Expects JSON body with partial data like: { "price": 899.99 }
    @PatchMapping("/{id}/price")
    public ResponseEntity<String> updateProductPrice(@PathVariable Long id, @RequestBody Product partialProduct) {
        return ResponseEntity.ok("Successfully updated price for product ID " + id + " to " + partialProduct.getPrice());
    }

    // 4. DELETE - Remove a product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok("Product with ID " + id + " has been successfully deleted.");
    }
}
