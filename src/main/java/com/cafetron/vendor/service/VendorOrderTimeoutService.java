package com.cafetron.vendor.service;

import com.cafetron.order.entity.Order;
import com.cafetron.order.repository.OrderRepository;
import com.cafetron.pickup.VendorOrderStatus;
import com.cafetron.pickup.VendorOrderStatusType;
import com.cafetron.pickup.repository.VendorOrderStatusRepository;
import com.cafetron.wallet.service.WalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VendorOrderTimeoutService {
    private final VendorOrderStatusRepository vendorOrderStatusRepository;
    private final OrderRepository orderRepository;
    private final WalletService walletService;

    public VendorOrderTimeoutService(
            VendorOrderStatusRepository vendorOrderStatusRepository,
            OrderRepository orderRepository,
            WalletService walletService
    ) {
        this.vendorOrderStatusRepository = vendorOrderStatusRepository;
        this.orderRepository = orderRepository;
        this.walletService = walletService;
    }

    @Scheduled(fixedDelayString = "${cafetron.vendor.timeout-scan-ms:60000}")
    @Transactional
    public void processExpiredVendorOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<VendorOrderStatus> expiredStatuses =
                vendorOrderStatusRepository.findExpiredPendingActionableStatuses(now);

        if (expiredStatuses.isEmpty()) {
            return;
        }

        Map<Long, Order> timedOutOrders = new LinkedHashMap<>();
        for (VendorOrderStatus status : expiredStatuses) {
            status.setStatus(VendorOrderStatusType.TIMEOUT);
            status.setActionedAt(now);

            Order order = status.getOrderItem().getOrder();
            timedOutOrders.put(order.getId(), order);
        }

        vendorOrderStatusRepository.saveAll(expiredStatuses);

        for (Order order : timedOutOrders.values()) {
            if (!"REFUNDED".equalsIgnoreCase(order.getPaymentStatus())) {
                walletService.refund(
                        order.getUserId(),
                        order,
                        order.getTotalAmount(),
                        "Order timeout refund"
                );
            }

            order.setOverallStatus("CANCELLED");
            order.setPaymentStatus("REFUNDED");
        }

        orderRepository.saveAll(timedOutOrders.values());
    }
}
