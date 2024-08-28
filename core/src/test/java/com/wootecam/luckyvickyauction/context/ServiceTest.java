package com.wootecam.luckyvickyauction.context;

import com.wootecam.luckyvickyauction.domain.repository.AuctionRepository;
import com.wootecam.luckyvickyauction.domain.repository.MemberRepository;
import com.wootecam.luckyvickyauction.domain.repository.ReceiptRepository;
import com.wootecam.luckyvickyauction.service.auction.AuctionService;
import com.wootecam.luckyvickyauction.service.member.MemberService;
import com.wootecam.luckyvickyauction.service.payment.PaymentService;
import com.wootecam.luckyvickyauction.service.receipt.ReceiptService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class ServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected AuctionRepository auctionRepository;

    @Autowired
    protected AuctionService auctionService;

    @Autowired
    protected ReceiptRepository receiptRepository;

    @Autowired
    protected PaymentService paymentService;

    @Autowired
    protected ReceiptService receiptService;

    protected LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

    @Autowired
    protected RedisTemplate<String, Long> redisTemplate;

    @AfterEach
    void tearDown() {
        databaseCleaner.clear();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}

