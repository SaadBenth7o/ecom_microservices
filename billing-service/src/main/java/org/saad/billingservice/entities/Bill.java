package org.saad.billingservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.saad.billingservice.listener.BillEventListener;
import org.saad.billingservice.model.Customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bills")
@EntityListeners(BillEventListener.class)
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La date de facturation est obligatoire")
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date billingDate;
    
    @NotNull(message = "Le client est obligatoire")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull(message = "La facture doit contenir au moins un produit")
    @Size(min = 1, message = "La facture doit contenir au moins un produit")
    private List<ProductItem> productItems = new ArrayList<>();
    
    @Transient 
    private Customer customer;
    
    // Méthode utilitaire pour calculer le total
    public Double getTotalAmount() {
        return productItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
    }
}
