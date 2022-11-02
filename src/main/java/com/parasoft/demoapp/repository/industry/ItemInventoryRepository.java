package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemInventoryRepository extends JpaRepository<ItemInventoryEntity, Long> {

    ItemInventoryEntity findByItemId(Long itemId);

    @Query("select inStock FROM ItemInventoryEntity where itemId=?1")
    Integer findInStockByItemId(Long id);

}
