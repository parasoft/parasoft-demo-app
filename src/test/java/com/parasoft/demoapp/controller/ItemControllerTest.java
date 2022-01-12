package com.parasoft.demoapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.parasoft.demoapp.dto.ItemsDTO;
import com.parasoft.demoapp.exception.CategoryNotFoundException;
import com.parasoft.demoapp.exception.ItemNameExistsAlreadyException;
import com.parasoft.demoapp.exception.ItemNotFoundException;
import com.parasoft.demoapp.exception.ParameterException;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.service.GlobalPreferencesService;
import com.parasoft.demoapp.service.ItemService;

/**
 * Test class for ItemController
 *
 * @see com.parasoft.demoapp.controller.ItemController
 */
public class ItemControllerTest {

	@InjectMocks
	ItemController underTest;

	@Mock
	ItemService itemService;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for addNewItem(ItemsDTO) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#addNewItem(ItemsDTO)
	 */
	@Test
	public void testAddNewItem_normal() throws Throwable {
		// Given
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName("AddTest");
		itemsDto.setDescription("AddTestDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(0);
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		ItemEntity addItemResult = mock(ItemEntity.class);
		doReturn(addItemResult).when(itemService).addNewItem(anyString(), anyString(), anyLong(), anyInt(), anyString(),
				any(RegionType.class));

		// when
		ResponseResult<ItemEntity> result = underTest.addNewItem(itemsDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for addNewItem(ItemsDTO) with ItemNameExistsAlreadyException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#addNewItem(ItemsDTO)
	 */
	@Test(expected = ItemNameExistsAlreadyException.class)
	public void testAddNewItem_exception_itemNameExistsAlready() throws Throwable {
		// Given
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName("ExistsAlready");
		itemsDto.setDescription("ExistsAlreadyDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(0);
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		when(itemService.addNewItem(anyString(), anyString(), anyLong(), anyInt(), anyString(), any(RegionType.class)))
				.thenThrow(ItemNameExistsAlreadyException.class);

		// When
		underTest.addNewItem(itemsDto);
	}

	/**
	 * Test for addNewItem(ItemsDTO) with CategoryNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#addNewItem(ItemsDTO)
	 */
	@Test(expected = CategoryNotFoundException.class)
	public void testAddNewItem_exception_categoryNotFound() throws Throwable {
		// Given
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName("CategoryNotFound");
		itemsDto.setDescription("CategoryNotFoundDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(0);
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		when(itemService.addNewItem(anyString(), anyString(), anyLong(), anyInt(), anyString(), any(RegionType.class)))
				.thenThrow(CategoryNotFoundException.class);

		// When
		underTest.addNewItem(itemsDto);
	}

	/**
	 * Test for addNewItem(ItemsDTO) with ParameterException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#addNewItem(ItemsDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testAddNewItem_parameterException() throws Throwable {
		// Given
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName(" "); // empty
		itemsDto.setDescription("CategoryNotFoundDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(-10); // negative
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		when(itemService.addNewItem(anyString(), anyString(), anyLong(), anyInt(), anyString(), any(RegionType.class)))
				.thenThrow(ParameterException.class);

		// When
		underTest.addNewItem(itemsDto);
	}

	/**
	 * Test for deleteItemById(Long) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#deleteItemById(Long)
	 */
	@Test
	public void testDeleteItemById_normal() throws Throwable {
		// Given
		Long itemId = 0L;
		doNothing().when(itemService).removeItemById(anyLong());

		// When
		ResponseResult<Long> result = underTest.deleteItemById(itemId);

		// Then
		assertNotNull(result);
		assertEquals(itemId, result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for deleteItemById(Long) with ItemNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#deleteItemById(Long)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testDeleteItemById_exception_itemNotFound() throws Throwable {
		// Given
		Long itemId = -1L;
		doThrow(ItemNotFoundException.class).when(itemService).removeItemById(anyLong());

		// When
		underTest.deleteItemById(itemId);
	}

	/**
	 * Test for deleteItemById(Long) with ParameterException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#deleteItemById(Long)
	 */
	@Test(expected = ParameterException.class)
	public void testDeleteItemById_parameterException() throws Throwable {
		// Given
		Long itemId = -1L;
		doThrow(ParameterException.class).when(itemService).removeItemById(anyLong());

		// When
		underTest.deleteItemById(itemId);
	}

	/**
	 * Test for deleteItemByName(String) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#deleteItemByName(String)
	 */
	@Test
	public void testDeleteItemByName_normal() throws Throwable {
		// Given
		String itemName = "ItemName";
		doNothing().when(itemService).removeItemByName(anyString());

		// When
		ResponseResult<String> result = underTest.deleteItemByName(itemName);

		// Then
		assertNotNull(result);
		assertEquals(itemName, result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for deleteItemByName(String) with ItemNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#deleteItemByName(String)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testDeleteItemByName_exception_itemNotFound() throws Throwable {
		// Given
		String itemName = "ItemName";
		doThrow(ItemNotFoundException.class).when(itemService).removeItemByName(anyString());

		// When
		underTest.deleteItemByName(itemName);
	}

	/**
	 * Test for deleteItemByName(String) with ParameterException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#deleteItemByName(String)
	 */
	@Test(expected = ParameterException.class)
	public void testDeleteItemByName_parameterException() throws Throwable {
		// Given
		String itemName = " "; // empty
		doThrow(ParameterException.class).when(itemService).removeItemByName(anyString());

		// When
		underTest.deleteItemByName(itemName);
	}

	/**
	 * Test for updateItemById(Long, ItemsDTO) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#updateItemById(Long, ItemsDTO)
	 */
	@Test
	public void testUpdateItemById_normal() throws Throwable {
		// Given
		Long itemId = 0L;
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName("UpdateItem");
		itemsDto.setDescription("UpdateItemDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(0);
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		ItemEntity updateItemResult = mock(ItemEntity.class);
		doReturn(updateItemResult).when(itemService).updateItem(anyLong(), anyString(), anyString(), anyLong(),
				anyInt(), anyString(), any(RegionType.class));

		// When
		ResponseResult<ItemEntity> result = underTest.updateItemById(itemId, itemsDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for updateItemById(Long, ItemsDTO) with ItemNameExistsAlreadyException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#updateItemById(Long, ItemsDTO)
	 */
	@Test(expected = ItemNameExistsAlreadyException.class)
	public void testUpdateItemById_exception_itemNameExistsAlready() throws Throwable {
		// Given
		Long itemId = 0L;
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName("UpdateItem");
		itemsDto.setDescription("UpdateItemDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(0);
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		doThrow(ItemNameExistsAlreadyException.class).when(itemService).updateItem(anyLong(), anyString(), anyString(),
				anyLong(), anyInt(), anyString(), any(RegionType.class));

		// When
		underTest.updateItemById(itemId, itemsDto);
	}

	/**
	 * Test for updateItemById(Long, ItemsDTO) with ParameterException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#updateItemById(Long, ItemsDTO)
	 */
	@Test(expected = ParameterException.class)
	public void testUpdateItemById_parameterException() throws Throwable {
		// Given
		Long itemId = 0L;
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName(" "); // empty
		itemsDto.setDescription("UpdateItemDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(-10); // negative
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		doThrow(ParameterException.class).when(itemService).updateItem(anyLong(), anyString(), anyString(),
				anyLong(), anyInt(), anyString(), any(RegionType.class));

		// When
		underTest.updateItemById(itemId, itemsDto);
	}

	/**
	 * Test for updateItemById(Long, ItemsDTO) with ItemNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#updateItemById(Long, ItemsDTO)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testUpdateItemById_exception_itemNotFound() throws Throwable {
		// Given
		Long itemId = 0L;
		ItemsDTO itemsDto = new ItemsDTO();
		itemsDto.setName("UpdateItem");
		itemsDto.setDescription("UpdateItemDesc");
		itemsDto.setCategoryId(0L);
		itemsDto.setInStock(0);
		itemsDto.setImagePath("/image/path");
		itemsDto.setRegion(RegionType.UNITED_STATES);
		doThrow(ItemNotFoundException.class).when(itemService).updateItem(anyLong(), anyString(), anyString(),
				anyLong(), anyInt(), anyString(), any(RegionType.class));

		// When
		underTest.updateItemById(itemId, itemsDto);
	}

	/**
	 * Test for updateItemInStock(Long, Integer) with normal
	 *
	 * @see ItemController#updateItemInStock(Long, Integer)
	 */
	@Test
	public void testUpdateItemInStock_normal() throws Throwable {
		// Given
		Long itemId = 1L;
		Integer newInStock = 10;
		ItemEntity getItemByIdResult = mock(ItemEntity.class);
		doReturn(getItemByIdResult).when(itemService).updateItemInStock(anyLong(), any());

		// When
		ResponseResult<ItemEntity> result = underTest.updateItemInStock(itemId, newInStock);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for updateItemInStock(Long, Integer) with ParameterException
	 *
	 * @see ItemController#updateItemInStock(Long, Integer)
	 */
	@Test(expected = ParameterException.class)
	public void testUpdateItemInStock_parameterException() throws Throwable {
		// Given
		Long itemId = 1L;
		Integer newInStock = 10;
		doThrow(ParameterException.class).when(itemService).updateItemInStock(any(), any());

		// When
		underTest.updateItemInStock(itemId, newInStock);

	}

	/**
	 * Test for updateItemInStock(Long, Integer) with
	 *
	 * @see ItemController#updateItemInStock(Long, Integer)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testUpdateItemInStock_exception_itemNotFound() throws Throwable {
		// Given
		Long itemId = 1L;
		Integer newInStock = 10;
		doThrow(ItemNotFoundException.class).when(itemService).updateItemInStock(any(), any());

		// When
		underTest.updateItemInStock(itemId, newInStock);
	}

	/**
	 * Test for getItemById(Long) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItemById(Long)
	 */
	@Test
	public void testGetItemById_normal() throws Throwable {
		// Given
		Long id = 0L;
		ItemEntity getItemByIdResult = mock(ItemEntity.class);
		doReturn(getItemByIdResult).when(itemService).getItemById(anyLong());

		// When
		ResponseResult<ItemEntity> result = underTest.getItemById(id);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getItemById(Long) with exception
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItemById(Long)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testGetItemById_exception_itemNotFound() throws Throwable {
		// Given
		Long itemId = 0L;
		doThrow(ItemNotFoundException.class).when(itemService).getItemById(anyLong());

		// When
		underTest.getItemById(itemId);
	}

	/**
	 * Test for getItemById(Long) with ParameterException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItemById(Long)
	 */
	@Test(expected = ParameterException.class)
	public void testGetItemById_parameterException() throws Throwable {
		// Given
		Long itemId = -1L;
		doThrow(ParameterException.class).when(itemService).getItemById(anyLong());

		// When
		underTest.getItemById(itemId);
	}

	/**
	 * Test for getItemByName(String) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItemByName(String)
	 */
	@Test
	public void testGetItemByName_normal() throws Throwable {
		// Given
		String itemName = "ItemName";
		ItemEntity getItemByNameResult = mock(ItemEntity.class);
		doReturn(getItemByNameResult).when(itemService).getItemByName(anyString());

		// When
		ResponseResult<ItemEntity> result = underTest.getItemByName(itemName);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
	}

	/**
	 * Test for getItemByName(String) with ParameterException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItemByName(String)
	 */
	@Test(expected = ParameterException.class)
	public void testGetItemByName_parameterException() throws Throwable {
		// Given
		String itemName = " "; // empty
		doThrow(ParameterException.class).when(itemService).getItemByName(anyString());

		// When
		underTest.getItemByName(itemName);
	}


	/**
	 * Test for getItemByName(String) with ItemNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItemByName(String)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testGetItemByName_exception_itemNotFound() throws Throwable {
		// Given
		String itemName = "ItemName";
		doThrow(ItemNotFoundException.class).when(itemService).getItemByName(anyString());

		// When
		underTest.getItemByName(itemName);
	}

	/**
	 * Test for getItems(Long, RegionType, String, Pageable) with normal
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItems(Long, RegionType[], String, Pageable)
	 */
	@Test
	public void testGetItems_normal() throws Throwable {
		// Given
		Page<ItemEntity> getItemsResult = mockPage();
		doReturn(getItemsResult).when(itemService).getItems(anyLong(), any(RegionType[].class), anyString(),
				any(Pageable.class));

		// When
		Long categoryId = 0L;
		RegionType[] regions = {RegionType.UNITED_STATES};
		String searchString = "SearchString";
		Pageable pageable = mock(Pageable.class);
		ResponseResult<PageInfo<ItemEntity>> result = underTest.getItems(categoryId, regions, searchString, pageable);

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(1, result.getData().getContent().size());
		assertEquals(12, result.getData().getNumber());
		assertEquals(11, result.getData().getNumberOfElements());
		assertEquals(1, result.getData().getSize());
		assertEquals(20, result.getData().getTotalElements());
		assertEquals(2, result.getData().getTotalPages());
		assertEquals("id: ASC", result.getData().getSort());
	}

	/**
	 * Helper method to generate and configure mock of Sort
	 */
	private static Sort mockSort() throws Throwable {
		Sort getSortResult = mock(Sort.class);
		when(getSortResult.toString()).thenReturn("id: ASC");
		
		return getSortResult;
	}

	/**
	 * Helper method to generate and configure mock of Page
	 */
	private static Page<ItemEntity> mockPage() throws Throwable {
		Page<ItemEntity> getItemsResult = mock(Page.class);
		List<ItemEntity> getContentResult = new ArrayList<>();
		ItemEntity item = mock(ItemEntity.class);
		getContentResult.add(item);
		doReturn(getContentResult).when(getItemsResult).getContent();

		int getNumberResult = 12;
		when(getItemsResult.getNumber()).thenReturn(getNumberResult);

		int getNumberOfElementsResult = 11;
		when(getItemsResult.getNumberOfElements()).thenReturn(getNumberOfElementsResult);

		int getSizeResult = 1;
		when(getItemsResult.getSize()).thenReturn(getSizeResult);

		Sort getSortResult = mockSort();
		when(getItemsResult.getSort()).thenReturn(getSortResult);

		long getTotalElementsResult = 20;
		when(getItemsResult.getTotalElements()).thenReturn(getTotalElementsResult);

		int getTotalPagesResult = 2;
		when(getItemsResult.getTotalPages()).thenReturn(getTotalPagesResult);
		return getItemsResult;
	}
	
	/**
	 * Test for getItems(Long, RegionType, String, Pageable) with ItemNotFoundException
	 *
	 * @see com.parasoft.demoapp.controller.ItemController#getItems(Long, RegionType[], String, Pageable)
	 */
	@Test(expected = ItemNotFoundException.class)
	public void testGetItems_exception_itemNotFound() throws Throwable {
		// Given
		Long categoryId = 0L;
		RegionType[] regions = {RegionType.UNITED_STATES};
		String searchString = "SearchString";
		Pageable pageable = mock(Pageable.class);
		doThrow(ItemNotFoundException.class).when(itemService).getItems(anyLong(), any(RegionType[].class), anyString(),
				any(Pageable.class));
		
		// When
		underTest.getItems(categoryId, regions, searchString, pageable);
	}
}