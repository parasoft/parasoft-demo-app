package com.parasoft.demoapp.defaultdata.industry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.parasoft.demoapp.config.datasource.IndustryRoutingDataSource;
import com.parasoft.demoapp.defaultdata.global.GlobalUsersCreator;
import com.parasoft.demoapp.model.global.LocalizationLanguageType;
import com.parasoft.demoapp.model.global.UserEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.CategoryEntity;
import com.parasoft.demoapp.model.industry.ItemEntity;
import com.parasoft.demoapp.model.industry.LabelEntity;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.service.CategoryService;
import com.parasoft.demoapp.service.ItemService;
import com.parasoft.demoapp.service.LabelService;
import com.parasoft.demoapp.service.LocationService;
import com.parasoft.demoapp.service.OrderService;
import com.parasoft.demoapp.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource("file:./src/test/java/com/parasoft/demoapp/application.properties")
@DirtiesContext
public class OutdoorDefaultDataSpringTest {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ItemService itemService;

	@Autowired
	LocationService locationService;

	@Autowired
	OrderService orderService;

	@Autowired
	UserService userService;
	
	@Autowired
	LabelService labelService;
	
	/**
	 * test for if default assets of Outdoor industry are successfully created
	 */
	@Test
	public void testDefaultOutdoorAssetsCreateSuccessfully() throws Throwable {
		// Given
		// switch database to Outdoor
		IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;

		// When
		// get all categories of Outdoor industry
		String searchString = "";
		Page<CategoryEntity> categories = categoryService.getCategories(searchString, Pageable.unpaged());

		// Then
		assertNotNull(categories);
		assertTrue(categories.getTotalElements() > 0);
		assertTrue(categories.getContent().get(0).getImage().startsWith("/outdoor"));

		// When
		List<ItemEntity> items = itemService.getAllItems();

		// Then
		assertTrue(items.size() > 0);
		assertTrue(items.get(0).getImage().startsWith("/outdoor"));
	}

	/**
	 * test for if default locations of Outdoor industry are successfully created
	 */
	@Test
	public void testDefaultOutdoorLocationsSuccessfullyCreated() throws Throwable {
		// Given
		// switch database to DEFENSE
		IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;

		// When
		// get all locations of defense industry
		List<LocationEntity> locations = locationService.getAllLocations();

		// Then
		assertNotNull(locations);
		assertTrue(locations.size() > 0);
	}

	/**
	 * test for if default orders of defense industry are successfully created
	 */
	@Test
	public void testDefaultOutdoorOrdersSuccessfullyCreated() throws Throwable {
		// Given
		// switch database to DEFENSE
		IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;

		UserEntity approver = userService.getUserByUsername(GlobalUsersCreator.USERNAME_APPROVER);

		// When
		// get all orders of defense industry
		List<OrderEntity> orders = orderService.getAllOrders(approver.getUsername(), approver.getRole().getName());

		// Then
		assertNotNull(orders);
//		assertTrue(orders.size() > 0);
	}
	
	/**
	 * test for if default overrided labels of outdoor industry are successfully created
	 */
	@Test
	public void testDefaultOutdoorOverridedLabelsSuccessfullyCreated() throws Throwable {
		// Given
		// switch database to OUTDOOR
		IndustryRoutingDataSource.currentIndustry = IndustryType.OUTDOOR;

		// When
		// get all overrided labels of outdoor industry
		List<LabelEntity> labels = labelService.getAllLabelsFromDBByLanguageType(LocalizationLanguageType.EN);

		// Then
		assertNotNull(labels);
		assertTrue(labels.size() > 0);

		// When
		// get all overrided labels of outdoor industry
		labels = labelService.getAllLabelsFromDBByLanguageType(LocalizationLanguageType.ZH);

		// Then
		assertNotNull(labels);
		assertTrue(labels.size() > 0);
	}
}