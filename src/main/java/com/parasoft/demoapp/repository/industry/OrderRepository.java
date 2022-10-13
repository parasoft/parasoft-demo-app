package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    OrderEntity findOrderByOrderNumber(String number);

	List<OrderEntity> findAllByRequestedBy(String requestedBy);

    Page<OrderEntity> findAllByRequestedBy(String requestedBy, Pageable pageable);

    @Query("select count(reviewedByPRCH) FROM OrderEntity where reviewedByPRCH=false and requestedBy=:requestedBy")
    int countByReviewedByPRCH(@Param("requestedBy") String requestedBy);

    int countByReviewedByAPV(boolean b);
}
