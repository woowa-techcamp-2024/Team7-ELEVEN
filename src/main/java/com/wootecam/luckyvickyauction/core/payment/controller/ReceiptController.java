package com.wootecam.luckyvickyauction.core.payment.controller;

import com.wootecam.core.domain.type.Role;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.info.BuyerReceiptSimpleInfo;
import com.wootecam.core.dto.receipt.info.ReceiptInfo;
import com.wootecam.core.dto.receipt.info.SellerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.controller.BuyerOnly;
import com.wootecam.luckyvickyauction.core.member.controller.Login;
import com.wootecam.luckyvickyauction.core.member.controller.Roles;
import com.wootecam.luckyvickyauction.core.member.controller.SellerOnly;
import com.wootecam.luckyvickyauction.core.payment.service.ReceiptService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // 구매자는 자신의 거래 이력 목록을 조회할 수 있다.
    @BuyerOnly
    @GetMapping("/buyer")
    public ResponseEntity<List<BuyerReceiptSimpleInfo>> getReceipts(@Login SignInInfo buyerInfo,
                                                                    @RequestParam(name = "offset") int offset,
                                                                    @RequestParam(name = "size") int size) {
        BuyerReceiptSearchCondition condition = new BuyerReceiptSearchCondition(offset, size);
        List<BuyerReceiptSimpleInfo> infos = receiptService.getBuyerReceiptSimpleInfos(buyerInfo, condition);
        return ResponseEntity.ok(infos);
    }

    // 판매자는 자신의 거래 이력 목록을 조회할 수 있다.
    @SellerOnly
    @GetMapping("/seller")
    public ResponseEntity<List<SellerReceiptSimpleInfo>> getSellerReceipts(@Login SignInInfo sellerInfo,
                                                                           @RequestParam(name = "offset") int offset,
                                                                           @RequestParam(name = "size") int size) {
        SellerReceiptSearchCondition condition = new SellerReceiptSearchCondition(offset, size);
        List<SellerReceiptSimpleInfo> infos = receiptService.getSellerReceiptSimpleInfos(sellerInfo, condition);
        return ResponseEntity.ok(infos);
    }

    // 사용자는 자신의 거래 이력을 상세 조회할 수 있다.
    @Roles({Role.BUYER, Role.SELLER})
    @GetMapping("/{receiptId}")
    public ResponseEntity<ReceiptInfo> getReceipt(@Login SignInInfo memberInfo,
                                                  @PathVariable("receiptId") UUID receiptId) {
        ReceiptInfo receiptInfo = receiptService.getReceiptInfo(memberInfo, receiptId);
        return ResponseEntity.ok(receiptInfo);
    }
}
