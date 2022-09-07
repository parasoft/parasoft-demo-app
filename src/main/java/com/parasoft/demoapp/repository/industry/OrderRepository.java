package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.OrderEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findOrderByOrderNumber(String number);

	List<OrderEntity> findAllByRequestedBy(String requestedBy);

    Page<OrderEntity> findAllByRequestedBy(String requestedBy, Pageable pageable);

    int countByReviewedByPRCH(boolean b);

    int countByReviewedByAPV(boolean b);
}
