package com.wootecam.api.controller;

import com.wootecam.api.controller.annotation.CurrentTime;
import com.wootecam.api.controller.annotation.Login;
import com.wootecam.api.controller.annotation.SellerOnly;
import com.wootecam.api.dto.CreateAuctionRequest;
import com.wootecam.core.dto.auction.command.CancelAuctionCommand;
import com.wootecam.core.dto.auction.command.CreateAuctionCommand;
import com.wootecam.core.dto.auction.condition.SellerAuctionSearchCondition;
import com.wootecam.core.dto.auction.info.SellerAuctionInfo;
import com.wootecam.core.dto.auction.info.SellerAuctionSimpleInfo;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.service.auction.AuctionService;
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
import org.springframework.web.bind.annotation.RequestParam;
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
                                                                           @RequestParam(name = "offset") int offset,
                                                                           @RequestParam(name = "size") int size) {
        SellerAuctionSearchCondition condition = new SellerAuctionSearchCondition(sellerInfo.id(), offset, size);
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
