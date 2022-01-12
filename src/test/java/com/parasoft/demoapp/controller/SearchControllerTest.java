/**
 * 
 */
package com.parasoft.demoapp.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.ItemService;

/**
 * Test class for SearchController
 *
 * @see com.parasoft.demoapp.controller.SearchController
 */
public class SearchControllerTest {

	// Object under test
	@InjectMocks
	SearchController underTest;

	@Mock
	ItemService itemService;

	@Mock
	CategoryService categoryService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for searchItems(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.controller.SearchController#searchItems(String, Pageable)
	 */
	@Test
	public void testSearchItems_normal() throws Throwable {
		// Given
		List<ItemEntity> content = new ArrayList<>();
		content.add(new ItemEntity());
		Pageable pageable = Pageable.unpaged();
		int totalElement = 2;
		
		Page<ItemEntity> page = new PageImpl<>(content, pageable, totalElement);
		
		doReturn(page).when(itemService)
				.searchItemsByNameOrDescription(nullable(String.class), nullable(Pageable.class));

		// When
		String key = "key";
		ResponseResult<PageInfo<ItemEntity>> result = underTest.searchItems(key, pageable);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(content, result.getData().getContent());
		assertEquals(totalElement, result.getData().getTotalElements());
	}
	
	/**
	 * Test for searchItems(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.controller.SearchController#searchItems(String, Pageable)
	 */
	@Test(expected = ParameterException.class)
	public void testSearchItems_parameterException() throws Throwable {
		// Given
		doThrow(ParameterException.class).when(itemService)
				.searchItemsByNameOrDescription(nullable(String.class), nullable(Pageable.class));

		// When
		String key = "key";
		Pageable pageable = Pageable.unpaged();
		underTest.searchItems(key, pageable);
	}

	/**
	 * Test for searchCategories(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.controller.SearchController#searchCategories(String, Pageable)
	 */
	@Test
	public void testSearchCategories_normal() throws Throwable {
		// Given
		List<CategoryEntity> content = new ArrayList<>();
		content.add(new CategoryEntity());
		Pageable pageable = Pageable.unpaged();
		int totalElement = 2;

		Page<CategoryEntity> page = new PageImpl<>(content, pageable, totalElement);

		doReturn(page).when(categoryService)
				.searchCategoriesByNameOrDescription(nullable(String.class), nullable(Pageable.class));

		// When
		String key = "key";
		ResponseResult<PageInfo<CategoryEntity>> result = underTest.searchCategories(key, pageable);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(content, result.getData().getContent());
		assertEquals(totalElement, result.getData().getTotalElements());
	}

	/**
	 * Test for searchCategories(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.controller.SearchController#searchCategories(String, Pageable)
	 */
	@Test(expected = ParameterException.class)
	public void testSearchCategories_parameterException() throws Throwable {
		// Given
		doThrow(ParameterException.class).when(categoryService)
				.searchCategoriesByNameOrDescription(nullable(String.class), nullable(Pageable.class));

		// When
		String key = "key";
		Pageable pageable = Pageable.unpaged();
		underTest.searchCategories(key, pageable);
	}
}