package com.parasoft.demoapp.service;

import com.parasoft.demoapp.dto.*;
import com.parasoft.demoapp.exception.*;
import com.parasoft.demoapp.messages.AssetMessages;
import com.parasoft.demoapp.messages.OrderMessages;
import com.parasoft.demoapp.model.global.RoleType;
import com.parasoft.demoapp.model.industry.*;
import com.parasoft.demoapp.repository.industry.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderMQService orderMQService;

    @Autowired
    private LocationService locationService;

    @Transactional
    public InventoryOperationRequestMessageDTO handleMessageFromResponseQueue(InventoryOperationResultMessageDTO operationResult) {
        OrderEntity order = null;
        String orderNumber = operationResult.getOrderNumber();
        try {
            if(operationResult.getOperation() == InventoryOperation.DECREASE) {
                order = getOrderByOrderNumber(orderNumber);
                switch (operationResult.getStatus()) {
                    case SUCCESS:
                        order = updateOrderStatus(orderNumber, OrderStatus.PROCESSED, null);
                        OrderMQMessageDTO message =
                                new OrderMQMessageDTO(orderNumber, order.getRequestedBy(), order.getStatus(), OrderMessages.THE_ORDER_IS_PROCESSED);
                        orderMQService.sendToApprover(message);
                        break;
                    case FAIL:
                        order = updateOrderStatus(orderNumber, OrderStatus.CANCELED, operationResult.getInfo());
                        OrderMQMessageDTO msg =
                                new OrderMQMessageDTO(orderNumber, order.getRequestedBy(), order.getStatus(), OrderMessages.THE_ORDER_IS_CANCELLED);
                        orderMQService.sendToApprover(msg);
                        break;
                    default:
                        log.error(operationResult.getStatus() + " status is not supported");
                }
            }
            return null;
        } catch (OrderNotFoundException | ParameterException | OrderStatusException e) {
            log.error("Order Exception:", e);
            return new InventoryOperationRequestMessageDTO(InventoryOperation.NONE,
                    operationResult.getOrderNumber(),
                    e.getMessage());
        }
    }

    private OrderEntity updateOrderStatus(String orderNumber, OrderStatus status, String comments)
                                            throws OrderNotFoundException, ParameterException, OrderStatusException {
        OrderEntity order = getOrderByOrderNumber(orderNumber);
        if(order.getStatus().getPriority() < status.getPriority()) {
            order.setStatus(status);
            order.setComments(comments);
            return orderRepository.save(order);
        }

        throw new OrderStatusException("Can not change order status from " + order.getStatus() + " to " + status);
    }

    public synchronized OrderEntity addNewOrderSynchronized(Long userId, String username, RegionType region, String location,
                                                            String shipping, String receiverId, String eventId, String eventNumber)
            throws ParameterException, ItemNotFoundException, CartItemNotFoundException {

        return  addNewOrder(userId, username, region, location, shipping, receiverId, eventId, eventNumber);
    }

    @Transactional(value = "industryTransactionManager", rollbackFor = {CartItemNotFoundException.class})
    public OrderEntity addNewOrder(Long userId, String username, RegionType region, String location, String shipping, String receiverId, String eventId,
                                   String eventNumber)
            throws ParameterException, ItemNotFoundException, CartItemNotFoundException {

        ParameterValidator.requireNonNull(userId, OrderMessages.USER_ID_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(username, OrderMessages.USERNAME_CANNOT_BE_NULL);
        ParameterValidator.requireNonNull(region, OrderMessages.REGION_CANNOT_BE_NULL);
        ParameterValidator.requireNonBlank(location, OrderMessages.LOCATION_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(shipping, OrderMessages.SHIPPING_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(receiverId, OrderMessages.RECEIVER_ID_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(eventId, OrderMessages.EVENT_ID_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(eventNumber, OrderMessages.EVENT_NUMBER_CANNOT_BE_BLANK);

        LocationEntity locationEntity;
        try {
            locationEntity = locationService.getLocationByRegion(region);
        } catch (LocationNotFoundException e) {
            throw new ParameterException(MessageFormat.format(OrderMessages.LOCATION_NOT_FOUND_FOR_REGION, region.toString()), e);
        }

        OrderEntity order = new OrderEntity();
        order.setRequestedBy(username);
        order.setStatus(OrderStatus.SUBMITTED);
        order.setRegion(region);
        order.setLocation(location);
        order.setShippingType(shipping);
        order.setOrderImage(locationEntity.getLocationImage());
        order.setReceiverId(receiverId);
        order.setEventId(eventId);
        order.setEventNumber(eventNumber);
        order.setSubmissionDate(new Date());
        order.setReviewedByAPV(false);
        order.setReviewedByPRCH(true);

        List<OrderItemEntity> orderItemEntities = cartItemsToOrderItems(userId);

        for(OrderItemEntity orderItem : orderItemEntities) {
            orderItem.setOrder(order);
        }
        order.setOrderItems(orderItemEntities);

        order = orderRepository.save(order);
        String orderNumber = generateOrderNumberAccordingToId(order.getId());
        order.setOrderNumber(orderNumber);
        order = orderRepository.save(order);
        shoppingCartService.clearShoppingCart(userId);
        orderMQService.sendToInventoryRequestQueue(InventoryOperation.DECREASE, orderNumber, order.getOrderItems());

        return order;
    }

    private String generateOrderNumberAccordingToId(Long orderId){
        String fixLengthId = String.format("%08d", orderId + 23456000);

        String firstFragment = fixLengthId.substring(0, 2);
        String secondFragment = fixLengthId.substring(2, 5);
        String thirdFragment = fixLengthId.substring(5, 8);

        return firstFragment + "-" + secondFragment + "-" + thirdFragment;
    }

    private List<OrderItemEntity> cartItemsToOrderItems(Long userId)
            throws ParameterException, ItemNotFoundException, CartItemNotFoundException {

        List<CartItemEntity> cartItems = shoppingCartService.getCartItemsByUserId(userId);
        if(cartItems.size() == 0){
            throw new CartItemNotFoundException(AssetMessages.NO_CART_ITEMS);
        }

        List<OrderItemEntity> orderItems = new ArrayList<>();
        for (CartItemEntity cartItem : cartItems) {
            OrderItemEntity orderItem = new OrderItemEntity();
            ItemEntity item = itemService.getItemById(cartItem.getItemId());
            orderItem.setName(item.getName());
            orderItem.setDescription(item.getDescription());
            orderItem.setImage(item.getImage());
            orderItem.setItemId(item.getId());
            orderItem.setQuantity(cartItem.getQuantity());

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public OrderEntity getOrderByOrderNumber(String orderNumber) throws OrderNotFoundException, ParameterException {
        ParameterValidator.requireNonBlank(orderNumber, OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK);

        OrderEntity order = orderRepository.findOrderByOrderNumber(orderNumber);

        if (null == order) {
            throw new OrderNotFoundException(MessageFormat.format(
                    OrderMessages.THERE_IS_NO_ORDER_CORRESPONDING_TO, orderNumber));
        }

        return order;
    }

    public synchronized OrderEntity updateOrderByOrderNumberSynchronized(String orderNumber, String userRoleName,
                                                                         OrderStatus newStatus, Boolean reviewedByPRCH, Boolean reviewedByAPV,
                                                                         String respondedBy, String comments, boolean publicToMQ)
            throws IncorrectOperationException, OrderNotFoundException, NoPermissionException, ParameterException {

        return updateOrderByOrderNumber(
                orderNumber, userRoleName, newStatus, reviewedByPRCH, reviewedByAPV, respondedBy, comments, publicToMQ);
    }

    @Transactional(value = "industryTransactionManager")
    public OrderEntity updateOrderByOrderNumber(String orderNumber, String userRoleName, OrderStatus newStatus,
                                                Boolean reviewedByPRCH, Boolean reviewedByAPV,
                                                String respondedBy, String comments,
                                                boolean publicToMQ)
            throws ParameterException, OrderNotFoundException, NoPermissionException, IncorrectOperationException {

        ParameterValidator.requireNonNull(newStatus, OrderMessages.STATUS_CANNOT_BE_NULL);
        ParameterValidator.requireNonBlank(orderNumber, OrderMessages.ORDER_NUMBER_CANNOT_BE_BLANK);
        ParameterValidator.requireNonBlank(userRoleName, OrderMessages.USER_ROLE_NAME_CANNOT_BE_BLANK);

        OrderEntity originalOrder = getOrderByOrderNumber(orderNumber);

        if(originalOrder.getStatus() == OrderStatus.CANCELED) {
            throw new IncorrectOperationException(OrderMessages.ORDER_INFO_CANNOT_CHANGE_FROM_CANCELED);
        }

        if(originalOrder.getStatus() == OrderStatus.SUBMITTED) {
            throw new IncorrectOperationException(OrderMessages.ORDER_INFO_CANNOT_CHANGE_FROM_SUBMITTED);
        }

        if(newStatus != originalOrder.getStatus() && newStatus.getPriority() <= OrderStatus.PROCESSED.getPriority()) {
            throw new ParameterException(
                    MessageFormat.format(OrderMessages.ORDER_STATUS_CHANGED_BACK_ERROR, originalOrder.getStatus(), newStatus));
        }

        OrderEntity newOrder = originalOrder.copy();

        if(RoleType.ROLE_PURCHASER.toString().equals(userRoleName)){
            checkOrderStatusChangedByPurchaser(originalOrder, newStatus);
            ParameterValidator.requireNonNull(reviewedByPRCH,
                    OrderMessages.ORDER_REVIEW_STATUS_OF_PURCHASER_SHOULD_NOT_BE_NULL);
            if(!reviewedByPRCH.equals(originalOrder.getReviewedByPRCH())) {
                checkOrderIsAlreadyReviewed(userRoleName, originalOrder);
                newOrder.setReviewedByPRCH(true);
            }
        }else if(RoleType.ROLE_APPROVER.toString().equals(userRoleName)){
            if(!originalOrder.getStatus().equals(newStatus)) {
                checkOrderIsOpenToApprover(originalOrder);
                newOrder.setStatus(newStatus);
                newOrder.setReviewedByPRCH(false);
                newOrder.setReviewedByAPV(true);
            }else {
                ParameterValidator.requireNonNull(reviewedByAPV,
                        OrderMessages.ORDER_REVIEW_STATUS_OF_APPROVER_SHOULD_NOT_BE_NULL);
                if(!reviewedByAPV.equals(originalOrder.getReviewedByAPV())) {
                    checkOrderIsAlreadyReviewed(userRoleName, originalOrder);
                    newOrder.setReviewedByAPV(true);
                }
            }
        }

        boolean sendRequestToIncreaseInventory = false;
        if(!originalOrder.getStatus().equals(newStatus)) {
            newOrder.setComments(comments == null ? "" : comments);
            if(OrderStatus.DECLINED.equals(newStatus)) {
                sendRequestToIncreaseInventory = true;
            }

            newOrder.setApproverReplyDate(new Date());
            // Set response username only when approvers change the order status
            newOrder.setRespondedBy(respondedBy);
        }

        newOrder = orderRepository.save(newOrder);

        if(sendRequestToIncreaseInventory) {
            orderMQService.sendToInventoryRequestQueue(InventoryOperation.INCREASE,
                    newOrder.getOrderNumber(),
                    newOrder.getOrderItems());
        }

        // send message to MQ topic
        if(publicToMQ && !originalOrder.getStatus().equals(newOrder.getStatus())){
            OrderMQMessageDTO message =
                    new OrderMQMessageDTO(newOrder.getOrderNumber(), newOrder.getRequestedBy(), newOrder.getStatus(), OrderMessages.ORDER_STATUS_CHANGED);
            orderMQService.sendToPurchaser(message);
        }

        return newOrder;
    }

    private void checkOrderIsAlreadyReviewed(String userRoleName, OrderEntity order) throws IncorrectOperationException {
        if(RoleType.ROLE_PURCHASER.toString().equals(userRoleName)){
            if(order.getReviewedByPRCH()) {
                throw new IncorrectOperationException(OrderMessages.CANNOT_SET_TRUE_TO_FALSE);
            }
        }else if(RoleType.ROLE_APPROVER.toString().equals(userRoleName)) {
            if(order.getReviewedByAPV()) {
                throw new IncorrectOperationException(OrderMessages.CANNOT_SET_TRUE_TO_FALSE);
            }
        }
    }

    private void checkOrderIsOpenToApprover(OrderEntity order) throws IncorrectOperationException {
        if(!OrderStatus.PROCESSED.equals(order.getStatus())) {
            throw new IncorrectOperationException(OrderMessages.ALREADY_MODIFIED_THIS_ORDER);
        }
    }

    private void checkOrderStatusChangedByPurchaser(OrderEntity order, OrderStatus newStatus)
            throws NoPermissionException {
        if(!order.getStatus().equals(newStatus)) {
            throw new NoPermissionException(
                    MessageFormat.format(OrderMessages.NO_PERMISSION_TO_CHANGE_TO_ORDER_STATUS, newStatus));
        }
    }

    public List<OrderEntity> getAllOrders(String requestedBy, String userRoleName) throws ParameterException {
        ParameterValidator.requireNonNull(requestedBy, OrderMessages.USERNAME_CANNOT_BE_NULL);
        ParameterValidator.requireNonBlank(userRoleName, OrderMessages.USER_ROLE_NAME_CANNOT_BE_BLANK);

        List<OrderEntity> list = new ArrayList<>();
        if(RoleType.ROLE_APPROVER.toString().equals(userRoleName)) {
            list = orderRepository.findAll();

        }else if (RoleType.ROLE_PURCHASER.toString().equals(userRoleName)) {
            list = orderRepository.findAllByRequestedBy(requestedBy);
        }

        return list;
    }

    public Page<OrderEntity> getAllOrders(String requestedBy, String userRoleName, Pageable pageable)
            throws ParameterException {
        ParameterValidator.requireNonNull(requestedBy, OrderMessages.USERNAME_CANNOT_BE_NULL);
        ParameterValidator.requireNonBlank(userRoleName, OrderMessages.USER_ROLE_NAME_CANNOT_BE_BLANK);

        Page<OrderEntity> page = new PageImpl<>(new ArrayList<>(), pageable,  0);
        if(RoleType.ROLE_APPROVER.toString().equals(userRoleName)) {
            List<OrderStatus> orderStatues = new ArrayList<>();
            orderStatues.add(OrderStatus.SUBMITTED);
            orderStatues.add(OrderStatus.CANCELED);
            page = orderRepository.findAllByStatusNotIn(orderStatues, pageable);

        }else if (RoleType.ROLE_PURCHASER.toString().equals(userRoleName)) {
            page = orderRepository.findAllByRequestedBy(requestedBy, pageable);
        }

        return page;
    }
}
