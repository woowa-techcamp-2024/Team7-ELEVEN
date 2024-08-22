package com.wootecam.luckyvickyauction.context;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionCoreRepository;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionJpaRepository;
import com.wootecam.luckyvickyauction.core.member.infra.MemberCoreRepository;
import com.wootecam.luckyvickyauction.core.payment.infra.ReceiptCoreRepository;
import com.wootecam.luckyvickyauction.core.payment.infra.ReceiptJpaRepository;
import com.wootecam.luckyvickyauction.global.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

// 중간에서 맵핑을 담당하는 CoreRepository 의 경우 자동 생성 대상이 아니므로 직접 명시해 줄 것
@Import({JpaConfig.class, MemberCoreRepository.class, AuctionCoreRepository.class, ReceiptCoreRepository.class})
@DataJpaTest
public abstract class RepositoryTest {

    @Autowired
    public AuctionJpaRepository auctionJpaRepository;

    @Autowired
    public ReceiptJpaRepository receiptJpaRepository;

}
