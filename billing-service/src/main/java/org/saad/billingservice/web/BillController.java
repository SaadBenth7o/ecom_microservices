package org.saad.billingservice.web;

import jakarta.validation.Valid;
import org.saad.billingservice.entities.Bill;
import org.saad.billingservice.entities.ProductItem;
import org.saad.billingservice.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillRepository billRepository;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {
        Optional<Bill> bill = billRepository.findById(id);
        return bill.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Bill>> getBillsByCustomer(@PathVariable Long customerId) {
        List<Bill> bills = billRepository.findByCustomerId(customerId);
        return ResponseEntity.ok(bills);
    }

    @PostMapping
    public ResponseEntity<Bill> createBill(@Valid @RequestBody Bill bill) {
        // Vérifier que le customer existe
        if (bill.getCustomerId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // Vérifier qu'il y a au moins un produit
        if (bill.getProductItems() == null || bill.getProductItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // Définir la date si elle n'est pas fournie
        if (bill.getBillingDate() == null) {
            bill.setBillingDate(new Date());
        }
        
        // Associer chaque ProductItem à la facture
        for (ProductItem item : bill.getProductItems()) {
            item.setBill(bill);
        }
        
        Bill savedBill = billRepository.save(bill);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bill> updateBill(@PathVariable Long id, @Valid @RequestBody Bill billDetails) {
        Optional<Bill> optionalBill = billRepository.findById(id);
        if (optionalBill.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Bill bill = optionalBill.get();
        bill.setBillingDate(billDetails.getBillingDate());
        
        if (billDetails.getCustomerId() != null) {
            bill.setCustomerId(billDetails.getCustomerId());
        }
        
        // Mettre à jour les ProductItems
        if (billDetails.getProductItems() != null && !billDetails.getProductItems().isEmpty()) {
            bill.getProductItems().clear();
            bill.getProductItems().addAll(billDetails.getProductItems());
            for (ProductItem item : bill.getProductItems()) {
                item.setBill(bill);
            }
        }
        
        Bill updatedBill = billRepository.save(bill);
        return ResponseEntity.ok(updatedBill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        if (!billRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        billRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

