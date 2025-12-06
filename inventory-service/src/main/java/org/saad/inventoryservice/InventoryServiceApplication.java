package org.saad.inventoryservice;

import org.saad.inventoryservice.entities.Product;
import org.saad.inventoryservice.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(ProductRepository productRepository){
		return args -> {
			productRepository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("MacBook Pro 16-inch")
					.price(2499.99)
					.quantity(45)
					.build());
			productRepository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("Sony WH-1000XM5 Headphones")
					.price(399.99)
					.quantity(128)
					.build());
			productRepository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("Samsung Galaxy S24 Ultra")
					.price(1299.00)
					.quantity(67)
					.build());
			productRepository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("LG UltraWide Monitor 34-inch")
					.price(599.50)
					.quantity(32)
					.build());
			productRepository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("Logitech MX Master 3S Mouse")
					.price(99.99)
					.quantity(215)
					.build());
			productRepository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("iPad Pro 12.9-inch")
					.price(1099.00)
					.quantity(89)
					.build());

			productRepository.findAll().forEach(p->{
				System.out.println("========================================");
				System.out.println("Product: " + p.getName());
				System.out.println("Price: $" + p.getPrice());
				System.out.println("Stock: " + p.getQuantity() + " units");
				System.out.println("========================================");
			});
		};
	}

}