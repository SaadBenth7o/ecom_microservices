package org.saad.billingservice;

import org.saad.billingservice.entities.Bill;
import org.saad.billingservice.entities.ProductItem;
import org.saad.billingservice.feign.CustomerRestClient;
import org.saad.billingservice.feign.ProductRestClient;
import org.saad.billingservice.model.Customer;
import org.saad.billingservice.model.Product;
import org.saad.billingservice.repository.BillRepository;
import org.saad.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner(BillRepository  billRepository,
										ProductItemRepository productItemRepository,
										CustomerRestClient customerRestClient,
										ProductRestClient productRestClient){

		return args -> {
			Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();
			Collection<Product> products = productRestClient.getAllProducts().getContent();

			customers.forEach(customer -> {
				Bill bill = Bill.builder()
						.billingDate(new Date())
						.customerId(customer.getId())
						.build();
				billRepository.save(bill);
				products.forEach(product -> {
					ProductItem productItem = ProductItem.builder()
							.bill(bill)
							.productId(product.getId())
							.quantity(1+new Random().nextInt(10))
							.unitPrice(product.getPrice())
							.build();
					productItemRepository.save(productItem);
				});
			});
		};
	}

}
