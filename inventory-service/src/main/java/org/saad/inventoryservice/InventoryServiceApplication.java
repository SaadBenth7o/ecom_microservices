package org.saad.inventoryservice;

import org.saad.inventoryservice.entities.Product;
import org.saad.inventoryservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(ProductRepository productRepository){
		return args -> {
			// Note: Les produits seront crees avec des customerId valides
			// via l'API REST. Ici on cree des produits de test avec customerId = 1
			if (productRepository.count() == 0) {
				productRepository.save(Product.builder()
						.name("Computer")
						.price(6500.0)
						.quantity(321)
						.customerId(1L)
						.build());
				productRepository.save(Product.builder()
						.name("Printer")
						.price(5400.0)
						.quantity(19)
						.customerId(1L)
						.build());
				productRepository.save(Product.builder()
						.name("Smart Phone")
						.price(4300.0)
						.quantity(14)
						.customerId(1L)
						.build());

				productRepository.findAll().forEach(p->{
					System.out.println(p.toString());
				});
			}
		};
	}

}