package com.wootecam.luckyvickyauction.core.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MEMBERS")
public class MemberEntity {
    @Id
    private Long id;
}
