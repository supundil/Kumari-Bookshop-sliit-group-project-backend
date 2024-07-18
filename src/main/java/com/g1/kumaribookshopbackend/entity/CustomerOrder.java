package com.g1.kumaribookshopbackend.entity;

import com.g1.kumaribookshopbackend.dto.SuperDto;
import com.g1.kumaribookshopbackend.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CustomerOrder extends SuperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oderId;
    @Column
    private LocalDateTime oderPlacedDate;
    @Column
    private LocalDateTime oderVerifiedDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(targetEntity = OrderDetail.class, mappedBy = "customerOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderDetail> orderDetailSet = new HashSet<>();

    @Override
    public SuperDto toDto() {
        return null;
    }
}
