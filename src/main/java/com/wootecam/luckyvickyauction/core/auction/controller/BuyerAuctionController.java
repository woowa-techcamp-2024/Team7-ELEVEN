package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.luckyvickyauction.core.auction.controller.dto.PurchaseRequest;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.auction.service.Auctioneer;
import com.wootecam.luckyvickyauction.core.member.controller.BuyerOnly;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BuyerAuctionController {

    private final AuctionService auctionService;
    private final PaymentService paymentService;
    private final Auctioneer auctioneer;

    // 사용자는 경매 목록을 조회한다.
    @GetMapping("/auctions")
    public ResponseEntity<List<BuyerAuctionSimpleInfo>> getAuctions(@RequestBody AuctionSearchCondition condition) {
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
    public ResponseEntity<Void> submitAuction(@Login SignInInfo signInInfo,
                                              @CurrentTime LocalDateTime now,
                                              @PathVariable(name = "auctionId") Long auctionId,
                                              @RequestBody PurchaseRequest purchaseRequest) {
        auctioneer.process(signInInfo, purchaseRequest.price(), auctionId, purchaseRequest.quantity(), now);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자는 입찰한 경매를 환불한다.
     *
     * @param receiptId
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/32">Github Story Issue</a>
     */
    @BuyerOnly
    @PutMapping("/receipts/{receiptId}/refund")
    public ResponseEntity<Void> refundAuction(@Login SignInInfo buyerInfo, @PathVariable("receiptId") Long receiptId) {
        paymentService.refund(buyerInfo, receiptId);
        return ResponseEntity.ok().build();
    }
}
