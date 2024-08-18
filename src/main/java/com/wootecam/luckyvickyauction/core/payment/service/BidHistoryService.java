package com.wootecam.luckyvickyauction.core.payment.service;

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

// TODO [추후 Controller로 책임 변경] [writeAt: 2024/08/16/16:31] [writeBy: minseok-oh]
@Service
@RequiredArgsConstructor
public class BidHistoryService {

    private final BidHistoryRepository bidHistoryRepository;

    public BidHistoryInfo getBidHistoryInfo(long bidHistoryId) {
        // 검증
        BidHistory bidHistory = bidHistoryRepository.findById(bidHistoryId)
                .orElseThrow(() -> new NotFoundException("입찰 내역을 찾을 수 없습니다. 내역 id=" + bidHistoryId, ErrorCode.B000));

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
