package com.wootecam.test.context;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class ServiceTest {

//    @Autowired
//    private DatabaseCleaner databaseCleaner;
//
//    @Autowired
//    protected MemberRepository memberRepository;
//
//    @Autowired
//    protected MemberService memberService;
//
//    @Autowired
//    protected AuctionRepository auctionRepository;
//
//    @Autowired
//    protected Auctioneer auctioneer;
//
//    @Autowired
//    protected ReceiptRepository receiptRepository;
//
//    @Autowired
//    protected PaymentService paymentService;
//
//    @Autowired
//    protected ReceiptService receiptService;
//    @Autowired
//    protected RedisTemplate<String, Long> redisTemplate;
//
//    @AfterEach
//    void tearDown() {
//        databaseCleaner.clear();
//        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
//    }

    protected LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);

}
