package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.OrderEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findOrderByOrderNumber(String number);

	List<OrderEntity> findAllByUserId(Long userId);

    Page<OrderEntity> findAllByUserId(Long userId, Pageable pageable);
}
