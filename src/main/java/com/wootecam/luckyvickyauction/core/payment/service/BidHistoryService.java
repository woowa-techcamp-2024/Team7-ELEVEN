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
import com.wootecam.luckyvickyauction.global.util.Mapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidHistoryService {

    private final BidHistoryRepository bidHistoryRepository;

    public BidHistoryInfo getBidHistoryInfo(SignInInfo memberInfo, long bidHistoryId) {
        BidHistory bidHistory = bidHistoryRepository.findByIdAndMemberId(bidHistoryId, memberInfo.id(),
                        memberInfo.role())
                .orElseThrow(() -> new NotFoundException("입찰 내역을 찾을 수 없습니다. 내역 id=" + bidHistoryId, ErrorCode.B000));

        return Mapper.convertToBidHistoryInfo(bidHistory);
    }

    public List<BuyerReceiptSimpleInfo> getBuyerReceiptSimpleInfos(SignInInfo buyerInfo,
                                                                   BuyerReceiptSearchCondition condition) {
        List<BidHistory> bidHistories = bidHistoryRepository.findAllByBuyerId(buyerInfo.id(), condition);
        return bidHistories.stream()
                .map(Mapper::convertToBuyerReceiptSimpleInfo)
                .toList();
    }

    public List<SellerReceiptSimpleInfo> getSellerReceiptSimpleInfos(SellerReceiptSearchCondition condition) {
        List<BidHistory> bidHistories = bidHistoryRepository.findAllBy(condition);
        return bidHistories.stream()
                .map(Mapper::convertToSellerReceiptSimpleInfo)
                .toList();
    }
}
