package org.saad.inventoryservice.repository;

import org.saad.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search products by name (for chatbot queries)
    List<Product> findByNameContaining(String name);
    
    // Find products by customer ID
    List<Product> findByCustomerId(Long customerId);
}
