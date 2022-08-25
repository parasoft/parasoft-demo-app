package com.parasoft.demoapp.controller;

import com.parasoft.demoapp.dto.OrderDTO;
import com.parasoft.demoapp.dto.OrderMQMessageDTO;
import com.parasoft.demoapp.dto.OrderStatusDTO;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.industry.OrderEntity;
import com.parasoft.demoapp.service.DemoBugService;
import com.parasoft.demoapp.service.OrderMQService;
import com.parasoft.demoapp.service.OrderService;
import com.parasoft.demoapp.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "orders")
@Controller
@RequestMapping(value = {"/v1/orders", "/proxy/v1/orders"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderMQService orderMQService;

	@Autowired
	private DemoBugService demoBugService;

	@Operation(description = "Submit a new order.")
	@ApiResponse(responseCode = "200", description = "New order submitted successfully.")
	@ApiResponse(responseCode = "400", description = "Invalid request payload.",
				content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401", description = "You are not authorized to submit a new order.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "403", description = "You do not have permission to submit a new order.",
			content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "There is no cart item or item to create a new order.",
			content = {@Content(schema = @Schema(hidden = true))})
	@PostMapping
	@ResponseBody
	public ResponseResult<OrderEntity> addNewOrder(Authentication auth,  @RequestBody OrderDTO orderDto )
			throws ParameterException, ItemNotFoundException, CartItemNotFoundException {

		ResponseResult<OrderEntity> response = ResponseResult.getInstance(
				ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		Long currentUserId = AuthenticationUtil.getUserIdInAuthentication(auth);
		String currentUserName = AuthenticationUtil.getUserNameInAuthentication(auth);
		OrderEntity order = orderService.addNewOrderSynchronized(currentUserId, currentUserName, orderDto.getRegion(), orderDto.getLocation(),
				orderDto.getReceiverId(), orderDto.getEventId(), orderDto.getEventNumber());
		response.setData(order);

		OrderMQMessageDTO message =
				new OrderMQMessageDTO(order.getOrderNumber(), order.getStatus(), OrderMessages.ORDER_STATUS_CHANGED);
		orderMQService.sendToApprover(message);

		return response;
	}

	@Operation(description = "Obtain an order by order number.")
	@ApiResponse(responseCode = "200", description = "Order with corresponding order number was returned.")
	@ApiResponse(responseCode = "400", description = "Invalid request parameter.",
				content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401",description = "You are not authorized to get an order by order number.",
				content = {@Content(schema = @Schema(hidden = true)) })
	@ApiResponse(responseCode = "404", description = "No order with corresponding order number.",
				content = {@Content(schema = @Schema(hidden = true))})
	@GetMapping("/{orderNumber}")
	@ResponseBody
	public ResponseResult<OrderEntity> getOrderByOrderNumber(@PathVariable String orderNumber)
			throws ParameterException, OrderNotFoundException, GlobalPreferencesMoreThanOneException,
				GlobalPreferencesNotFoundException, DemoBugsIntroduceFailedException {

		ResponseResult<OrderEntity> response = ResponseResult.getInstance(
				ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		OrderEntity order = orderService.getOrderByOrderNumber(orderNumber);
		demoBugService.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(order);

		response.setData(order);

		return response;
	}

	@Operation(description = "Update status of order by order number.")
	@ApiResponse(responseCode = "200", description = "Modified order with corresponding order number was returned.")
	@ApiResponse(responseCode = "400", description = "Invalid request parameter.",
			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "401",description = "You are not authorized to change status.",
			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "403",description = "You do not have permission to change status.",
			content = {@Content(schema = @Schema(hidden = true))})
	@ApiResponse(responseCode = "404", description = "No order with corresponding order number.",
			content = {@Content(schema = @Schema(hidden = true))})
	@PutMapping("/{orderNumber}")
	@ResponseBody
	public ResponseResult<OrderEntity> updateStatusOfOrderByOrderNumber(Authentication auth, 
			@RequestBody OrderStatusDTO newStatus, @PathVariable String orderNumber)
			throws ParameterException, OrderNotFoundException, NoPermissionException, IncorrectOperationException {

		ResponseResult<OrderEntity> response = ResponseResult.getInstance(
				ResponseResult.STATUS_OK, ResponseResult.MESSAGE_OK);

		response.setData(orderService.updateOrderByOrderNumberSynchronized(
				orderNumber, AuthenticationUtil.getUserRoleNameInAuthentication(auth), newStatus.getStatus(),
				newStatus.isReviewedByPRCH(), newStatus.isReviewedByAPV(), newStatus.getComments(), true));

		return response;
	}
	
	//	@Operation(description = "Obtain all orders under current identity.")
	//	@ApiResponses({
	//		@ApiResponse(responseCode = "200",
	//					description = "All orders under current identity got successfully."),
	//		@ApiResponse(responseCode = "403",
	//					description = "You do not have permission to get all orders.",
	//					content = {@Content(schema = @Schema(hidden = true)) }),
	//	})
	//	@GetMapping("/v1/orders")
	//	@ResponseBody
	@Deprecated
	public ResponseResult<List<OrderEntity>> showAllOrders(Authentication auth) throws ParameterException {
		
		ResponseResult<List<OrderEntity>> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);
		
		response.setData(orderService.getAllOrders(AuthenticationUtil.getUserNameInAuthentication(auth),
				AuthenticationUtil.getUserRoleNameInAuthentication(auth)));

		return response;
	}

	@Operation(description = "Obtain all orders under the current identity.")
	@ApiResponses({
			@ApiResponse(responseCode = "200",
					description = "All orders under current identity got successfully."),
			@ApiResponse(responseCode = "401",
					description = "You are not authorized to get all orders.",
					content = {@Content(schema = @Schema(hidden = true)) }),
	})
	@GetMapping
	@ResponseBody
	public ResponseResult<PageInfo<OrderEntity>> showAllOrders(Authentication auth,
			 @ParameterObject
			 @PageableDefault(sort = "orderNumber", direction = Sort.Direction.DESC, size = Integer.MAX_VALUE)
					 			Pageable pageable)
								throws ParameterException, GlobalPreferencesMoreThanOneException,
									GlobalPreferencesNotFoundException, DemoBugsIntroduceFailedException {

		ResponseResult<PageInfo<OrderEntity>> response = ResponseResult.getInstance(ResponseResult.STATUS_OK,
				ResponseResult.MESSAGE_OK);

		pageable = demoBugService.introduceBugWithReverseOrdersIfNeeded(pageable);
		
		Page<OrderEntity> page = orderService.getAllOrders(AuthenticationUtil.getUserNameInAuthentication(auth),
				AuthenticationUtil.getUserRoleNameInAuthentication(auth), pageable);
		PageInfo<OrderEntity> pageInfo =  new PageInfo<>(page);

		demoBugService.introduceBugWithIncorrectLocationForApprovedOrdersIfNeeded(pageInfo.getContent());

		response.setData(pageInfo);

		return response;
	}
}
