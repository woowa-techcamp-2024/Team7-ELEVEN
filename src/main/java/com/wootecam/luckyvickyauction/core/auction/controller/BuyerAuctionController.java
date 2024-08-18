package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    // 사용자는 경매 목록을 조회한다.
    @GetMapping
    public List<BuyerAuctionSimpleInfo> getAuctions(@RequestBody AuctionSearchCondition condition) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

    // 사용자는 경매의 상세정보를 조회한다.
    @GetMapping("/{auctionId}")
    public BuyerAuctionInfo getAuction(@PathVariable("auctionId") Long auctionId) {
        return auctionService.getBuyerAuction(auctionId);
    }

    // 사용자는 경매에 입찰한다.
    @PostMapping("/{auctionId}/bids")
    public void bidAuction(@PathVariable Long auctionId, long price, long quantity) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

    // 사용자는 입찰한 경매를 환불한다.
    @DeleteMapping("/{auctionId}/bids/{receiptId}")
    public void refundAuction(@PathVariable Long auctionId, @PathVariable Long receiptId) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

}
