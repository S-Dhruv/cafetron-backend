package com.cafetron.order.dto;

import java.math.BigDecimal;

public record PlaceOrderResponse(
        Long orderId,
        String orderStatus,
        String paymentStatus,
        BigDecimal totalAmount,
        String qrToken
) {}