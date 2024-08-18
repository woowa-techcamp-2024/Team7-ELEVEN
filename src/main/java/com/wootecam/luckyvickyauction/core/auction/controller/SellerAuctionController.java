package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class SellerAuctionController {

    private final AuctionService auctionService;

    // 판매자는 경매를 생성한다.
    @PostMapping
    public ResponseEntity<Void> createAuction(@RequestBody CreateAuctionCommand command) {
        auctionService.createAuction(command);
        return ResponseEntity.ok().build();
    }

    // 판매자는 경매를 취소한다.
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(@PathVariable Long auctionId) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

    // 판매자는 자신이 등록한 경매 목록을 조회한다.
    @GetMapping("/seller")
    public List<SellerAuctionSimpleInfo> getSellerAuctions() {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

    // 판매자는 자신이 등록한 경매를 상세 조회한다.
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfo> getSellerAuction(@PathVariable Long auctionId) {
        SellerAuctionInfo sellerAuctionInfo = auctionService.getSellerAuction(auctionId);
        return ResponseEntity.ok(sellerAuctionInfo);
    }

    // 판매자는 자신의 경매 상품의 재고를 수정한다.
    @PatchMapping("/{auctionId}/stock")
    public void changeAuctionStock(@PathVariable Long auctionId, @RequestParam long amount) {
        // TODO: [Task에 맞게 로직 구현할 것!] [writeAt: 2024/08/16/17:40] [writeBy: chhs2131]
        throw new UnsupportedOperationException();
    }

}
