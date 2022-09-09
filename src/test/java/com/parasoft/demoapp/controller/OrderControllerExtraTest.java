package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.UnreviewedOrderNumberResponseDTO;
import com.parasoft.demoapp.service.OrderServiceExtra;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test for OrderControllerExtra
 *
 * @see OrderControllerExtra
 */
public class OrderControllerExtraTest {

	@InjectMocks
	OrderControllerExtra underTest;

	@Mock
	OrderServiceExtra orderServiceExtra;

	@Before
	public void setupMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test for unreviewedOrderNumber()
	 *
	 * @see OrderControllerExtra#unreviewedOrderNumber()
	 */
	@Test
	public void testUnreviewedOrderNumber() {
		// Given
		UnreviewedOrderNumberResponseDTO unreviewedOrderNumberResponseDTO = new UnreviewedOrderNumberResponseDTO(1, 2);
		when(orderServiceExtra.getUnreviewedOrderNumber()).thenReturn(unreviewedOrderNumberResponseDTO);

		// When
		ResponseResult<UnreviewedOrderNumberResponseDTO> result = underTest.unreviewedOrderNumber();

		// Then
		assertNotNull(result);
		assertNotNull(result.getData());
		assertEquals(ResponseResult.STATUS_OK, result.getStatus());
		assertEquals(ResponseResult.MESSAGE_OK, result.getMessage());
		assertEquals(unreviewedOrderNumberResponseDTO.getUnreviewedByApprover(), result.getData().getUnreviewedByApprover());
		assertEquals(result.getData().getUnreviewedByPurchaser(), result.getData().getUnreviewedByPurchaser());
	}
}