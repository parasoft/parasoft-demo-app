package com.parasoft.demoapp.repository.industry;

import com.parasoft.demoapp.model.industry.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {

    CategoryEntity findByName(String categoryName);

    Page<CategoryEntity> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKey, String descriptionKey, Pageable pageable);

    boolean existsByName(String searchName);

    long countByImage(String imagePathOfCategory);
}
