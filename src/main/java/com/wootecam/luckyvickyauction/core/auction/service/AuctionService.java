package com.wootecam.luckyvickyauction.core.auction.service;

import com.wootecam.luckyvickyauction.core.auction.domain.Auction;
import com.wootecam.luckyvickyauction.core.auction.domain.AuctionRepository;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.CancelAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.dto.UpdateAuctionStockCommand;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: [update, change 메소드에서 경매 상태 조건을 확인하는 부분을 서비스가 아니라 Auction이 갖게 하도록 변경하기] [writeAt: 2024/08/15/14:18] [writeBy: HiiWee]
@Service
@Transactional
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public void createAuction(SignInInfo sellerInfo, CreateAuctionCommand command) {
        Auction auction = Auction.builder()
                .sellerId(sellerInfo.id())
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
     * 경매 시작 전에는 경매를 취소할 수 있다.
     *
     * @param signInInfo 경매를 취소하려는 사용자 정보
     * @param command    취소할 경매 정보
     */
    public void cancelAuction(SignInInfo signInInfo, CancelAuctionCommand command) {
        // 회원 권한이 판매자인지 확인한다.
        if (!signInInfo.isType(Role.SELLER)) {
            throw new UnauthorizedException("판매자만 경매를 취소할 수 있습니다.", ErrorCode.A024);
        }

        // 경매 정보를 불러온다.
        Auction auction = findAuctionObject(command.auctionId());

        // 경매의 소유주가 해당 판매자인지 확인한다.
        if (!auction.isSeller(signInInfo.id())) {
            throw new UnauthorizedException("자신이 등록한 경매만 취소할 수 있습니다.", ErrorCode.A025);
        }

        // 취소하려는 경매의 상태가 '경매 시작 전'인지 확인한다.
        if (!auction.currentStatus(command.requestTime()).isWaiting()) {
            String message = String.format("시작 전인 경매만 취소할 수 있습니다. 시작시간=%s, 요청시간=%s", auction.getStartedAt(),
                    command.requestTime());
            throw new BadRequestException(message, ErrorCode.A026);
        }

        // 취소를 진행한다.
        auctionRepository.deleteById(command.auctionId());
    }

    public void changeStock(SignInInfo signInInfo, UpdateAuctionStockCommand command) {
        if (!signInInfo.isType(Role.SELLER)) {
            throw new UnauthorizedException("판매자만 재고를 수정할 수 있습니다.", ErrorCode.A017);
        }
        Auction auction = findAuctionObject(command.auctionId());
        auction.changeStock(command.changeRequestStock(), command.requestTime(), signInInfo.id());

        auctionRepository.save(auction);
    }

    /**
     * 경매 상품에 대한 입찰(구매)을 진행한다.
     *
     * @param auctionId   경매 번호
     * @param price       구매를 원하는 가격
     * @param quantity    수량
     * @param requestTime 요청 시간
     */
    public void submitBid(long auctionId, long price, long quantity, ZonedDateTime requestTime) {
        Auction auction = findAuctionObject(auctionId);
        auction.submit(price, quantity, requestTime);
        auctionRepository.save(auction);
    }

    /**
     * 경매 단건 조회
     */
    public AuctionInfo getAuction(long auctionId) {
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
        Auction auction = findAuctionObject(auctionId);

        return Mapper.convertToBuyerAuctionInfo(auction);
    }

    /**
     * 판매자용 경매 조회
     *
     * @param auctionId 경매 ID
     * @return 판매자용 경매 정보
     */
    public SellerAuctionInfo getSellerAuction(SignInInfo sellerInfo, long auctionId) {
        Auction auction = auctionRepository.findByIdAndSellerId(auctionId, sellerInfo.id())
                .orElseThrow(() -> new NotFoundException("경매를 찾을 수 없습니다.", ErrorCode.A035));
        return Mapper.convertToSellerAuctionInfo(auction);
    }

    /**
     * 구매자용 경매 목록 조회
     *
     * @param condition
     * @return 구매자용 경매 목록
     */
    public List<BuyerAuctionSimpleInfo> getBuyerAuctionSimpleInfos(AuctionSearchCondition condition) {
        return auctionRepository.findAllBy(condition).stream()
                .map(Mapper::convertToBuyerAuctionSimpleInfo)
                .toList();
    }

    /**
     * 판매자용 경매 목록 조회
     *
     * @param condition
     * @return 판매자용 경매 목록
     */
    public List<SellerAuctionSimpleInfo> getSellerAuctionSimpleInfos(SellerAuctionSearchCondition condition) {
        return auctionRepository.findAllBy(condition).stream()
                .map(Mapper::convertToSellerAuctionSimpleInfo)
                .toList();
    }

    /**
     * 경매 상품에 대한 입찰 취소를 진행한다. - 경매 도메인 내에서 이를 quantity에 대한 검증 로직을 넣었습니다.
     *
     * @param auctionId 경매 아이디
     * @param quantity  환불할 수량
     */
    public void cancelBid(long auctionId, long quantity) {
        Auction auction = findAuctionObject(auctionId);
        auction.refundStock(quantity);
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

    private Auction findAuctionObject(long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(
                        () -> new NotFoundException("경매(Auction)를 찾을 수 없습니다. AuctionId: " + auctionId, ErrorCode.A011));
    }
}
