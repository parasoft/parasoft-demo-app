package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingCartRepository extends JpaRepository<CartItemEntity, Long> {

    void deleteByUserId(Long userId);
    
    void deleteByUserIdAndItemId(Long userId, Long itemId);

    List<CartItemEntity> findAllByUserId(Long userId);

    CartItemEntity findByUserIdAndItemId(Long userId, Long itemId);

    boolean existsByItemIdAndUserId(Long itemId, Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByItemId(Long itemId);
}
