/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.parasoft.demoapp.model.industry.CategoryEntity;

/**
 * test for CategoryService
 *
 * @see com.parasoft.demoapp.service.CategoryService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class CategoryServiceSpringTest {

	@Autowired
	CategoryService service;

	/**
	 * test for getCategories(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#getCategories(String, Pageable)
	 */
	@Test
	@Transactional(value = "industryTransactionManager")
	public void testGetCategories() throws Throwable {
		// When
		String searchString = ""; // ignore search String filter
		Pageable pageable = Pageable.unpaged();
		Page<CategoryEntity> result = service.getCategories(searchString, pageable);

		// Then
		assertNotNull(result.getContent());
		
		// Given
		String name = "test";
		service.addNewCategory(name, "description", "imagePath");
		
		// When
		searchString = "test";
		result = service.getCategories(searchString, pageable);

		// Then
		assertNotNull(result.getContent());
		assertEquals(1, result.getTotalElements());
		assertEquals(name, result.getContent().get(0).getName());
		
		// When
		searchString = "es"; 	// fuzzy search
		result = service.getCategories(searchString, pageable);

		// Then
		assertNotNull(result.getContent());
		assertEquals(1, result.getTotalElements());
		assertEquals(name, result.getContent().get(0).getName());
	}

	/**
	 * test for addNewCategories(String, String, String) with blank image path to use the default image.
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	@Transactional(value = "industryTransactionManager")
	public void testAddNewCategories_defaultImage() throws Throwable {
		// Given
		String defaultImage = "/" + IndustryRoutingDataSource.currentIndustry.getValue().toLowerCase() + "/images/defaultImage.png";

		// When
		CategoryEntity categoryOne = service.addNewCategory("DefaultImageCategoryOne", "categoryOne with default image", null);

		// Then
		assertEquals(defaultImage, categoryOne.getImage());

		// When
		CategoryEntity categoryTwo = service.addNewCategory("DefaultImageCategoryTwo", "categoryTwo with default image", " ");

		// Then
		assertEquals(defaultImage, categoryTwo.getImage());

		// When
		CategoryEntity categoryThree = service.addNewCategory("DefaultImageCategoryThree", "categoryThree with default image", "");

		// Then
		assertEquals(defaultImage, categoryThree.getImage());
	}

	/**
	 * test for updateCategory(Long, String, String, String) with blank image path to user default image.
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	@Transactional(value = "industryTransactionManager")
	public void testUpdateCategory_default() throws Throwable {
		// Given
		String name = "DefaultImageCategory";
		String description = "category with default image";
		String imagePath = "static/defense/images/troop.png";
		String defaultImage = "/" + IndustryRoutingDataSource.currentIndustry.getValue().toLowerCase() + "/images/defaultImage.png";

		// When
		CategoryEntity category = service.addNewCategory(name, description, imagePath);

		// Then
		assertEquals(imagePath, category.getImage());

		// When
		String newImagePath = " ";
		service.updateCategory(category.getId(), name, description, newImagePath);

		// Then
		assertEquals(defaultImage, category.getImage());

		// When
		newImagePath = "";
		service.updateCategory(category.getId(), name, description, newImagePath);

		// Then
		assertEquals(defaultImage, category.getImage());

		// When
		newImagePath = null;
		service.updateCategory(category.getId(), name, description, newImagePath);

		// Then
		assertEquals(defaultImage, category.getImage());
	}
}