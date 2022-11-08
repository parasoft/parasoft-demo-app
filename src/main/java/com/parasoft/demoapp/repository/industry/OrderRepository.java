package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findOrderByOrderNumber(String number);

	List<OrderEntity> findAllByRequestedBy(String requestedBy);

    Page<OrderEntity> findAllByRequestedBy(String requestedBy, Pageable pageable);

    Page<OrderEntity> findAllByStatusNotIn(List<OrderStatus> orderStatuses, Pageable pageable);

    int countByRequestedByAndReviewedByPRCHAndStatusNotIn(String requestedBy, boolean reviewedByPRCH, List<OrderStatus> orderStatuses);

    int countByReviewedByAPVAndStatusNotIn(boolean reviewedByAPV, List<OrderStatus> orderStatuses);
}
