package com.cafetron.order.service;

import com.cafetron.order.dto.*;
import com.cafetron.security.UserPrincipal;

import java.util.List;

public interface OrderService {
    PlaceOrderResponse placeOrder(Long userId, PlaceOrderRequest request);
    List<MyOrderSummaryResponse> getMyOrders(Long userId);
    OrderDetailResponse getOrderDetail(Long userId, Long orderId);
    OrderDetailResponse getOrderDetailByToken(UserPrincipal principal, String token);
    OrderDetailResponse processTimeout(Long userId, Long orderId);
}
