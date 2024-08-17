package com.wootecam.luckyvickyauction.context;

import com.wootecam.luckyvickyauction.core.auction.infra.AuctionJpaRepository;
import com.wootecam.luckyvickyauction.core.payment.infra.ReceiptJpaRepository;
import com.wootecam.luckyvickyauction.global.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(JpaConfig.class)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    public AuctionJpaRepository auctionJpaRepository;

    @Autowired
    public ReceiptJpaRepository receiptJpaRepository;

}
