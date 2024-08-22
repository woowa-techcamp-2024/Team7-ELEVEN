package com.wootecam.luckyvickyauction.core.auction.controller;

import com.wootecam.luckyvickyauction.core.auction.controller.dto.CreateAuctionRequest;
import com.wootecam.luckyvickyauction.core.auction.controller.dto.SellerAuctionSearchRequest;
import com.wootecam.luckyvickyauction.core.auction.dto.CancelAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.service.AuctionService;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.member.controller.SellerOnly;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.LocalDateTime;
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
public class SellerAuctionController {

    private final AuctionService auctionService;

    // 판매자는 경매를 등록한다.
    @SellerOnly
    @PostMapping
    public ResponseEntity<Void> createAuction(@Login SignInInfo sellerInfo,
                                              @RequestBody CreateAuctionRequest request,
                                              @CurrentTime LocalDateTime now) {
        CreateAuctionCommand command = new CreateAuctionCommand(request.productName(), request.originPrice(),
                request.stock(), request.maximumPurchaseLimitCount(), request.pricePolicy(),
                request.variationDuration(), now, request.startedAt(), request.finishedAt(), request.isShowStock());
        auctionService.createAuction(sellerInfo, command);
        return ResponseEntity.ok().build();
    }

    /**
     * 판매자는 경매를 취소한다.
     *
     * @param auctionId 취소할 경매의 ID
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/171">Github issue</a>
     */
    @SellerOnly
    @DeleteMapping("/{auctionId}")
    public void cancelAuction(@Login SignInInfo sellerInfo,
                              @PathVariable("auctionId") Long auctionId,
                              @CurrentTime LocalDateTime now) {
        CancelAuctionCommand command = new CancelAuctionCommand(now, auctionId);
        auctionService.cancelAuction(sellerInfo, command);
    }

    // 판매자는 자신이 등록한 경매 목록을 조회한다.
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerAuctionSimpleInfo>> getSellerAuctions(@Login SignInInfo sellerInfo,
                                                                           @RequestBody SellerAuctionSearchRequest request) {
        SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(
                sellerInfo.id(),
                request.offset(),
                request.size());
        List<SellerAuctionSimpleInfo> infos = auctionService.getSellerAuctionSimpleInfos(condition);
        return ResponseEntity.ok(infos);
    }

    // 판매자는 자신이 등록한 경매를 상세 조회한다.
    @SellerOnly
    @GetMapping("/{auctionId}/seller")
    public ResponseEntity<SellerAuctionInfo> getSellerAuction(@Login SignInInfo sellerInfo,
                                                              @PathVariable("auctionId") Long auctionId) {
        SellerAuctionInfo sellerAuctionInfo = auctionService.getSellerAuction(sellerInfo, auctionId);
        return ResponseEntity.ok(sellerAuctionInfo);
    }
}
