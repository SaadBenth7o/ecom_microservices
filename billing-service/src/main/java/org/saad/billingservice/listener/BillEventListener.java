package org.saad.billingservice.listener;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.saad.billingservice.entities.Bill;
import org.saad.billingservice.service.BillingEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BillEventListener {

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        BillEventListener.applicationContext = applicationContext;
    }

    @PostPersist
    public void onBillCreated(Bill bill) {
        try {
            BillingEventPublisher publisher = applicationContext.getBean(BillingEventPublisher.class);
            publisher.publishBillCreated(bill);
            log.info("Published billing event for bill ID: {}", bill.getId());
        } catch (Exception e) {
            log.error("Error publishing billing event", e);
        }
    }
}

