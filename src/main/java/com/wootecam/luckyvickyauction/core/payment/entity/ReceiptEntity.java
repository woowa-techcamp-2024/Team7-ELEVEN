package com.wootecam.luckyvickyauction.core.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "RECEIPT")
@Getter
public class ReceiptEntity {
    @Id
    private Long id;
}
