package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionStatus;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;

    /**
     * 경매 생성
     */
    public void createAuction(CreateAuctionCommand command) {
        // 경매 지속 시간 검증
        Duration diff = Duration.between(command.startedAt(), command.finishedAt());
        if (!(diff.getSeconds() % (60 * 10) == 0 && diff.getSeconds() / (60 * 10) <= 6)) {
            throw new BadRequestException("경매 지속 시간은 10분 단위여야하고, 최대 60분까지만 가능합니다. 현재: " + diff.getSeconds() % (60 * 10),
                    ErrorCode.A008);
        }

        Auction auction = Auction.builder()
                .sellerId(command.sellerId())
                .productName(command.productName())
                .originPrice(command.originPrice())
                .stock(command.stock())
                .maximumPurchaseLimitCount(command.maximumPurchaseLimitCount())
                .pricePolicy(command.pricePolicy())
                .variationDuration(command.variationDuration())
                .startedAt(command.startedAt())
                .finishedAt(command.finishedAt())
                .isShowStock(command.isShowStock())
                .build();
        auctionRepository.save(auction);
    }

    /**
     * 경매 종료 경매 시작 전에는 경매를 종료할 수 있다.
     */
    public void closeAuction(long auctionId) {
    }

    /**
     * 경매 단건 조회
     */
    public AuctionInfo getAuction(long auctionId) {
        return null;
    }

    /**
     * 경매 목록 조회
     */
    public List<AuctionInfo> getAuctions(AuctionSearchCondition condition) {
        return null;
    }

    /**
     * 경매 재고 감소
     */
    public void decreaseStock(long actionId, int quantity) {
    }

    /**
     * 경매 재고 증가
     */
    public void increaseStock(long actionId, int quantity) {
    }

    /**
     * 경매 옵션 변경
     */
    public void changeOption(UpdateAuctionCommand command) {
        // 검증
        Auction auction = auctionRepository.findById(command.auctionId())
            .orElseThrow(() -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + command.auctionId(),
                ErrorCode.A011));

        if (auction.getStatus() != AuctionStatus.WAITING) {
            throw new BadRequestException(
                "시작 전인 경매만 변경할 수 있습니다. 변경요청시간: " + command.requestTime() + ", 경매시작시간: " + auction.getStartedAt(),
                ErrorCode.A012);
        }

        // 변경 TODO
        // auction.변경해줘(command);

        // 저장
        auctionRepository.save(auction);
    }

    public void submitBid(long auctionId, long price, long quantity) {

    }
}
