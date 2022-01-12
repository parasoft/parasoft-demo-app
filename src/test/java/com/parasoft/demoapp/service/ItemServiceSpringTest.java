package com.parasoft.demoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class ItemServiceSpringTest {

    @Autowired
    ItemService underTest;

    @Autowired
    CategoryService categoryService;

    /**
     * test for getItems(Long, RegionType, String, Pageable)
     *
     * @see ItemService#getItems(Long, RegionType[], String, Pageable)
     */
    @Transactional(value = "industryTransactionManager")
    @Test
    public void testGetItems_normal() throws Throwable {
        // Given
        CategoryEntity categoryOne = categoryService.addNewCategory("CI", "description", "imagePath");
        CategoryEntity categoryTwo = categoryService.addNewCategory("CII", "description", "imagePath");

        underTest.addNewItem("I", "description", categoryOne.getId(), 10, "imagePath", RegionType.LOCATION_1);
        underTest.addNewItem("II", "description", categoryOne.getId(), 9, "imagePath", RegionType.LOCATION_2);
        underTest.addNewItem("III", "description", categoryTwo.getId(), 9, "imagePath", RegionType.LOCATION_2);
        underTest.addNewItem("IV", "description", categoryTwo.getId(), 9, "imagePath", RegionType.LOCATION_2);

        // When
        RegionType[] regions1 = {RegionType.LOCATION_2};
        Page<ItemEntity> result = underTest.getItems(categoryTwo.getId(), regions1, null, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(2, result.getSize());

        // When
        RegionType[] regions2 = {RegionType.LOCATION_1};
        result = underTest.getItems(categoryOne.getId(), regions2, null, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(1, result.getSize());

        // When
        RegionType[] regions3 = {RegionType.LOCATION_1, RegionType.LOCATION_2};
        result = underTest.getItems(categoryOne.getId(), regions3, null, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(2, result.getSize());

        // When
        RegionType[] regions4 = null;
        result = underTest.getItems(categoryOne.getId(), regions4, null, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(2, result.getSize());

        // When
        RegionType[] regions5 = {};
        result = underTest.getItems(categoryOne.getId(), regions5, null, Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(2, result.getSize());

        // When
        String message = "";
        RegionType[] regions6 = {RegionType.LOCATION_3};
        try {
            underTest.getItems(categoryOne.getId(), regions6, "III", Pageable.unpaged());
        } catch (Exception e) {
            message = e.getMessage();
        }

        // Then
        assertEquals(AssetMessages.NO_ITEMS, message);

        // When
        RegionType[] regions7 = {};
        result = underTest.getItems(categoryOne.getId(), regions7, "description", Pageable.unpaged());

        // Then
        assertNotNull(result);
        assertEquals(2, result.getSize());
    }

    /**
     * test for addNewItem(String, String, Long, Integer, String, RegionType) with blank image to use default image
     *
     * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
     */
    @Transactional(value = "industryTransactionManager")
    @Test
    public void testAddItems_defaultImage() throws Throwable {
        // Given
        CategoryEntity categoryOne = categoryService.addNewCategory("CI", "description", "imagePath");
        String defaultImage = "/" + IndustryRoutingDataSource.currentIndustry.getValue().toLowerCase() + "/images/defaultImage.png";

        // When
        ItemEntity itemOne = underTest.addNewItem("I", "description", categoryOne.getId(), 10, "", RegionType.LOCATION_1);
        ItemEntity itemTwo = underTest.addNewItem("II", "description", categoryOne.getId(), 9, null, RegionType.LOCATION_2);
        ItemEntity itemThree = underTest.addNewItem("III", "description", categoryOne.getId(), 9, " ", RegionType.LOCATION_2);

        // Then
        assertEquals(defaultImage, itemOne.getImage());
        assertEquals(defaultImage, itemTwo.getImage());
        assertEquals(defaultImage, itemThree.getImage());
    }

    /**
     * test for updateItem(Long, String, String, Long, Integer, String, RegionType) with blank image to use default image.
     *
     * @see com.parasoft.demoapp.service.ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
     */
    @Test
    @Transactional(value = "industryTransactionManager")
    public void testUpdate_defaultImage() throws Throwable {
        // Given
        CategoryEntity categoryOne = categoryService.addNewCategory("CI", "description", "imagePath");
        String defaultImage = "/" + IndustryRoutingDataSource.currentIndustry.getValue().toLowerCase() + "/images/defaultImage.png";

        ItemEntity itemOne = underTest.addNewItem("I", "description", categoryOne.getId(), 10, "imagePath", RegionType.LOCATION_1);
        ItemEntity itemTwo = underTest.addNewItem("II", "description", categoryOne.getId(), 9, "imagePath", RegionType.LOCATION_2);
        ItemEntity itemThree = underTest.addNewItem("III", "description", categoryOne.getId(), 9, "imagePath", RegionType.LOCATION_2);

        // When
        underTest.updateItem(itemOne.getId(), "I", "description", categoryOne.getId(), 10, " ", RegionType.LOCATION_1);
        underTest.updateItem(itemTwo.getId(), "II", "description", categoryOne.getId(), 9, null, RegionType.LOCATION_2);
        underTest.updateItem(itemThree.getId(), "III", "description", categoryOne.getId(), 9, "", RegionType.LOCATION_2);

        // Then
        assertEquals(defaultImage, itemOne.getImage());
        assertEquals(defaultImage, itemTwo.getImage());
        assertEquals(defaultImage, itemThree.getImage());
    }

}
