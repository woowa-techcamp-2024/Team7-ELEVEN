package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.core.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.core.dto.auction.info.BuyerAuctionInfo;
import com.wootecam.core.dto.auction.info.BuyerAuctionSimpleInfo;
import com.wootecam.core.dto.auction.message.AuctionPurchaseRequestMessage;
import com.wootecam.core.dto.auction.message.AuctionRefundRequestMessage;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.core.auction.controller.dto.PurchaseRequest;
import com.wootecam.luckyvickyauction.core.auction.controller.dto.PurchaseResponse;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.controller.BuyerOnly;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BuyerAuctionController {

    private final AuctionService auctionService;
    private final PaymentService paymentService;
    private final Auctioneer auctioneer;

    // 사용자는 경매 목록을 조회한다.
    @GetMapping("/auctions")
    public ResponseEntity<List<BuyerAuctionSimpleInfo>> getAuctions(@RequestParam(name = "offset") int offset,
                                                                    @RequestParam(name = "size") int size) {
        AuctionSearchCondition condition = new AuctionSearchCondition(offset, size);
        List<BuyerAuctionSimpleInfo> infos = auctionService.getBuyerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    // 사용자는 경매의 상세정보를 조회한다.
    @GetMapping("/auctions/{auctionId}")
    public ResponseEntity<BuyerAuctionInfo> getAuction(@PathVariable("auctionId") Long auctionId) {
        BuyerAuctionInfo result = auctionService.getBuyerAuction(auctionId);
        return ResponseEntity.ok(result);
    }

    // 사용자는 경매에 입찰한다.
    @BuyerOnly
    @PostMapping("/auctions/{auctionId}/purchase")
    public ResponseEntity<PurchaseResponse> submitAuction(@Login SignInInfo signInInfo,
                                                          @CurrentTime LocalDateTime now,
                                                          @PathVariable(name = "auctionId") Long auctionId,
                                                          @RequestBody PurchaseRequest purchaseRequest) {
        AuctionPurchaseRequestMessage requestMessage = AuctionPurchaseRequestMessage.builder()
                .requestId(UUID.randomUUID())
                .buyerId(signInInfo.id())
                .auctionId(auctionId)
                .price(purchaseRequest.price())
                .quantity(purchaseRequest.quantity())
                .requestTime(now)
                .build();
        auctioneer.process(requestMessage);

        PurchaseResponse response = new PurchaseResponse(requestMessage.requestId());
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자는 입찰한 경매를 환불한다.
     *
     * @param receiptId
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/32">Github Story Issue</a>
     */
    @BuyerOnly
    @PutMapping("/receipts/{receiptId}/refund")
    public ResponseEntity<Void> refundAuction(@Login SignInInfo buyerInfo,
                                              @PathVariable("receiptId") UUID receiptId,
                                              @CurrentTime LocalDateTime now) {
        var message = new AuctionRefundRequestMessage(buyerInfo, receiptId, now);
        auctioneer.refund(message);
        return ResponseEntity.ok().build();
    }
}
