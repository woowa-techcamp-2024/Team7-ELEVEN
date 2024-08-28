package com.wootecam.luckyvickyauction.dto.receipt.info;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.domain.entity.Member;
import com.wootecam.luckyvickyauction.domain.entity.Point;
import com.wootecam.luckyvickyauction.domain.entity.type.ReceiptStatus;
import com.wootecam.luckyvickyauction.domain.entity.type.Role;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReceiptInfoTest {

    @Test
    void 입찰_내역_생성_요청을_정상적으로_처리한다() {
        // given
        String productName = "상품이름";
        long price = 10000L;
        long quantity = 1L;
        ReceiptStatus receiptStatus = ReceiptStatus.PURCHASED;
        Long auctionId = 1L;
        Member seller = Member.builder()
                .id(1L)
                .signInId("buyerId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(1000L))
                .build();
        Member buyer = Member.builder()
                .id(2L)
                .signInId("sellerId")
                .password("password00")
                .role(Role.SELLER)
                .point(new Point(1000L))
                .build();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        UUID id = UUID.randomUUID();
        ReceiptInfo receiptInfo = new ReceiptInfo(id, productName, price, quantity, receiptStatus, auctionId,
                seller.getId(), buyer.getId(), createdAt, updatedAt);

        // then
        assertAll(
                () -> assertThat(receiptInfo.receiptId()).isEqualTo(id),
                () -> assertThat(receiptInfo.productName()).isEqualTo(productName),
                () -> assertThat(receiptInfo.price()).isEqualTo(price),
                () -> assertThat(receiptInfo.quantity()).isEqualTo(quantity),
                () -> assertThat(receiptInfo.receiptStatus()).isEqualTo(receiptStatus),
                () -> assertThat(receiptInfo.auctionId()).isEqualTo(auctionId),
                () -> assertThat(receiptInfo.sellerId()).isEqualTo(seller.getId()),
                () -> assertThat(receiptInfo.buyerId()).isEqualTo(buyer.getId())
        );
    }
}
