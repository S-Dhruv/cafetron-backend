package com.cafetron.vendor.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VendorOrderResponse(
        Long vendorOrderStatusId,
        Long orderId,
        Long orderItemId,
        Long menuItemId,
        String itemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String pickupSlot,
        String orderStatus,
        String vendorStatus,
        String declinedReason,
        LocalDateTime actionExpiresAt,
        LocalDateTime createdAt
) {}
