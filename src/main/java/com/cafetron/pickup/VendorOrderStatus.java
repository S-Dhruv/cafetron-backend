package com.cafetron.pickup;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_order_status")
@Getter
@Setter
@NoArgsConstructor
public class VendorOrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "order_id", nullable = false)
//    private Order order;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "vendor_id", nullable = false)
//    private Vendor vendor;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "status")
    private String status;

    @Column(name = "declined_reason")
    private String declinedReason;

    @Column(name = "action_expires_at")
    private LocalDateTime actionExpiresAt;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}