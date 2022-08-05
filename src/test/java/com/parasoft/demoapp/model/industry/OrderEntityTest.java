/**
 *
 */
package com.parasoft.demoapp.model.industry;


import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * Test class for OrderEntity
 *
 * @see com.parasoft.demoapp.model.industry.OrderEntity
 */
public class OrderEntityTest {

	/**
	 * Test for copy()
	 *
	 * @see com.parasoft.demoapp.model.industry.OrderEntity#copy()
	 */
	@Test
	public void testCopy() throws Throwable {
		// Given
		Long id = 1L;
	    String orderNumber = "234-567-001";
	    Long userId = 1L;
	    OrderStatus status = OrderStatus.SUBMITTED;
	    Boolean reviewedByAPV = true;
	    Boolean reviewedByPRCH = true;
	    List<OrderItemEntity> orderItems = new ArrayList<>();
	    RegionType region = RegionType.JAPAN;
	    String location = "location";
	    String orderImage = "orderImage";
	    String receiverId = "receiverId";
	    String eventId = "eventId";
	    String eventNumber = "eventNumber";
	    String comments = "reason string";
	    Date submissionDate = new Date();
		Date approverReplyDate = new Date();

		// When
		OrderEntity originalOrder = new OrderEntity();
		originalOrder.setId(id);
		originalOrder.setOrderNumber(orderNumber);
		originalOrder.setUserId(userId);
		originalOrder.setStatus(status);
		originalOrder.setReviewedByAPV(reviewedByAPV);
		originalOrder.setReviewedByPRCH(reviewedByPRCH);
		originalOrder.setOrderItems(orderItems);
		originalOrder.setRegion(region);
		originalOrder.setLocation(location);
		originalOrder.setOrderImage(orderImage);
		originalOrder.setReceiverId(receiverId);
		originalOrder.setEventId(eventId);
		originalOrder.setEventNumber(eventNumber);
		originalOrder.setSubmissionDate(submissionDate);
		originalOrder.setApproverReplyDate(approverReplyDate);
		originalOrder.setComments(comments);

		OrderEntity newOrder = originalOrder.copy();

		boolean invokedByJtest = false;
		for(Field field : originalOrder.getClass().getDeclaredFields()) {
			if(field.getName().contains("__")) { // invoked by Jtest(with some additional fields, name start with __)
				invokedByJtest = true;
				break;
			}
		}

		// Then
		assertFalse(originalOrder == newOrder);
		assertEquals(originalOrder, newOrder);

		if(invokedByJtest) {
			assertEquals(20, originalOrder.getClass().getDeclaredFields().length,
					"Some additional files are not cloned, please confirm it is wanted or alter this test.");
		}else {
			assertEquals(16, originalOrder.getClass().getDeclaredFields().length,
					"Some additional files are not cloned, please confirm it is wanted or alter this test.");
		}
	}
}