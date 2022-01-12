package com.parasoft.demoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.parasoft.demoapp.exception.UploadedImageCanNotDeleteException;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.repository.industry.CategoryRepository;
import com.parasoft.demoapp.repository.industry.ItemRepository;

/**
 * test class for CategoryService
 *
 * @see CategoryService
 */
public class CategoryServiceTest {

	@InjectMocks
	CategoryService underTest;

	@Mock
	CategoryRepository categoryRepository;

	@Mock
	ItemRepository itemRepository;

	@Mock
	ImageService imageService;

	@BeforeEach
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for addNewCategory(String, String, String)
	 *
	 * @see CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	public void testAddNewCategory_normal() throws Throwable {
		// Given
		String name = "Tanks";
		String description = "about tanks";
		String imagePath = "/image/path";
		CategoryEntity saveResult = new CategoryEntity(name, description, imagePath);
		doReturn(saveResult).when(categoryRepository).save((CategoryEntity) any());

		// When
		CategoryEntity result = underTest.addNewCategory(name, description, imagePath);

		// Then
		assertNotNull(result);
		assertEquals(name, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(imagePath, result.getImage());
	}

	/**
	 * test for addNewCategory(String, String, String)
	 *
	 * @see CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	public void testAddNewCategory_nameExistsAlready() throws Throwable {
		// Given
		String name = "exist already";
		String description = "about tanks";
		String imagePath = "/image/path";
		doReturn(true).when(categoryRepository).existsByName(anyString());

		// When
		String message = "";
		try {
			underTest.addNewCategory(name, description, imagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_NAME_EXISTS_ALREADY, name), message);
	}

	/**
	 * test for addNewCategory(String, String, String)
	 *
	 * @see CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	public void testAddNewCategory_nullName() throws Throwable {
		// Given
		String name = null;
		String description = "about tanks";
		String imagePath = "/image/path";
		doThrow(NullPointerException.class).when(categoryRepository).save((CategoryEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewCategory(name, description, imagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewCategory(String, String, String)
	 *
	 * @see CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	public void testAddNewCategory_nullDescription() throws Throwable {
		// Given
		String name = "Tanks";
		String description = null;
		String imagePath = "/image/path";
		doThrow(NullPointerException.class).when(categoryRepository).save((CategoryEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewCategory(name, description, imagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewCategory(String, String, String)
	 *
	 * @see CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	public void testAddNewCategory_emptyName() throws Throwable {
		// Given
		String name = " ";
		String description = "about tanks";
		String imagePath = "/image/path";
		doThrow(NullPointerException.class).when(categoryRepository).save((CategoryEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewCategory(name, description, imagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewCategory(String, String, String)
	 *
	 * @see CategoryService#addNewCategory(String, String, String)
	 */
	@Test
	public void testAddNewCategory_emptyDescription() throws Throwable {
		// Given
		String name = "Tanks";
		String description = " ";
		String imagePath = "/image/path";
		doThrow(NullPointerException.class).when(categoryRepository).save((CategoryEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewCategory(name, description, imagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for removeCategory(Long)
	 *
	 * @see CategoryService#removeCategory(Long)
	 */
	@Test
	public void testRemoveCategory_normal() throws Throwable {
		// Given
		doNothing().when(categoryRepository).deleteById(nullable(Long.class));
		doReturn(true).when(categoryRepository).existsById(anyLong());
		Optional<CategoryEntity> optional = Optional.of(mock(CategoryEntity.class));
		doReturn(optional).when(categoryRepository).findById(anyLong());

		// When
		Long categoryId = 0L;
		underTest.removeCategory(categoryId);
	}

	/**
	 * test for removeCategory(Long)
	 *
	 * @see CategoryService#removeCategory(Long)
	 */
	@Test
	public void testRemoveCategory_failToDeleteImage() throws Throwable {
		// Given
		doNothing().when(categoryRepository).deleteById(nullable(Long.class));
		doReturn(true).when(categoryRepository).existsById(anyLong());
		CategoryEntity category = mock(CategoryEntity.class);
		when(category.getImage()).thenReturn("/uploaded_images/**");
		Optional<CategoryEntity> optional = Optional.of(category);
		doReturn(optional).when(categoryRepository).findById(anyLong());

		doReturn(1L).when(imageService).numberOfImageUsed(anyString());

		doThrow(UploadedImageCanNotDeleteException.class).when(imageService).deleteUploadedImageByPath(nullable(String.class));

		// When
		Long categoryId = 0L;
		underTest.removeCategory(categoryId);   // exception is caught by under test method.
	}
	
	/**
	 * test for removeCategory(Long)
	 *
	 * @see CategoryService#removeCategory(Long)
	 */
	@Test
	public void testRemoveCategory_categoryNotExists() throws Throwable {
		// Given
		doReturn(false).when(categoryRepository).existsById(anyLong());

		// When
		Long categoryId = -1L;
		String message = "";
		try {
			underTest.removeCategory(categoryId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId), message);
		
	}

	/**
	 * test for removeCategory(Long)
	 *
	 * @see CategoryService#removeCategory(Long)
	 */
	@Test
	public void testRemoveCategory_categoryHasAtLeastOneItemException() throws Throwable {
		// Given
		doReturn(true).when(categoryRepository).existsById(anyLong());
		doReturn(1L).when(itemRepository).countByCategoryId(anyLong());

		// When
		Long categoryId = 1L;
		String message = "";
		try {
			underTest.removeCategory(categoryId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.THERE_IS_AT_LEAST_ONE_ITEM_IN_THE_CATEGORY, categoryId), message);

	}

	/**
	 * test for removeCategory(Long)
	 *
	 * @see CategoryService#removeCategory(Long)
	 */
	@Test
	public void testRemoveCategory_nullCategoryId() throws Throwable {
		// Given
		doNothing().when(categoryRepository).deleteById(nullable(Long.class));

		// When
		Long categoryId = null;
		String message = "";
		try {
			underTest.removeCategory(categoryId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_normal() throws Throwable {
		// Given
		CategoryEntity entity = new CategoryEntity();
		Optional<CategoryEntity> optional = Optional.of(entity);
		doReturn(optional).when(categoryRepository).findById((Long) any());
		doReturn(false).when(categoryRepository).existsByName(anyString());

		Long categoryId = 0L;
		String newName = "Tanks";
		String newDescription = "about tanks";
		String newImagePath = "/image/path";
		CategoryEntity saveResult = new CategoryEntity(newName, newDescription, newImagePath);
		doReturn(saveResult).when(categoryRepository).save((CategoryEntity) any());

		// When
		CategoryEntity result = underTest.updateCategory(categoryId, newName, newDescription, newImagePath);

		// Then
		assertNotNull(result);
		assertEquals(newName, result.getName());
		assertEquals(newDescription, result.getDescription());
		assertEquals(newImagePath, result.getImage());
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_nameExistsAlready() throws Throwable {
		// Given
		String originalName = "original name";
		String newName = "exist already";
		String description = "about tanks";
		String imagePath = "/image/path";

		CategoryEntity entity = new CategoryEntity();
		entity.setName(originalName);
		Optional<CategoryEntity> optional = Optional.of(entity);
		doReturn(optional).when(categoryRepository).findById((Long) any());

		doReturn(true).when(categoryRepository).existsByName(anyString());

		// When
		String message = "";
		Long categoryId = 0L;
		try {
			underTest.updateCategory(categoryId, newName, description, imagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_NAME_EXISTS_ALREADY, newName), message);
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_nullCategoryId() throws Throwable {
		// Given
		Long categoryId = null;
		String newName = "Tanks";
		String newDescription = "about tanks";
		String newImagePath = "/image/path";

		// When
		String message = "";
		try {
			underTest.updateCategory(categoryId, newName, newDescription, newImagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_nullName() throws Throwable {
		// Given
		Long categoryId = 0L;
		String newName = null;
		String newDescription = "about tanks";
		String newImagePath = "/image/path";

		// When
		String message = "";
		try {
			underTest.updateCategory(categoryId, newName, newDescription, newImagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_nullDescription() throws Throwable {
		// Given
		Long categoryId = 0L;
		String newName = "Tanks";
		String newDescription = null;
		String newImagePath = "/image/path";

		// When
		String message = "";
		try {
			underTest.updateCategory(categoryId, newName, newDescription, newImagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_emptyName() throws Throwable {
		// Given
		Long categoryId = 0L;
		String newName = " ";
		String newDescription = "about tanks";
		String newImagePath = "/image/path";

		// When
		String message = "";
		try {
			underTest.updateCategory(categoryId, newName, newDescription, newImagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateCategory(Long, String, String, String)
	 *
	 * @see CategoryService#updateCategory(Long, String, String, String)
	 */
	@Test
	public void testUpdateCategory_emptyDescription() throws Throwable {
		// Given
		Long categoryId = 0L;
		String newName = "Tanks";
		String newDescription = " ";
		String newImagePath = "/image/path";

		// When
		String message = "";
		try {
			underTest.updateCategory(categoryId, newName, newDescription, newImagePath);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for getByCategoryId(Long)
	 *
	 * @see CategoryService#getByCategoryId(Long)
	 */
	@Test
	public void testGetByCategoryId_normal() throws Throwable {
		// Given
		String name = "Tanks";
		String description = "about tanks";
		String imagePath = "/image/path";
		CategoryEntity entity = new CategoryEntity(name, description, imagePath);
		Optional<CategoryEntity> optional = Optional.of(entity);
		doReturn(optional).when(categoryRepository).findById(nullable(Long.class));

		// When
		Long id = 0L;
		CategoryEntity result = underTest.getByCategoryId(id);

		// Then
		assertNotNull(result);
		assertEquals(name, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(imagePath, result.getImage());
	}

	/**
	 * test for getByCategoryId(Long)
	 *
	 * @see CategoryService#getByCategoryId(Long)
	 */
	@Test
	public void testGetByCategoryId_notFound() throws Throwable {
		// Given
		Optional<CategoryEntity> optional = Optional.ofNullable(null);
		doReturn(optional).when(categoryRepository).findById(nullable(Long.class));

		// When
		Long id = 0L;
		String message = "";
		try {
			underTest.getByCategoryId(id);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, id), message);
	}

	/**
	 * test for getByCategoryId(Long)
	 *
	 * @see CategoryService#getByCategoryId(Long)
	 */
	@Test
	public void testGetByCategoryId_nullId() throws Throwable {
		// Given
		Optional<CategoryEntity> optional = Optional.ofNullable(null);
		doReturn(optional).when(categoryRepository).findById(nullable(Long.class));

		// When
		Long id = null;
		String message = "";
		try {
			underTest.getByCategoryId(id);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for getByCategoryName(String)
	 *
	 * @see CategoryService#getByCategoryName(String)
	 */
	@Test
	public void testGetByCategoryName_normal() throws Throwable {
		// Given
		CategoryEntity findByNameResult = mock(CategoryEntity.class);
		when(categoryRepository.findByName(nullable(String.class))).thenReturn(findByNameResult);

		// When
		String name = "Tanks";
		CategoryEntity result = underTest.getByCategoryName(name);

		// Then
		assertNotNull(result);
	}

	/**
	 * test for getByCategoryName(String)
	 *
	 * @see CategoryService#getByCategoryName(String)
	 */
	@Test
	public void testGetByCategoryName_noCategory() throws Throwable {
		// Given
		CategoryEntity findByNameResult = null;
		when(categoryRepository.findByName(nullable(String.class))).thenReturn(findByNameResult);

		// When
		String name = "not exist";
		String message = "";
		try {
			underTest.getByCategoryName(name);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_NAME_NOT_FOUND, name), message);
	}

	/**
	 * test for getByCategoryName(String)
	 *
	 * @see CategoryService#getByCategoryName(String)
	 */
	@Test
	public void testGetByCategoryName_nullName() throws Throwable {

		// When
		String name = null;
		String message = "";
		try {
			underTest.getByCategoryName(name);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for getByCategoryName(String)
	 *
	 * @see CategoryService#getByCategoryName(String)
	 */
	@Test
	public void testGetByCategoryName_emptyName() throws Throwable {

		// When
		String name = "";
		String message = "";
		try {
			underTest.getByCategoryName(name);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for getAllCategories()
	 *
	 * @see CategoryService#getCategories(String, Pageable)
	 */
	@Test
	public void testGetCategories_normal() throws Throwable {
		// Given
		List<CategoryEntity> Categories = new ArrayList<>();
		CategoryEntity item = mock(CategoryEntity.class);
		Categories.add(item);

		Page<CategoryEntity> page = new PageImpl<>(Categories);
		doReturn(page).when(categoryRepository).findAll((Specification)any(), (Pageable)any());

		// When
		String searchString = "";
		Page<CategoryEntity> result = underTest.getCategories(searchString ,Pageable.unpaged());

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
	}

	/**
	 * Test for searchCategoriesByNameOrDescription(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#searchCategoriesByNameOrDescription(String, Pageable)
	 */
	@Test
	public void testSearchCategoriesByNameOrDescription_normal() throws Throwable {
		// Given
		List<CategoryEntity> content = new ArrayList<>();
		content.add(new CategoryEntity());
		Pageable pageable = Pageable.unpaged();
		int totalElement = 2;

		Page<CategoryEntity> page = new PageImpl<>(content, pageable, totalElement);
		doReturn(page).when(categoryRepository).findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
				nullable(String.class), nullable(String.class), nullable(Pageable.class));

		// When
		String key = "Tank";
		Page<CategoryEntity> result = underTest.searchCategoriesByNameOrDescription(key, pageable);

		// Then
		assertNotNull(result);
		Assert.assertEquals(totalElement, result.getTotalElements());
		Assert.assertEquals(content, result.getContent());
	}

	/**
	 * Test for searchCategoriesByNameOrDescription(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#searchCategoriesByNameOrDescription(String, Pageable)
	 */
	@Test
	public void testSearchCategoriesByNameOrDescription_nullKey() throws Throwable {
		// Given
		Pageable pageable = Pageable.unpaged();

		// When
		String key = null;
		String message = "";
		try {
			underTest.searchCategoriesByNameOrDescription(key, pageable);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assert.assertEquals(AssetMessages.SEARCH_FIELD_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for searchCategoriesByNameOrDescription(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#searchCategoriesByNameOrDescription(String, Pageable)
	 */
	@Test
	public void testSearchCategoriessByNameOrDescription_emptyKey() throws Throwable {
		// Given
		Pageable pageable = Pageable.unpaged();

		// When
		String key = "  ";
		String message = "";
		try {
			underTest.searchCategoriesByNameOrDescription(key, pageable);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		Assert.assertEquals(AssetMessages.SEARCH_FIELD_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for numberOfImageUsedInCategories(String)
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#numberOfImageUsedInCategories(String)
	 */
	@Test
	public void testNumberOfImageUsedInCategories_normal() throws Throwable {
		// Given
		when(categoryRepository.countByImage(anyString())).thenReturn(1L);

		// When
		String imagePath = "/uploaded_images/defense/123.jpg";
		long result = underTest.numberOfImageUsedInCategories(imagePath);

		// Then
		Assert.assertEquals(1l, result);
	}

	/**
	 * Test for numberOfImageUsedInCategories(String)
	 *
	 * @see com.parasoft.demoapp.service.CategoryService#numberOfImageUsedInCategories(String)
	 */
	@Test
	public void testNumberOfImageUsedInCategories_nullImagePath() throws Throwable {

		// When
		String imagePath = null;
		long result = underTest.numberOfImageUsedInCategories(imagePath);

		// Then
		Assert.assertEquals(0, result);
	}
}