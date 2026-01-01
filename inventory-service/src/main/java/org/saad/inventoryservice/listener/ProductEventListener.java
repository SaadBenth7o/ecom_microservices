package org.saad.inventoryservice.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.saad.inventoryservice.entities.Product;
import org.saad.inventoryservice.service.InventoryEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductEventListener {

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        ProductEventListener.applicationContext = applicationContext;
    }

    @PostPersist
    public void onProductCreated(Product product) {
        try {
            InventoryEventPublisher publisher = applicationContext.getBean(InventoryEventPublisher.class);
            publisher.publishProductEvent(product, "PRODUCT_CREATED");
            log.info("Published inventory event for product ID: {}", product.getId());
        } catch (Exception e) {
            log.error("Error publishing inventory event", e);
        }
    }

    @PostUpdate
    public void onProductUpdated(Product product) {
        try {
            InventoryEventPublisher publisher = applicationContext.getBean(InventoryEventPublisher.class);
            publisher.publishProductEvent(product, "PRODUCT_UPDATED");
            log.info("Published inventory update event for product ID: {}", product.getId());
        } catch (Exception e) {
            log.error("Error publishing inventory update event", e);
        }
    }
}

