package org.saad.mcpserver.tools;

import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * E-Commerce AI Tools - Exposed via MCP protocol
 * These tools can be called by the AI agent to get business data
 */
@Component
public class EcommerceTools {

    // ============ PRODUCT TOOLS ============

    @McpTool(name = "getProduct", description = "Get information about a specific product by name or ID")
    public Product getProduct(@McpArg(description = "The product name or ID") String productNameOrId) {
        // Simulated product data - in production, call Product Service via REST
        return switch (productNameOrId.toLowerCase()) {
            case "iphone", "iphone 15" -> new Product("P001", "iPhone 15 Pro", 1299.99, 50, "Electronics", true);
            case "macbook", "macbook pro" -> new Product("P002", "MacBook Pro M3", 2499.99, 25, "Electronics", true);
            case "airpods", "airpods pro" -> new Product("P003", "AirPods Pro 2", 249.99, 100, "Electronics", true);
            default -> new Product("P999", productNameOrId, 99.99, 10, "General", true);
        };
    }

    @McpTool(name = "getAllProducts", description = "Get all available products in the store")
    public List<Product> getAllProducts() {
        return List.of(
                new Product("P001", "iPhone 15 Pro", 1299.99, 50, "Electronics", true),
                new Product("P002", "MacBook Pro M3", 2499.99, 25, "Electronics", true),
                new Product("P003", "AirPods Pro 2", 249.99, 100, "Electronics", true),
                new Product("P004", "iPad Air", 799.99, 35, "Electronics", true),
                new Product("P005", "Apple Watch", 399.99, 60, "Electronics", true));
    }

    @McpTool(name = "searchProducts", description = "Search products by category or keyword")
    public List<Product> searchProducts(
            @McpArg(description = "Category or keyword to search") String searchTerm) {
        List<Product> allProducts = getAllProducts();
        return allProducts.stream()
                .filter(p -> p.name().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        p.category().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }

    @McpTool(name = "checkProductStock", description = "Check if a product is in stock")
    public StockInfo checkProductStock(@McpArg(description = "Product name") String productName) {
        Product product = getProduct(productName);
        boolean inStock = product.quantity() > 0;
        String status = inStock ? "In Stock" : "Out of Stock";
        return new StockInfo(product.id(), product.name(), product.quantity(), status);
    }

    // ============ CUSTOMER TOOLS ============

    @McpTool(name = "getCustomer", description = "Get customer information by name or email")
    public Customer getCustomer(@McpArg(description = "Customer name or email") String identifier) {
        // Simulated customer data
        return switch (identifier.toLowerCase()) {
            case "saad", "saad@email.com" -> new Customer("C001", "Saad", "saad@email.com", "Gold", 5);
            case "hassan", "hassan@email.com" -> new Customer("C002", "Hassan", "hassan@email.com", "Silver", 3);
            case "fatima", "fatima@email.com" -> new Customer("C003", "Fatima", "fatima@email.com", "Platinum", 10);
            default -> new Customer("C999", identifier, identifier + "@email.com", "Standard", 1);
        };
    }

    @McpTool(name = "getCustomerOrders", description = "Get orders for a specific customer")
    public List<Order> getCustomerOrders(@McpArg(description = "Customer name") String customerName) {
        return List.of(
                new Order("ORD001", customerName, 1599.98, "Delivered", "2024-12-15"),
                new Order("ORD002", customerName, 249.99, "Shipped", "2024-12-18"),
                new Order("ORD003", customerName, 799.99, "Processing", "2024-12-19"));
    }

    // ============ BUSINESS TOOLS ============

    @McpTool(name = "getDailyStats", description = "Get today's sales statistics")
    public SalesStats getDailyStats() {
        return new SalesStats(
                125, // total orders
                45678.50, // revenue
                15, // new customers
                4.8 // average rating
        );
    }

    @McpTool(name = "getTopProducts", description = "Get top selling products")
    public List<TopProduct> getTopProducts() {
        return List.of(
                new TopProduct("iPhone 15 Pro", 150, 194985.00),
                new TopProduct("AirPods Pro 2", 280, 69997.20),
                new TopProduct("MacBook Pro M3", 45, 112495.55));
    }

    // ============ RECORDS (DTOs) ============

    public record Product(String id, String name, double price, int quantity, String category, boolean available) {
    }

    public record Customer(String id, String name, String email, String tier, int totalOrders) {
    }

    public record Order(String orderId, String customerName, double total, String status, String date) {
    }

    public record StockInfo(String productId, String productName, int quantity, String status) {
    }

    public record SalesStats(int totalOrders, double revenue, int newCustomers, double avgRating) {
    }

    public record TopProduct(String name, int unitsSold, double revenue) {
    }
}
