package com.wootecam.luckyvickyauction.service.receipt;

import com.wootecam.luckyvickyauction.domain.entity.Receipt;
import com.wootecam.luckyvickyauction.domain.repository.ReceiptRepository;
import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.dto.receipt.info.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.dto.receipt.info.ReceiptInfo;
import com.wootecam.luckyvickyauction.dto.receipt.info.SellerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.exception.AuthorizationException;
import com.wootecam.luckyvickyauction.exception.ErrorCode;
import com.wootecam.luckyvickyauction.exception.NotFoundException;
import com.wootecam.luckyvickyauction.util.Mapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public ReceiptInfo getReceiptInfo(SignInInfo memberInfo, UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new NotFoundException("거래 내역을 찾을 수 없습니다. id=" + receiptId, ErrorCode.R000));

        if (!receipt.isOwnedBy(memberInfo.id())) {
            throw new AuthorizationException("자신이 판매한 거래 내역 혹은 구매한 거래 내역만 조회할 수 있습니다.", ErrorCode.R001);
        }

        return Mapper.convertToReceiptInfo(receipt);
    }

    public List<BuyerReceiptSimpleInfo> getBuyerReceiptSimpleInfos(SignInInfo buyerInfo,
                                                                   BuyerReceiptSearchCondition condition) {
        List<Receipt> receipts = receiptRepository.findAllByBuyerId(buyerInfo.id(), condition);
        return receipts.stream()
                .map(Mapper::convertToBuyerReceiptSimpleInfo)
                .toList();
    }

    public List<SellerReceiptSimpleInfo> getSellerReceiptSimpleInfos(SignInInfo sellerInfo,
                                                                     SellerReceiptSearchCondition condition) {
        List<Receipt> receipts = receiptRepository.findAllBySellerId(sellerInfo.id(), condition);
        return receipts.stream()
                .map(Mapper::convertToSellerReceiptSimpleInfo)
                .toList();
    }
}