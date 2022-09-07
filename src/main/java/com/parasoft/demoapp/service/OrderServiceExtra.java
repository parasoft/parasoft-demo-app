package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.UnreviewedOrderNumberResponseDTO;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceExtra {

    @Autowired
    private OrderRepository orderRepository;

    public UnreviewedOrderNumberResponseDTO getUnreviewedOrderNumber() {
        int unreviewedByPurchaserOrderNumber = orderRepository.countByReviewedByPRCH(false);
        int unreviewedByApproverOrderNumber = orderRepository.countByReviewedByAPV(false);
        return new UnreviewedOrderNumberResponseDTO(unreviewedByApproverOrderNumber, unreviewedByPurchaserOrderNumber);
    }
}
