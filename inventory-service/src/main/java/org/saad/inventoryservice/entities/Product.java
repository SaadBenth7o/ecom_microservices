package org.saad.inventoryservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
// @EntityListeners(ProductEventListener.class) - Removed: Inventory Service does not publish to Kafka per architecture
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 200, message = "Le nom doit contenir entre 2 et 200 caractères")
    @Column(nullable = false)
    private String name;
    
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    @Column(nullable = false)
    private Double price;
    
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @Column(nullable = false)
    private Integer quantity;
    
    // Relation avec Customer - Un produit appartient à un customer
    @NotNull(message = "Le client est obligatoire")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Transient
    private String customerName; // Pour l'affichage dans le frontend
}
