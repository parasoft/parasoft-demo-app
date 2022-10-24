package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemInventoryRepository extends JpaRepository<ItemInventoryEntity, Long> {
    ItemInventoryEntity findByItemId(Long itemId);
}
