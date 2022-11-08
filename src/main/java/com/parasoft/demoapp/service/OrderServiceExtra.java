package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.UnreviewedOrderNumberResponseDTO;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceExtra {

    @Autowired
    private OrderRepository orderRepository;

    public UnreviewedOrderNumberResponseDTO getUnreviewedOrderNumber(String currentUsername) {
        List<OrderStatus> excludedOrderStatuses = new ArrayList<>();
        excludedOrderStatuses.add(OrderStatus.SUBMITTED);
        excludedOrderStatuses.add(OrderStatus.CANCELED);
        int unreviewedByPurchaserOrderNumber =
                orderRepository.countByRequestedByAndReviewedByPRCHAndStatusNotIn(currentUsername, false, excludedOrderStatuses);
        int unreviewedByApproverOrderNumber =
                orderRepository.countByReviewedByAPVAndStatusNotIn(false, excludedOrderStatuses);
        return new UnreviewedOrderNumberResponseDTO(unreviewedByApproverOrderNumber, unreviewedByPurchaserOrderNumber);
    }
}
