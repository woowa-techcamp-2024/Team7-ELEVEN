package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.controller.BuyerOnly;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class BuyerAuctionController {

    private final AuctionService auctionService;

    private final PaymentService paymentService;

    // 사용자는 경매 목록을 조회한다.
    @GetMapping
    public ResponseEntity<List<BuyerAuctionSimpleInfo>> getAuctions(@RequestBody AuctionSearchCondition condition) {
        List<BuyerAuctionSimpleInfo> infos = auctionService.getBuyerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    // 사용자는 경매의 상세정보를 조회한다.
    @GetMapping("/{auctionId}")
    public ResponseEntity<BuyerAuctionInfo> getAuction(@PathVariable("auctionId") Long auctionId) {
        BuyerAuctionInfo result = auctionService.getBuyerAuction(auctionId);
        return ResponseEntity.ok(result);
    }

    // 사용자는 경매에 입찰한다.
    @PostMapping("/{auctionId}/bids")
    public void bidAuction(@PathVariable Long auctionId, long price, long quantity) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

    /**
     * 사용자는 입찰한 경매를 환불한다.
     *
     * @param receiptId
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/32">Github Story Issue</a>
     */
    @BuyerOnly
    @DeleteMapping("/bids/{receiptId}")
    public ResponseEntity<Void> refundAuction(@Login SignInInfo buyerInfo, @PathVariable("receiptId") Long receiptId) {
        paymentService.refund(buyerInfo, receiptId);
        return ResponseEntity.ok().build();
    }

}
