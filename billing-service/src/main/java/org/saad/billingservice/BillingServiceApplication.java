package org.saad.billingservice;

import org.saad.billingservice.entities.Bill;
import org.saad.billingservice.entities.ProductItem;
import org.saad.billingservice.feign.CustomerRestClient;
import org.saad.billingservice.feign.ProductRestClient;
import org.saad.billingservice.model.Customer;
import org.saad.billingservice.model.Product;
import org.saad.billingservice.repository.BillRepository;
import org.saad.billingservice.repository.ProductItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class BillingServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(BillingServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(DataInitializer dataInitializer) {
		return args -> {
			try {
				dataInitializer.initializeData();
				log.info("✅ Billing data initialization completed successfully!");
			} catch (Exception e) {
				log.warn("⚠️ Could not initialize billing data: {}. The service will continue running.",
						e.getMessage());
				log.info("You can manually trigger data initialization once dependent services are available.");
			}
		};
	}

	@Service
	static class DataInitializer {

		private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

		private final BillRepository billRepository;
		private final ProductItemRepository productItemRepository;
		private final CustomerRestClient customerRestClient;
		private final ProductRestClient productRestClient;

		public DataInitializer(BillRepository billRepository,
				ProductItemRepository productItemRepository,
				CustomerRestClient customerRestClient,
				ProductRestClient productRestClient) {
			this.billRepository = billRepository;
			this.productItemRepository = productItemRepository;
			this.customerRestClient = customerRestClient;
			this.productRestClient = productRestClient;
		}

		@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2, maxDelay = 30000), retryFor = Exception.class)
		public void initializeData() {
			// Only initialize if no bills exist (avoid duplicates on restart)
			if (billRepository.count() > 0) {
				log.info("ℹ️ Données de facturation existantes, pas d'insertion");
				return;
			}

			log.info("Attempting to initialize billing data...");

			Collection<Customer> customers = customerRestClient.getAllCustomers().getContent();
			Collection<Product> products = productRestClient.getAllProducts().getContent();

			log.info("Found {} customers and {} products", customers.size(), products.size());

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
							.quantity(1 + new Random().nextInt(10))
							.unitPrice(product.getPrice())
							.build();
					productItemRepository.save(productItem);
				});
			});
		}
	}

}
