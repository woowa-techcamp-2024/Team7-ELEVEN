package com.wootecam.luckyvickyauction.core.payment.service;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
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

    public BidHistoryInfo getBidHistoryInfo(Member member, long bidHistoryId) {
        // 검증
        BidHistory bidHistory = bidHistoryRepository.findById(bidHistoryId)
                .orElseThrow(() -> new NotFoundException("입찰 내역을 찾을 수 없습니다. 내역 id=" + bidHistoryId, ErrorCode.B000));

        if (!bidHistory.isOwnedBy(member)) {
            throw new UnauthorizedException(
                    "해당 거래 내역에 접근 권한이 없는 사용자입니다. 회원ID: " + (member == null ? "없음" : member.getSignInId()),
                    ErrorCode.B001);
        }

        // 반환
        return Mapper.convertToBidHistoryInfo(bidHistory);
    }

    public List<BuyerReceiptSimpleInfo> getBuyerReceiptSimpleInfos(BuyerReceiptSearchCondition condition) {
        List<BidHistory> bidHistories = bidHistoryRepository.findAllBy(condition);
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
