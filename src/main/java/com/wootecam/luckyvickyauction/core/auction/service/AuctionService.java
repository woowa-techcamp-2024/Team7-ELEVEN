package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionStatus;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionStockCommand;
import com.wootecam.luckyvickyauction.core.auction.infra.AuctionRepository;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

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
                .currentPrice(command.originPrice())
                .originPrice(command.originPrice())
                .currentStock(command.stock())
                .originStock(command.stock())
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
        // auctionRepository 에서 auctionId로 조회
        Auction auction = findAuctionObject(auctionId);

        return Mapper.convertToAuctionInfo(auction);
    }

    /**
     * 구매자용 경매 조회
     *
     * @param auctionId 경매 ID
     * @return 구매자용 경매 정보
     */
    public BuyerAuctionInfo getBuyerAuction(long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId,
                        ErrorCode.A011));

        return Mapper.convertToBuyerAuctionInfo(auction);
    }

    /**
     * 판매자용 경매 조회
     *
     * @param auctionId 경매 ID
     * @return 판매자용 경매 정보
     */
    public SellerAuctionInfo getSellerAuction(long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId,
                        ErrorCode.A011));

        return Mapper.convertToSellerAuctionInfo(auction);
    }

    /**
     * 경매 목록 조회
     */
    public List<AuctionInfo> getAuctions(AuctionSearchCondition condition) {
        return null;
    }

    public void changeStock(SignInInfo signInInfo, UpdateAuctionStockCommand command) {
        if (!signInInfo.isType(Role.SELLER)) {
            throw new UnauthorizedException("판매자만 재고를 수정할 수 있습니다.", ErrorCode.A017);
        }
        Auction auction = findAuctionObject(command.auctionId());

        if (!auction.currentStatus(command.requestTime()).isWaiting()) {
            String message = String.format("시작 전인 경매의 재고만 수정할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(),
                    command.requestTime());
            throw new BadRequestException(message, ErrorCode.A021);
        }
        if (!auction.isSeller(signInInfo.id())) {
            throw new UnauthorizedException("자신이 등록한 경매만 수정할 수 있습니다.", ErrorCode.A018);
        }

        auction.changeStock(command.changeRequestStock());

        auctionRepository.save(auction);
    }

    /**
     * 경매 옵션 변경
     */
    public void changeOption(UpdateAuctionCommand command) {
        // 검증
        Auction auction = findAuctionObject(command.auctionId());

        if (!auction.currentStatus(command.requestTime()).isWaiting()) {
            throw new BadRequestException(
                    "시작 전인 경매만 변경할 수 있습니다. 변경요청시간: " + command.requestTime() + ", 경매시작시간: " + auction.getStartedAt(),
                    ErrorCode.A012);
        }

        // 변경 TODO
        // auction.변경해줘(command);

        // 저장
        auctionRepository.save(auction);
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 진행한다.
     *
     * @param auctionId   경매 번호
     * @param price       구매를 원하는 가격
     * @param quantity    수량
     * @param requestTime
     */
    public void submitBid(long auctionId, long price, long quantity, ZonedDateTime requestTime) {
        // 검증
        Auction auction = findAuctionObject(auctionId);

        if (!auction.canPurchase(quantity)) {
            throw new BadRequestException(
                    "해당 수량만큼 구매할 수 없습니다. 재고: " + auction.getCurrentStock() + ", "
                            + "요청: " + quantity + ", 인당구매제한: " + auction.getMaximumPurchaseLimitCount(),
                    ErrorCode.A014);
        }

        AuctionStatus currentStatus = auction.currentStatus(requestTime);
        if (!currentStatus.isRunning()) {
            throw new BadRequestException(
                    "진행 중인 경매에만 입찰할 수 있습니다. 현재상태: " + currentStatus,
                    ErrorCode.A016);
        }

        // TODO 구매(입찰) 로직
    }

    /**
     * 경매 상품에 대한 입찰 취소를 진행한다. - 경매 도메인 내에서 이를 quantity에 대한 검증 로직을 넣었습니다.
     *
     * @param auctionId 경매 아이디
     * @param quantity  환불할 수량
     */
    public void cancelBid(long auctionId, long quantity) {
        Auction auction = findAuctionObject(auctionId);
        auction.updateCurrentStock(quantity);
        auctionRepository.save(auction);
    }

    private Auction findAuctionObject(long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A011));
    }
}
