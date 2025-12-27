package net.bendahou.mcpserver.tools;

import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * MCP Tools for accessing business microservices.
 * These tools allow the chatbot to query Customer, Billing, and Inventory data.
 */
@Component
public class BusinessServiceTools {

    private final WebClient webClient;

    @Value("${microservices.customer-service-url}")
    private String customerServiceUrl;

    @Value("${microservices.inventory-service-url}")
    private String inventoryServiceUrl;

    @Value("${microservices.billing-service-url}")
    private String billingServiceUrl;

    public BusinessServiceTools(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    // ===================== CUSTOMER SERVICE TOOLS =====================

    @McpTool(name = "getAllCustomers", description = "Get all customers from the customer database")
    public List<Map<String, Object>> getAllCustomers() {
        try {
            return webClient.get()
                    .uri(customerServiceUrl + "/customers")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            return List.of(Map.of("error", "Failed to fetch customers: " + e.getMessage()));
        }
    }

    @McpTool(name = "getCustomerById", description = "Get a specific customer by their ID")
    public Map<String, Object> getCustomerById(@McpArg(description = "The customer ID") Long customerId) {
        try {
            return webClient.get()
                    .uri(customerServiceUrl + "/customers/" + customerId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return Map.of("error", "Customer not found: " + e.getMessage());
        }
    }

    @McpTool(name = "searchCustomerByName", description = "Search for customers by name")
    public List<Map<String, Object>> searchCustomerByName(
            @McpArg(description = "The customer name to search for") String name) {
        try {
            return webClient.get()
                    .uri(customerServiceUrl + "/customers/search/findByNameContaining?name=" + name)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        Object embedded = response.get("_embedded");
                        if (embedded instanceof Map) {
                            Object customers = ((Map<?, ?>) embedded).get("customers");
                            if (customers instanceof List) {
                                return (List<Map<String, Object>>) customers;
                            }
                        }
                        return List.<Map<String, Object>>of();
                    })
                    .block();
        } catch (Exception e) {
            return List.of(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    // ===================== INVENTORY SERVICE TOOLS =====================

    @McpTool(name = "getAllProducts", description = "Get all products from the inventory")
    public List<Map<String, Object>> getAllProducts() {
        try {
            return webClient.get()
                    .uri(inventoryServiceUrl + "/products")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            return List.of(Map.of("error", "Failed to fetch products: " + e.getMessage()));
        }
    }

    @McpTool(name = "getProductById", description = "Get a specific product by its ID")
    public Map<String, Object> getProductById(@McpArg(description = "The product ID") Long productId) {
        try {
            return webClient.get()
                    .uri(inventoryServiceUrl + "/products/" + productId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return Map.of("error", "Product not found: " + e.getMessage());
        }
    }

    @McpTool(name = "searchProductByName", description = "Search for products by name")
    public List<Map<String, Object>> searchProductByName(
            @McpArg(description = "The product name to search for") String name) {
        try {
            return webClient.get()
                    .uri(inventoryServiceUrl + "/products/search/findByNameContaining?name=" + name)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        Object embedded = response.get("_embedded");
                        if (embedded instanceof Map) {
                            Object products = ((Map<?, ?>) embedded).get("products");
                            if (products instanceof List) {
                                return (List<Map<String, Object>>) products;
                            }
                        }
                        return List.<Map<String, Object>>of();
                    })
                    .block();
        } catch (Exception e) {
            return List.of(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    @McpTool(name = "getProductStock", description = "Get the stock quantity for a product")
    public Map<String, Object> getProductStock(@McpArg(description = "The product ID") Long productId) {
        try {
            Map<String, Object> product = getProductById(productId);
            if (product.containsKey("error")) {
                return product;
            }
            return Map.of(
                    "productId", productId,
                    "name", product.getOrDefault("name", "Unknown"),
                    "quantity", product.getOrDefault("quantity", 0),
                    "price", product.getOrDefault("price", 0));
        } catch (Exception e) {
            return Map.of("error", "Failed to get stock: " + e.getMessage());
        }
    }

    // ===================== BILLING SERVICE TOOLS =====================

    @McpTool(name = "getAllBills", description = "Get all bills from the billing system")
    public List<Map<String, Object>> getAllBills() {
        try {
            return webClient.get()
                    .uri(billingServiceUrl + "/bills")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
        } catch (Exception e) {
            return List.of(Map.of("error", "Failed to fetch bills: " + e.getMessage()));
        }
    }

    @McpTool(name = "getBillById", description = "Get a specific bill by its ID")
    public Map<String, Object> getBillById(@McpArg(description = "The bill ID") Long billId) {
        try {
            return webClient.get()
                    .uri(billingServiceUrl + "/bills/" + billId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return Map.of("error", "Bill not found: " + e.getMessage());
        }
    }

    @McpTool(name = "getBillsForCustomer", description = "Get all bills for a specific customer")
    public List<Map<String, Object>> getBillsForCustomer(@McpArg(description = "The customer ID") Long customerId) {
        try {
            return webClient.get()
                    .uri(billingServiceUrl + "/bills/search/findByCustomerId?customerId=" + customerId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> {
                        Object embedded = response.get("_embedded");
                        if (embedded instanceof Map) {
                            Object bills = ((Map<?, ?>) embedded).get("bills");
                            if (bills instanceof List) {
                                return (List<Map<String, Object>>) bills;
                            }
                        }
                        return List.<Map<String, Object>>of();
                    })
                    .block();
        } catch (Exception e) {
            return List.of(Map.of("error", "Failed to fetch customer bills: " + e.getMessage()));
        }
    }

    @McpTool(name = "getBillDetails", description = "Get complete details of a bill including customer and products")
    public Map<String, Object> getBillDetails(@McpArg(description = "The bill ID") Long billId) {
        try {
            Map<String, Object> bill = webClient.get()
                    .uri(billingServiceUrl + "/fullBill/" + billId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (bill == null) {
                return Map.of("error", "Bill not found");
            }
            return bill;
        } catch (Exception e) {
            // Try alternative endpoint
            return getBillById(billId);
        }
    }

    // ===================== ANALYTICS TOOLS =====================

    @McpTool(name = "getBusinessSummary", description = "Get a summary of the business: total customers, products, and bills")
    public Map<String, Object> getBusinessSummary() {
        try {
            List<Map<String, Object>> customers = getAllCustomers();
            List<Map<String, Object>> products = getAllProducts();
            List<Map<String, Object>> bills = getAllBills();

            int customerCount = customers.stream().anyMatch(c -> c.containsKey("error")) ? 0 : customers.size();
            int productCount = products.stream().anyMatch(p -> p.containsKey("error")) ? 0 : products.size();
            int billCount = bills.stream().anyMatch(b -> b.containsKey("error")) ? 0 : bills.size();

            return Map.of(
                    "totalCustomers", customerCount,
                    "totalProducts", productCount,
                    "totalBills", billCount,
                    "status", "active");
        } catch (Exception e) {
            return Map.of("error", "Failed to generate summary: " + e.getMessage());
        }
    }
}
