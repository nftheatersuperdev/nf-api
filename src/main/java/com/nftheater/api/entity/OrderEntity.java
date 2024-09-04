package com.nftheater.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity(name = "orderEntity")
@Table(name = "`order`")
public class OrderEntity extends AuditDateEntity {

    @Id
    @Column(name = "ref_no")
    private String refNo;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "package_id")
    private String packageId;
    @Column(name = "order_no")
    private String orderNo;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "status")
    private String status;



}
