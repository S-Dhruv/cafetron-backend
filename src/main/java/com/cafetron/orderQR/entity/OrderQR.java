package com.cafetron.orderQR.entity;

import com.cafetron.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="OrderQR")
@Getter
@Setter
@NoArgsConstructor
public class OrderQR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="order_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;

    @Column(name="qr_date", nullable = false, columnDefinition = "TEXT")
    private String qrData;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
}
