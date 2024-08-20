package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistoryRepository;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import com.wootecam.luckyvickyauction.global.exception.NotFoundException;
import com.wootecam.luckyvickyauction.global.exception.UnauthorizedException;
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidHistoryService {

    private final BidHistoryRepository bidHistoryRepository;

    public BidHistoryInfo getBidHistoryInfo(SignInInfo memberInfo, long bidHistoryId) {
        BidHistory bidHistory = bidHistoryRepository.findById(bidHistoryId)
                .orElseThrow(() -> new NotFoundException("입찰 내역을 찾을 수 없습니다. 내역 id=" + bidHistoryId, ErrorCode.B000));

        if (!bidHistory.isOwnedBy(memberInfo.id())) {
            throw new UnauthorizedException("자신이 판매한 내역 혹은 구매한 내역만 조회할 수 있습니다.", ErrorCode.B001);
        }

        return Mapper.convertToBidHistoryInfo(bidHistory);
    }

    public List<BuyerReceiptSimpleInfo> getBuyerReceiptSimpleInfos(SignInInfo buyerInfo,
                                                                   BuyerReceiptSearchCondition condition) {
        List<BidHistory> bidHistories = bidHistoryRepository.findAllByBuyerId(buyerInfo.id(), condition);
        return bidHistories.stream()
                .map(Mapper::convertToBuyerReceiptSimpleInfo)
                .toList();
    }

    public List<SellerReceiptSimpleInfo> getSellerReceiptSimpleInfos(SignInInfo sellerInfo,
                                                                     SellerReceiptSearchCondition condition) {
        List<BidHistory> bidHistories = bidHistoryRepository.findAllBySellerId(sellerInfo.id(), condition);
        return bidHistories.stream()
                .map(Mapper::convertToSellerReceiptSimpleInfo)
                .toList();
    }
}
