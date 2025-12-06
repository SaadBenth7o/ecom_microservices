package org.saad.customerservice;

import org.saad.customerservice.entities.Customer;
import org.saad.customerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.save(Customer.builder()
                    .name("Sarah Johnson").email("sarah.johnson@techcorp.com").build());
            customerRepository.save(Customer.builder()
                    .name("Ahmed El-Mansouri").email("ahmed.elmansouri@innovate.io").build());
            customerRepository.save(Customer.builder()
                    .name("Maria Garcia").email("maria.garcia@globaltech.es").build());
            customerRepository.save(Customer.builder()
                    .name("Yuki Tanaka").email("yuki.tanaka@futuresoft.jp").build());
            customerRepository.save(Customer.builder()
                    .name("Jean Dupont").email("jean.dupont@enterprise.fr").build());
            customerRepository.findAll().forEach(c-> {
                System.out.println("====================================");
                System.out.println("Customer ID: " + c.getId());
                System.out.println("Name: " + c.getName());
                System.out.println("Email: " + c.getEmail());
                System.out.println("====================================");
            });
        };
    }
}


