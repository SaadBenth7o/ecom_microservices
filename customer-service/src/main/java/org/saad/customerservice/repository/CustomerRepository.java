package org.saad.customerservice.repository;

import org.saad.customerservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Search customers by name (for chatbot queries)
    List<Customer> findByNameContaining(String name);
    
    // Find customer by email
    Optional<Customer> findByEmail(String email);
}
