package com.parasoft.demoapp.utilfortest;

import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.repository.industry.OrderRepository;

public class OrderUtilForTest {

    public static void waitChangeForOrderStatus(String orderNumber,
                                                OrderRepository orderRepository,
                                                OrderStatus unexpectedStatus, int maxSeconds) throws InterruptedException {
        int count = 0;
        while(orderRepository.findOrderByOrderNumber(orderNumber).getStatus() == unexpectedStatus) {
            if(count == maxSeconds) {
                throw new RuntimeException("Order status is not changed in a specific time. " +
                        "Status change is an asynchronous flow sine the MQ introduction between order service and inventory server.");
            }
            Thread.sleep(1000);
            count++;
        }
    }
}
