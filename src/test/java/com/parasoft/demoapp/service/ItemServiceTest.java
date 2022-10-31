package com.parasoft.demoapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.parasoft.demoapp.model.industry.ItemInventoryEntity;
import org.junit.Before;
import org.junit.Test;
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
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.industry.ItemRepository;

/**
 * test class for ItemService
 *
 * @see ItemService
 */
public class ItemServiceTest {

	@InjectMocks
	ItemService underTest;

	@Mock
	ItemRepository itemRepository;

	@Mock
	CategoryService categoryService;

	@Mock
	ImageService imageService;
	
	@Mock
	LocationService locationService;

	@Mock
    ItemInventoryService itemInventoryService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for addNewItem(String, String, Long, int, String, RegionType, Date) with normal
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_normal() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = 10L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		
		Date lastAccessedDate = new Date();
		ItemEntity saveResult = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);
		ItemInventoryEntity itemInventory = new ItemInventoryEntity(0L, inStock);
		doReturn(saveResult).when(itemRepository).save((ItemEntity) any());
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());
		doReturn(true).when(locationService).isCorrectRegionInCurrentIndustry(region);
		doReturn(itemInventory).when(itemInventoryService).saveItemInStock(nullable(Long.class), any());

		// When
		ItemEntity result = underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);

		// Then
		assertNotNull(result);
		assertEquals(name, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(inStock, result.getInStock());
		assertEquals(imagePath, result.getImage());
		assertEquals(region.getDisplayName(), result.getRegion().getDisplayName());
		assertEquals(lastAccessedDate, result.getLastAccessedDate());
	}

	/**
	 * Test for addNewItem(String, String, Long, int, String, RegionType, Date) with NameExistsAlreadyException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_nameExistsAlready() {
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		doReturn(true).when(itemRepository).existsByName(anyString());
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_NAME_EXISTS_ALREADY, name), message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with NullNameException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_nullName() {
		// Given
		String name = null;
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with NullDescriptionException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_nullDescription() {
		// Given
		String name = "item";
		String description = null;
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with NullCategoryException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_nullCategoryId() {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = null;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with NullInStockException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_nullInStock() {
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = null;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.IN_STOCK_CANNOT_BE_NULL, message);
	}

		/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with NullInStockException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_negativeInStock() {
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = -1;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, inStock), message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date)
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_nullRegion() {
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = null;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.REGION_CANNOT_BE_NULL, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with EmptyNameException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_emptyName() {
		// Given
		String name = "";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.JAPAN;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with EmptyDescriptionException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_emptyDescription() {
		// Given
		String name = "item";
		String description = "";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.JAPAN;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date) with NegativeNumberInStockException
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_negativeNumberInStock() {
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = -1;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.JAPAN;
		doThrow(NullPointerException.class).when(itemRepository).save((ItemEntity) any());

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date)
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_categoryIdNotExists() {

		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = -1L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		doReturn(false).when(categoryService).existsByCategoryId(anyLong()); // test point

		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId), message);
	}

	/**
	 * test for addNewItem(String, String, Long, int, String, RegionType, Date)
	 *
	 * @see ItemService#addNewItem(String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testAddNewItem_incorrectRegionInCurrentIndustry() throws Throwable{
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.EARTH;
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());
		doReturn(false).when(locationService).isCorrectRegionInCurrentIndustry(region);
		
		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY, message);
	}
	
	/**
	 * test for removeItemById(Long) with normal
	 *
	 * @see ItemService#removeItemById(Long)
	 */
	@Test
	public void testRemoveItemById_normal() throws Throwable {
		// Given
		doNothing().when(itemRepository).deleteById(nullable(Long.class));
		doReturn(true).when(itemRepository).existsById(anyLong());
		Optional<ItemEntity> optional = Optional.of(mock(ItemEntity.class));
		doReturn(optional).when(itemRepository).findById(anyLong());

		// When
		Long itemId = 0L;
		underTest.removeItemById(itemId);

		// Then
		verify(itemInventoryService, times(1)).removeItemInventoryByItemId(itemId);
	}

	/**
	 * test for removeItemById(Long) with normal
	 *
	 * @see ItemService#removeItemById(Long)
	 */
	@Test
	public void testRemoveItemById_failToDeleteImage() throws Throwable {
		// Given
		doNothing().when(itemRepository).deleteById(nullable(Long.class));
		doReturn(true).when(itemRepository).existsById(anyLong());
		ItemEntity item = mock(ItemEntity.class);
		when(item.getImage()).thenReturn("/uploaded_images/**");
		Optional<ItemEntity> optional = Optional.of(item);
		doReturn(optional).when(itemRepository).findById(anyLong());
		doReturn(1L).when(imageService).numberOfImageUsed(anyString());

		doThrow(UploadedImageCanNotDeleteException.class).when(imageService).deleteUploadedImageByPath(nullable(String.class));

		// When
		Long itemId = 0L;
		underTest.removeItemById(itemId);  // exception is caught by under test method.
	}

	/**
	 * test for removeItemById(Long) with ItemNotExistException
	 *
	 * @see ItemService#removeItemById(Long)
	 */
	@Test
	public void testRemoveItemById_itemNotExist() {
		// Given
		doReturn(false).when(itemRepository).existsById(anyLong());

		// When
		Long itemId = -1L;
		String message = "";
		try {
			underTest.removeItemById(itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, itemId), message);
	}

	/**
	 * test for removeItemById(Long) with NullItemIdException
	 *
	 * @see ItemService#removeItemById(Long)
	 */
	@Test
	public void testRemoveItemById_nullItemId() {
		// Given
		doNothing().when(itemRepository).deleteById(nullable(Long.class));

		// When
		Long itemId = null;
		String message = null;
		try {
			underTest.removeItemById(itemId);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for removeItemByName(String) with normal
	 *
	 * @see ItemService#removeItemByName(String)
	 */
	@Test
	public void testRemoveItemByName_normal() throws Throwable {
		// Given
		doNothing().when(itemRepository).deleteByName(nullable(String.class));
		doReturn(true).when(itemRepository).existsByName(anyString());
		doReturn(true).when(itemRepository).existsById(anyLong());
		doReturn(mock(ItemEntity.class)).when(itemRepository).findByName(anyString());
		Optional<ItemEntity> optional = Optional.of(mock(ItemEntity.class));
		doReturn(optional).when(itemRepository).findById(anyLong());

		// When
		String itemName = "item";
		underTest.removeItemByName(itemName);
	}

	/**
	 * test for removeItemByName(String) with normal
	 *
	 * @see ItemService#removeItemByName(String)
	 */
	@Test
	public void testRemoveItemByName_failToDeleteImage() throws Throwable {
		// Given
		doNothing().when(itemRepository).deleteByName(nullable(String.class));
		doReturn(true).when(itemRepository).existsByName(anyString());
		doReturn(true).when(itemRepository).existsById(anyLong());
		doReturn(mock(ItemEntity.class)).when(itemRepository).findByName(anyString());
		ItemEntity item = mock(ItemEntity.class);
		when(item.getImage()).thenReturn("/uploaded_images/**");
		Optional<ItemEntity> optional = Optional.of(item);
		doReturn(optional).when(itemRepository).findById(anyLong());
		doReturn(1L).when(imageService).numberOfImageUsed(anyString());

		doThrow(UploadedImageCanNotDeleteException.class).when(imageService).deleteUploadedImageByPath(nullable(String.class));

		// When
		String itemName = "item";
		underTest.removeItemByName(itemName); // exception is caught by under test method.
	}

	/**
	 * test for removeItemByName(String) with ItemNotExistException
	 *
	 * @see ItemService#removeItemByName(String)
	 */
	@Test
	public void testRemoveItemByName_itemNotExist() throws Throwable {
		// Given
		doReturn(false).when(itemRepository).existsByName(anyString());

		// When
		String itemName = "item";
		String message = "";
		try {
			underTest.removeItemByName(itemName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_NAME_NOT_FOUND, itemName), message);
	}

	/**
	 * test for removeItemByName(String) with NullItemNameException
	 *
	 * @see ItemService#removeItemByName(String)
	 */
	@Test
	public void testRemoveItemByName_nullItemName() throws Throwable {
		// Given
		doNothing().when(itemRepository).deleteByName(nullable(String.class));

		// When
		String itemName = null;
		String message = "";
		try {
			underTest.removeItemByName(itemName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for removeItemByName(String) with NullItemNameException
	 *
	 * @see ItemService#removeItemByName(String)
	 */
	@Test
	public void testRemoveItemByName_emptyItemName() throws Throwable {
		// Given
		doNothing().when(itemRepository).deleteByName(nullable(String.class));

		// When
		String itemName = null;
		String message = "";
		try {
			underTest.removeItemByName(itemName);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with normal
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_normal() throws Throwable {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		Long categoryId = 10L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();
		ItemEntity saveResult = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);
		ItemInventoryEntity itemInventory = new ItemInventoryEntity(itemId, inStock);

		Optional<ItemEntity> optional = Optional.of(saveResult);
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());
		doReturn(optional).when(itemRepository).findById(anyLong());
		doReturn(saveResult).when(itemRepository).save((ItemEntity) any());
		doReturn(true).when(locationService).isCorrectRegionInCurrentIndustry(region);
		doReturn(itemInventory).when(itemInventoryService).saveItemInStock(anyLong(), any());

		// When
		ItemEntity result = underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);

		// Then
		assertNotNull(result);
		assertEquals(name, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(inStock, result.getInStock());
		assertEquals(imagePath, result.getImage());
		assertEquals(region, result.getRegion());
		verify(itemInventoryService, times(1)).saveItemInStock(itemId, inStock);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with normal
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_normal_nameChange() throws Throwable {
		// Given
		Long itemId = 0L;
		String name = "item";
		String differentName = "differentName";
		String description = "first item";
		Long categoryId = 10L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();
		ItemEntity saveResult = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);
		ItemInventoryEntity itemInventory = new ItemInventoryEntity(itemId, inStock);

		Optional<ItemEntity> optional = Optional.of(saveResult);
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());
		doReturn(optional).when(itemRepository).findById(anyLong());
		doReturn(saveResult).when(itemRepository).save((ItemEntity) any());
		doReturn(false).when(itemRepository).existsByName(anyString());
		doReturn(true).when(locationService).isCorrectRegionInCurrentIndustry(region);
        doReturn(itemInventory).when(itemInventoryService).saveItemInStock(anyLong(), nullable(Integer.class));

		// When
		ItemEntity result = underTest.updateItem(itemId, differentName, description, categoryId, inStock, imagePath,
				region);

		// Then
		assertNotNull(result);
		assertEquals(differentName, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(inStock, result.getInStock());
		assertEquals(imagePath, result.getImage());
		assertEquals(region, result.getRegion());
        verify(itemInventoryService, times(1)).saveItemInStock(itemId, inStock);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NullItemIdException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nullItemId() {
		// Given
		Long itemId = null;
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
	}
	
	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NullItemIdException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_incorrectRegionInCurrentIndustry() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.EARTH;
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());
		doReturn(false).when(locationService).isCorrectRegionInCurrentIndustry(region);
		
		// When
		String message = "";
		try {
			underTest.addNewItem(name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.INCORRECT_REGION_IN_CURRENT_INDUSTRY, message);
	}
	

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NullNameException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nullName() {
		// Given
		Long itemId = 0L;
		String name = null;
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NullDescriptionException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nullDescription() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = null;
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NullCategoryException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nullCategoryId() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		Long categoryId = null;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.CATEGORY_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NullInStockException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nullinStock() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = null;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.IN_STOCK_CANNOT_BE_NULL, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, Integer, String, RegionType)
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_negativeInStock() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = -1;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, inStock), message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType) with NullRegionException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nullRegion() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = null;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.REGION_CANNOT_BE_NULL, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date) with NameExistAlreadyException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_nameExistAlready() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();

		ItemEntity itemEntity = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);
		Optional<ItemEntity> optional = Optional.of(itemEntity);

		doReturn(optional).when(itemRepository).findById(anyLong());
		doReturn(true).when(itemRepository).existsByName(anyString());
		doReturn(true).when(categoryService).existsByCategoryId(anyLong());

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, "differentItem", description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_NAME_EXISTS_ALREADY, name), message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType) with EmptyNameException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_emptyName() {
		// Given
		Long itemId = 0L;
		String name = "";
		String description = "first item";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.JAPAN;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType) with EmptyDescriptionException
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_emptyDescription() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "";
		CategoryEntity category = mock(CategoryEntity.class);
		Long categoryId = category.getId();
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.JAPAN;

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.DESCRIPTION_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for updateItem(Long, String, String, Long, int, String, RegionType, Date)
	 *
	 * @see ItemService#updateItem(Long, String, String, Long, Integer, String, RegionType)
	 */
	@Test
	public void testUpdateItem_categoryIdNotExists() {
		// Given
		Long itemId = 0L;
		String name = "item";
		String description = "first item";
		Long categoryId = 10L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;

		doReturn(false).when(categoryService).existsByCategoryId(anyLong());

		// When
		String message = "";
		try {
			underTest.updateItem(itemId, name, description, categoryId, inStock, imagePath, region);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.CATEGORY_ID_NOT_FOUND, categoryId), message);
	}

	/**
	 * Test for updateItemInStock(Long, Integer) with noraml
	 *
	 * @see ItemService#updateItemInStock(Long, Integer)
	 */
	@Test
	public void testUpdateItemInStock_normal() throws Throwable {
		// Given
		Long itemId = 1L;
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 1;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();

		ItemEntity item = new ItemEntity(name, description, categoryId, inStock, imagePath, region, lastAccessedDate);
		Optional<ItemEntity> optional = Optional.of(item);
		Integer newInStock = 10;
        ItemInventoryEntity itemInventory = new ItemInventoryEntity(itemId, newInStock);
		doReturn(optional).when(itemRepository).findById(itemId);
		doReturn(itemInventory).when(itemInventoryService).saveItemInStock(itemId, newInStock);

		// When
		ItemEntity result = underTest.updateItemInStock(itemId, newInStock);

		// Then
		assertNotNull(result);
		assertEquals(newInStock, result.getInStock());
		verify(itemInventoryService, times(1)).saveItemInStock(itemId, newInStock);
	}

	/**
	 * Test for updateItemInStock(Long, Integer) with NullItemIdException
	 *
	 * @see ItemService#updateItemInStock(Long, Integer)
	 */
	@Test
	public void testUpdateItemInStock_exception_nullItemId() throws Throwable {
		// Given
		Long itemId = null;
		Integer newInStock = 10;

		// When
		String message = "";
		try {
			underTest.updateItemInStock(itemId, newInStock);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_ID_CANNOT_BE_NULL, itemId), message);
	}

	/**
	 * Test for updateItemInStock(Long, Integer) with negativeInStockException
	 *
	 * @see ItemService#updateItemInStock(Long, Integer)
	 */
	@Test
	public void testUpdateItemInStock_exception_negativeItemId() throws Throwable {
		// Given
		Long itemId = 1L;
		Integer newInStock = -10;

		// When
		String message = "";
		try {
			underTest.updateItemInStock(itemId, newInStock);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.IN_STOCK_CANNOT_BE_A_NEGATIVE_NUMBER, itemId), message);
	}

	/**
	 * test for getById(Long) with normal
	 *
	 * @see ItemService#getItemById(Long)
	 */
	@Test
	public void testGetItemById_normal() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.UNITED_STATES;
		Date lastAccessedDate = new Date();
		ItemEntity entity = new ItemEntity(name, description, categoryId, inStock, imagePath, region, lastAccessedDate);
		Optional<ItemEntity> optional = Optional.of(entity);
		doReturn(optional).when(itemRepository).findById(nullable(Long.class));
		doReturn(inStock).when(itemInventoryService).getInStockByItemId(nullable(Long.class));

		// When
		Long id = 0L;
		ItemEntity result = underTest.getItemById(id);

		// Then
		assertNotNull(result);
		assertEquals(name, result.getName());
		assertEquals(description, result.getDescription());
		assertEquals(inStock, result.getInStock());
		assertEquals(imagePath, result.getImage());
		assertEquals(region, result.getRegion());
		assertEquals(lastAccessedDate, result.getLastAccessedDate());
	}

	/**
	 * test for getById(Long) with NotFoundExcepion
	 *
	 * @see ItemService#getItemById(Long)
	 */
	@Test
	public void testGetItemById_notFound() {
		// Given
		Optional<ItemEntity> optional = Optional.ofNullable(null);
		doReturn(optional).when(itemRepository).findById(nullable(Long.class));

		// When
		Long id = 0L;
		String message = "";
		try {
			underTest.getItemById(id);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_ID_NOT_FOUND, id), message);
	}

	/**
	 * test for getById(Long) with NullIdException
	 *
	 * @see ItemService#getItemById(Long)
	 */
	@Test
	public void testGetItemById_nullId() {
		// Given
		Optional<ItemEntity> optional = Optional.ofNullable(null);
		doReturn(optional).when(itemRepository).findById(nullable(Long.class));

		// When
		Long id = null;
		String message = "";
		try {
			underTest.getItemById(id);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * test for getItemByName(String) with normal
	 *
	 * @see ItemService#getItemByName(String)
	 */
	@Test
	public void testGetItemByName_normal() throws Throwable {
		// Given
		ItemEntity findByNameResult = mock(ItemEntity.class);
		when(itemRepository.findByName(nullable(String.class))).thenReturn(findByNameResult);

		// When
		String name = "item";
		ItemEntity result = underTest.getItemByName(name);

		// Then
		assertNotNull(result);
		verify(itemInventoryService, times(1)).getInStockByItemId(nullable(Long.class));
	}

	/**
	 * test for getItemByName(String) with NoItemException
	 *
	 * @see ItemService#getItemByName(String)
	 */
	@Test
	public void testGetItemByName_noItem() {
		// Given
		ItemEntity findByNameResult = null;
		when(itemRepository.findByName(nullable(String.class))).thenReturn(findByNameResult);

		// When
		String name = "not exit";
		String message = "";
		try {
			underTest.getItemByName(name);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(MessageFormat.format(AssetMessages.ITEM_NAME_NOT_FOUND, name), message);
	}

	/**
	 * test for getItemByName(String) with NullNameExcrption
	 *
	 * @see ItemService#getItemByName(String)
	 */
	@Test
	public void testGetItemByName_nullName() {

		// When
		String name = null;
		String message = "";
		try {
			underTest.getItemByName(name);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for getItemByName(String) with NullNameExcrption
	 *
	 * @see ItemService#getItemByName(String)
	 */
	@Test
	public void testGetItemByName_emptyName() {

		// When
		String name = "";
		String message = "";
		try {
			underTest.getItemByName(name);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.ITEM_NAME_CANNOT_BE_BLANK, message);
	}

	/**
	 * test for getAllItems() with normal
	 *
	 * @see ItemService#getAllItems()
	 */
	@Test
	public void testGetAllItems_normal() throws Throwable {
		// Given
		List<ItemEntity> findAllResult = new ArrayList<>();
		ItemEntity item = mock(ItemEntity.class);
		findAllResult.add(item);
		doReturn(findAllResult).when(itemRepository).findAll();

		// When
		List<ItemEntity> result = underTest.getAllItems();

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		verify(itemInventoryService, times(findAllResult.size())).getInStockByItemId(nullable(Long.class));
	}

	/**
	 * test for getAllItems() with NoItemsExcrption
	 *
	 * @see ItemService#getAllItems()
	 */
	@Test
	public void testGetAllItems_noItems() {
		// Given
		List<ItemEntity> findAllResult = new ArrayList<>();
		doReturn(findAllResult).when(itemRepository).findAll();

		// When
		String message = "";
		try {
			underTest.getAllItems();
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.NO_ITEMS, message);
	}

	/**
	 * test for  getItems(Long, RegionType, String, Pageable) with normal
	 *
	 * @see ItemService#getItems(Long, RegionType[], String, Pageable)
	 */
	@Test
	public void testGetItems_normal() throws Throwable {
		// Given
		String name = "item";
		String description = "first item";
		Long categoryId = 1L;
		Integer inStock = 10;
		String imagePath = "/image/item/path";
		RegionType region = RegionType.GERMANY;
		Date lastAccessedDate = new Date();
		ItemEntity item = new ItemEntity(name, description, categoryId, inStock, imagePath, region,
				lastAccessedDate);

		List<ItemEntity> itemList = new ArrayList<>();
		itemList.add(item);

		Page<ItemEntity> findAllResult = new PageImpl<>(itemList);
		doReturn(findAllResult).when(itemRepository).findAll((Specification<ItemEntity>) any(), (Pageable) any());
		doReturn(inStock).when(itemInventoryService).getInStockByItemId(any());

		// When
		String searchName = null;
		Pageable pageable = Pageable.unpaged();
		RegionType[] regions = {RegionType.GERMANY, RegionType.JAPAN};
		Page<ItemEntity> result = underTest.getItems(categoryId, regions, searchName, pageable);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getSize());
        verify(itemInventoryService, times(itemList.size())).getInStockByItemId(nullable(Long.class));
	}

	/**
	 * test for getItems(Long, RegionType, String, Pageable) with NoItemException
	 *
	 * @see ItemService#getItems(Long, RegionType[], String, Pageable)
	 */
	@Test
	public void testGetItems_noItems() throws Throwable {
		// Given
		Long CategoryId = 0L;
		RegionType[] regions = {RegionType.GERMANY, RegionType.JAPAN};
		String searchName = null;
		List<ItemEntity> itemList = new ArrayList<>();
		Page<ItemEntity> findAllResult = new PageImpl<>(itemList);
		doReturn(findAllResult).when(itemRepository).findAll(any(Specification.class), any(Pageable.class));

		// When
		String message = "";
		try {
			underTest.getItems(CategoryId, regions, searchName, Pageable.unpaged());
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.NO_ITEMS, message);
        verify(itemInventoryService, times(0)).getInStockByItemId(nullable(Long.class));
	}

	/**
	 * Test for searchItemsByNameOrDescription(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.ItemService#searchItemsByNameOrDescription(String, Pageable)
	 */
	@Test
	public void testSearchItemsByNameOrDescription_normal() throws Throwable {
		// Given
		List<ItemEntity> content = new ArrayList<>();
		content.add(new ItemEntity());
		Pageable pageable = Pageable.unpaged();
		int totalElement = 2;
		
		Page<ItemEntity> page = new PageImpl<>(content, pageable, totalElement);
		doReturn(page).when(itemRepository)
				.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(nullable(String.class),
						nullable(String.class), nullable(Pageable.class));

		// When
		String key = "Tank";
		Page<ItemEntity> result = underTest.searchItemsByNameOrDescription(key, pageable);

		// Then
		assertNotNull(result);
		assertEquals(totalElement, result.getTotalElements());
		assertEquals(content, result.getContent());
        verify(itemInventoryService, times(content.size())).getInStockByItemId(nullable(Long.class));
	}
	
	/**
	 * Test for searchItemsByNameOrDescription(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.ItemService#searchItemsByNameOrDescription(String, Pageable)
	 */
	@Test
	public void testSearchItemsByNameOrDescription_nullKey() throws Throwable {
		// Given
		Pageable pageable = Pageable.unpaged();

		// When
		String key = null;
		String message = "";
		try {
			underTest.searchItemsByNameOrDescription(key, pageable);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.SEARCH_FIELD_CANNOT_BE_BLANK, message);
	}
	
	/**
	 * Test for searchItemsByNameOrDescription(String, Pageable)
	 *
	 * @see com.parasoft.demoapp.service.ItemService#searchItemsByNameOrDescription(String, Pageable)
	 */
	@Test
	public void testSearchItemsByNameOrDescription_emptyKey() throws Throwable {
		// Given
		Pageable pageable = Pageable.unpaged();

		// When
		String key = "  ";
		String message = "";
		try {
			underTest.searchItemsByNameOrDescription(key, pageable);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(AssetMessages.SEARCH_FIELD_CANNOT_BE_BLANK, message);
	}

	/**
	 * Test for numberOfImageUsedInItems(String)
	 *
	 * @see com.parasoft.demoapp.service.ItemService#numberOfImageUsedInItems(String)
	 */
	@Test
	public void testNumberOfImageUsedInItems_normal() throws Throwable {
		// Given
		when(itemRepository.countByImage(anyString())).thenReturn(1L);

		// When
		String imagePath = "/uploaded_images/defense/123.jpg";
		long result = underTest.numberOfImageUsedInItems(imagePath);

		// Then
		assertEquals(1l, result);
	}

	/**
	 * Test for numberOfImageUsedInItems(String)
	 *
	 * @see com.parasoft.demoapp.service.ItemService#numberOfImageUsedInItems(String)
	 */
	@Test
	public void testNumberOfImageUsedInItems_nullImagePath() throws Throwable {

		// When
		String imagePath = null;
		long result = underTest.numberOfImageUsedInItems(imagePath);

		// Then
		assertEquals(0, result);
	}

}
