/**
 * 
 */
package com.parasoft.demoapp.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.parasoft.demoapp.exception.LocationNotFoundException;
import com.parasoft.demoapp.messages.GlobalPreferencesMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.preferences.DemoBugEntity;
import com.parasoft.demoapp.model.global.preferences.DemoBugsType;
import com.parasoft.demoapp.model.global.preferences.GlobalPreferencesEntity;
import com.parasoft.demoapp.model.global.preferences.IndustryType;
import com.parasoft.demoapp.model.industry.LocationEntity;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.model.industry.OrderStatus;
import com.parasoft.demoapp.model.industry.RegionType;
import com.parasoft.demoapp.repository.global.DemoBugRepository;
import com.parasoft.demoapp.util.BugsTypeSortOfDemoBugs;

/**
 * test for DemoBugService
 *
 * @see com.parasoft.demoapp.service.DemoBugService
 */
public class DemoBugServiceTest {

	@InjectMocks
	DemoBugService underTest;

	@Mock
	DemoBugRepository demoBugRepository;

	@Mock
	GlobalPreferencesService globalPreferencesService;

	@Mock
	LocationService locationService;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test for removeByGlobalPreferencesId(Long)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#removeByGlobalPreferencesId(Long)
	 */
	@Test
	public void testRemoveGlobalPreferencesById_normal() throws Throwable {
		// Given
		doAnswer(invocation -> null).when(demoBugRepository).deleteByGlobalPreferencesId(anyLong());

		boolean existsByIdResult = true;
		when(demoBugRepository.existsById(nullable(Long.class))).thenReturn(existsByIdResult);

		// When
		Long id = 0L;
		underTest.removeByGlobalPreferencesId(id);
		
		// Then
		// no data, can not assert
	}
	
	/**
	 * test for removeGlobalPreferencesById(Long)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#removeByGlobalPreferencesId(Long)
	 */
	@Test
	public void testRemoveGlobalPreferencesById_nullId() throws Throwable {
		// Given
		Long id = null;

		// When
		String message = "";
		try {
			underTest.removeByGlobalPreferencesId(id);
		} catch (Exception e) {
			message = e.getMessage();
		}

		// Then
		assertEquals(GlobalPreferencesMessages.GLOBAL_PREFERENCES_ID_CANNOT_BE_NULL, message);
	}

	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_nullOrder()
			throws Exception {
		// Given
		OrderEntity order = null;

		// When
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		Assert.assertNull(order);
	}
	
	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_bugIsNotIntroduced_orderStatusIsNotApproved()
			throws Exception {
		// Given
		OrderEntity order = new OrderEntity();
		String correctLocation = "location";
		order.setLocation(correctLocation);
		order.setStatus(OrderStatus.DECLINED); // order status is not approved

		// When
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		Assert.assertEquals(correctLocation, order.getLocation());
	}

	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_bugIsNotIntroduced_noBugs()
			throws Exception {
		// Given
		OrderEntity order = new OrderEntity();
		String correctLocation = "location";
		order.setLocation(correctLocation);
		order.setStatus(OrderStatus.APPROVED);

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs()); // no bugs, size is 0.
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();

		// When
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		Assert.assertEquals(correctLocation, order.getLocation());
	}

	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_bugIsIntroducedForOrder() throws Exception {
		// Given
		OrderEntity order = new OrderEntity();
		order.setRegion(RegionType.GERMANY);
		order.setStatus(OrderStatus.APPROVED);

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugEntities.add(new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS));
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		globalPreferencesEntity.setIndustryType(IndustryType.DEFENSE);
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();

		LocationEntity locationOfAustralia = new LocationEntity();
		locationOfAustralia.setLocationImage("/australia/map");
		locationOfAustralia.setLocationInfo("10.12° E, 10.12° N");
		locationOfAustralia.setRegion(RegionType.AUSTRALIA);
		doReturn(locationOfAustralia).when(locationService).getLocationByRegion(RegionType.AUSTRALIA);

		LocationEntity locationOfUK = new LocationEntity();
		locationOfUK.setLocationImage("/uk/map");
		locationOfUK.setLocationInfo("20.15° E, 12.13° N");
		locationOfUK.setRegion(RegionType.UNITED_KINGDOM);
		doReturn(locationOfUK).when(locationService).getLocationByRegion(RegionType.UNITED_KINGDOM);

		// When
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		assertEquals(locationOfAustralia.getLocationInfo(), order.getLocation());
		assertEquals(locationOfAustralia.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfAustralia.getRegion(), order.getRegion());

		// When
		order.setRegion(RegionType.AUSTRALIA);
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		assertEquals(locationOfUK.getLocationInfo(), order.getLocation());
		assertEquals(locationOfUK.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfUK.getRegion(), order.getRegion());


		// Given
		LocationEntity locationOfNeptune = new LocationEntity();
		locationOfNeptune.setLocationImage("/neptune/map");
		locationOfNeptune.setLocationInfo("36.15° E, 18.63° N");
		locationOfNeptune.setRegion(RegionType.NEPTUNE);
		doReturn(locationOfNeptune).when(locationService).getLocationByRegion(RegionType.NEPTUNE);

		LocationEntity locationOfVenus = new LocationEntity();
		locationOfVenus.setLocationImage("/venus/map");
		locationOfVenus.setLocationInfo("50.35° E, 16.23° N");
		locationOfVenus.setRegion(RegionType.VENUS);
		doReturn(locationOfVenus).when(locationService).getLocationByRegion(RegionType.VENUS);

		// When
		globalPreferencesEntity.setIndustryType(IndustryType.AEROSPACE);
		order.setRegion(RegionType.NEPTUNE);
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		assertEquals(locationOfVenus.getLocationInfo(), order.getLocation());
		assertEquals(locationOfVenus.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfVenus.getRegion(), order.getRegion());

		// When
		order.setRegion(RegionType.VENUS);
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		assertEquals(locationOfNeptune.getLocationInfo(), order.getLocation());
		assertEquals(locationOfNeptune.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfNeptune.getRegion(), order.getRegion());


		// Given
		LocationEntity locationOfRegion2 = new LocationEntity();
		locationOfRegion2.setLocationImage("/neptune/map");
		locationOfRegion2.setLocationInfo("36.15° E, 18.63° N");
		locationOfRegion2.setRegion(RegionType.NEPTUNE);
		doReturn(locationOfRegion2).when(locationService).getLocationByRegion(RegionType.LOCATION_2);

		LocationEntity locationOfRegion8 = new LocationEntity();
		locationOfRegion8.setLocationImage("/venus/map");
		locationOfRegion8.setLocationInfo("50.35° E, 16.23° N");
		locationOfRegion8.setRegion(RegionType.VENUS);
		doReturn(locationOfRegion8).when(locationService).getLocationByRegion(RegionType.LOCATION_8);

		// When
		globalPreferencesEntity.setIndustryType(IndustryType.OUTDOOR);
		order.setRegion(RegionType.LOCATION_2);
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		assertEquals(locationOfRegion8.getLocationInfo(), order.getLocation());
		assertEquals(locationOfRegion8.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfRegion8.getRegion(), order.getRegion());

		// When
		order.setRegion(RegionType.LOCATION_8);
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		// Then
		assertEquals(locationOfRegion2.getLocationInfo(), order.getLocation());
		assertEquals(locationOfRegion2.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfRegion2.getRegion(), order.getRegion());
	}

	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_industryIsNotImplemented() throws Exception {
		// Given
		OrderEntity order = new OrderEntity();
		order.setStatus(OrderStatus.APPROVED);  // aaproved

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugEntities.add(new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS)); // bug introduced
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		globalPreferencesEntity.setIndustryType(IndustryType.RETAIL); // Retail industry
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();

		// When
		String message = "";
		try {
			underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);
		}catch (Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}

		assertEquals(MessageFormat.format(OrderMessages.FAILED_TO_INTRODUCES_INCORRECT_LOCATION_BUG,
				DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS.getValue()), message);

		// Given
		globalPreferencesEntity.setIndustryType(IndustryType.GOVERNMENT); // Government industry

		// When
		try {
			underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);
		}catch (Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}

		assertEquals(MessageFormat.format(OrderMessages.FAILED_TO_INTRODUCES_INCORRECT_LOCATION_BUG,
				DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS.getValue()), message);


		// Given
		globalPreferencesEntity.setIndustryType(IndustryType.HEALTHCARE); // Healthcare industry

		// When
		try {
			underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);
		}catch (Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}

		assertEquals(MessageFormat.format(OrderMessages.FAILED_TO_INTRODUCES_INCORRECT_LOCATION_BUG,
				DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS.getValue()), message);


	}

	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(OrderEntity)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_demoBugsIntroduceFailedException() throws Exception {
		// Given
		OrderEntity order = new OrderEntity();
		order.setRegion(RegionType.GERMANY);
		order.setStatus(OrderStatus.APPROVED);

		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugEntities.add(new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS));
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		globalPreferencesEntity.setIndustryType(IndustryType.DEFENSE);
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();

		doThrow(LocationNotFoundException.class).when(locationService).getLocationByRegion(any(RegionType.class));

		// When
		String message = "";
		try{
			underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);
		}catch (Exception e){
			e.printStackTrace();
			message = e.getMessage();
		}

		assertEquals(MessageFormat.format(OrderMessages.FAILED_TO_INTRODUCES_INCORRECT_LOCATION_BUG,
				DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS.getValue()), message);
	}

	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(Collection<OrderEntity>)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(Collection<OrderEntity>)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_nullOrders()
			throws Exception {
		// Given
		List<OrderEntity> orders = null;

		// When
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(orders);

		// Then
		assertNull(orders);
	}
	
	/**
	 * Test for introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(Collection<OrderEntity>)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(Collection <OrderEntity>)
	 */
	@Test
	public void testIntroduceBugWithIncorrectLocationForApprovedOrdersIfNeeded_bugIsIntroducedForOrders()
			throws Exception {
		// Given
		List<OrderEntity> orders = new ArrayList<>();
		OrderEntity order = new OrderEntity();
		order.setRegion(RegionType.JAPAN);
		order.setStatus(OrderStatus.APPROVED);
		orders.add(order);
		
		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugEntities.add(new DemoBugEntity(DemoBugsType.INCORRECT_LOCATION_FOR_APPROVED_ORDERS));
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		globalPreferencesEntity.setIndustryType(IndustryType.DEFENSE);
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();

		LocationEntity locationOfAustralia = new LocationEntity();
		locationOfAustralia.setLocationImage("/australia/map");
		locationOfAustralia.setLocationInfo("10.12° E, 10.12° N");
		locationOfAustralia.setRegion(RegionType.AUSTRALIA);
		doReturn(locationOfAustralia).when(locationService).getLocationByRegion(RegionType.AUSTRALIA);

		// When
		underTest.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(orders);

		// Then
		assertEquals(locationOfAustralia.getLocationInfo(), order.getLocation());
		assertEquals(locationOfAustralia.getLocationImage(), order.getOrderImage());
		assertEquals(locationOfAustralia.getRegion(), order.getRegion());
	}

	/**
	 * Parasoft Jtest UTA: Test for introduceBugWithReverseOrdersIfNeeded(Pageable)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithReverseOrdersIfNeeded(Pageable)
	 */
	@Test
	public void testIntroduceBugWithReverseOrdersIfNeeded_bugIsNotIntroduced_noBugs() throws Throwable {
		// Given
		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();
		
		Pageable pageable = Pageable.unpaged();
		
		// When
		Pageable result = underTest.introduceBugWithReverseOrdersIfNeeded(pageable);

		// Then
		assertNotNull(result);
		assertEquals(pageable, result);
	}
	
	/**
	 * Parasoft Jtest UTA: Test for introduceBugWithReverseOrdersIfNeeded(Pageable)
	 *
	 * @see com.parasoft.demoapp.service.DemoBugService#introduceBugWithReverseOrdersIfNeeded(Pageable)
	 */
	@Test
	public void testIntroduceBugWithReverseOrdersIfNeeded_bugIsIntroduced() throws Throwable {
		// Given
		GlobalPreferencesEntity globalPreferencesEntity = new GlobalPreferencesEntity();
		Set<DemoBugEntity> demoBugEntities = new TreeSet<>(new BugsTypeSortOfDemoBugs());
		demoBugEntities.add(new DemoBugEntity(DemoBugsType.REVERSE_ORDER_OF_ORDERS));
		globalPreferencesEntity.setDemoBugs(demoBugEntities);
		doReturn(globalPreferencesEntity).when(globalPreferencesService).getCurrentGlobalPreferences();
		
		List<Sort.Order>orders = new ArrayList<>();
		orders.add(Order.asc("orderNumber"));
		orders.add(Order.desc("region"));
		Sort sort = Sort.by(orders);
		Pageable pageable = PageRequest.of(1, 10, sort);
		
		// When
		Pageable result = underTest.introduceBugWithReverseOrdersIfNeeded(pageable);
		
		// Then
		Direction orderNumberResult = result.getSort().getOrderFor("orderNumber").getDirection();
		Direction regionResult = result.getSort().getOrderFor("region").getDirection();
		
		assertNotNull(result);
		assertNotEquals(pageable, result);
		assertEquals(Sort.Direction.DESC, orderNumberResult);
		assertEquals(Sort.Direction.ASC, regionResult);
	}
}