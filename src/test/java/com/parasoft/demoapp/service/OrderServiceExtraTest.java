package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.UnreviewedOrderNumberResponseDTO;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class OrderServiceExtraTest {

    @InjectMocks
    OrderServiceExtra underTest;

    @Mock
    OrderRepository orderRepository;

    @Before
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUnreviewedOrderNumber() {
        // Given
        String currentUsername = "purchaser1";
        when(orderRepository.countByRequestedByAndReviewedByPRCH(currentUsername, false)).thenReturn(1);
        when(orderRepository.countByReviewedByAPV(false)).thenReturn(2);

        // When
        UnreviewedOrderNumberResponseDTO result = underTest.getUnreviewedOrderNumber(currentUsername);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getUnreviewedByPurchaser());
        assertEquals(2, result.getUnreviewedByApprover());
    }
}