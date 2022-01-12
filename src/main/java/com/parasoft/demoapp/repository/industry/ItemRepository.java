package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, JpaSpecificationExecutor<ItemEntity> {

    @Override
    Page<ItemEntity> findAll(Pageable pageable);

    List<ItemEntity> findAllByRegion(RegionType region);

    ItemEntity findByName(String itemName);

    boolean existsByName(String name);

    List<ItemEntity> findAllByCategoryId(Long categoryId);

    void deleteByName(String name);

    long countByCategoryId(Long categoryId);

    @Query("select inStock FROM ItemEntity where id=?1")
    Integer findInStockById(Long id);

    Page<ItemEntity> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKey, String descriptionKey, Pageable pageable);

    long countByImage(String imagePath);
}
