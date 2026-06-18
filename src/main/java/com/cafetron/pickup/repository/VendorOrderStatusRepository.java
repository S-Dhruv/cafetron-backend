package com.cafetron.pickup.repository;

import com.cafetron.pickup.VendorOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendorOrderStatusRepository extends JpaRepository<VendorOrderStatus, Long> {
    List<VendorOrderStatus> findByOrderItem_Order_Id(Long orderId);

    @Query("SELECT vos FROM VendorOrderStatus vos LEFT JOIN FETCH vos.orderItem WHERE vos.orderItem.order.id = :orderId")
    List<VendorOrderStatus> findByOrderItem_Order_IdWithOrderItem(@Param("orderId") Long orderId);

    @Query("""
            SELECT vos FROM VendorOrderStatus vos
            JOIN FETCH vos.orderItem oi
            JOIN FETCH oi.order o
            JOIN FETCH oi.menuItem mi
            JOIN FETCH vos.vendor v
            WHERE LOWER(v.email) = LOWER(:vendorEmail)
            ORDER BY o.createdAt DESC
            """)
    List<VendorOrderStatus> findVendorQueue(@Param("vendorEmail") String vendorEmail);

    @Query("""
            SELECT vos FROM VendorOrderStatus vos
            JOIN FETCH vos.orderItem oi
            JOIN FETCH oi.order o
            JOIN FETCH oi.menuItem mi
            JOIN FETCH vos.vendor v
            WHERE vos.id = :statusId AND LOWER(v.email) = LOWER(:vendorEmail)
            """)
    Optional<VendorOrderStatus> findOwnedStatus(
            @Param("statusId") Long statusId,
            @Param("vendorEmail") String vendorEmail
    );

    @Query("""
            SELECT vos FROM VendorOrderStatus vos
            JOIN FETCH vos.orderItem oi
            JOIN FETCH oi.order o
            WHERE vos.status = com.cafetron.pickup.VendorOrderStatusType.PENDING
              AND vos.actionExpiresAt IS NOT NULL
              AND vos.actionExpiresAt <= :now
              AND o.overallStatus NOT IN ('VENDOR_DECLINED', 'TIMEOUT', 'CANCELLED')
              AND o.paymentStatus <> 'REFUNDED'
            """)
    List<VendorOrderStatus> findExpiredPendingActionableStatuses(@Param("now") LocalDateTime now);
}
