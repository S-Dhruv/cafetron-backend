package com.cafetron.wallet.entity;

import com.cafetron.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="wallet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long version;

    @PrePersist
    public void onCreate(){
        this.updatedAt=LocalDateTime.now();
        if(this.balance==null){
            this.balance=BigDecimal.ZERO;
        }
        if(this.version==null){
            this.version=0L;
        }
    }

    @PostLoad
    public void onLoad(){
        if(this.version==null){
            this.version=0L;
        }
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt=LocalDateTime.now();
    }
}
