package org.saad.billingservice.repository;

import org.saad.billingservice.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface BillRepository extends JpaRepository<Bill, Long> {

    // Find bills by customer (for chatbot queries)
    List<Bill> findByCustomerId(Long customerId);
}
