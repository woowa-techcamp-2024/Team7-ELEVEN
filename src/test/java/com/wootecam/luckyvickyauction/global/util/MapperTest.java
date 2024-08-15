package com.wootecam.luckyvickyauction.global.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.payment.domain.BidHistory;
import com.wootecam.luckyvickyauction.core.payment.dto.TxHistorySimpleInfo;
import org.junit.jupiter.api.Test;

class MapperTest {

    @Test
    public void 거래내역_entity를_BuyerReceiptSimpleInfo으로_변환하면_도메인의_정보가_동일하게_전달된다() {
        // given
        Member buyer = Member.builder()
                .id(1L)
                .signInId("buyer")
                .build();

        Member seller = Member.builder()
                .id(2L)
                .signInId("seller")
                .build();

        BidHistory history = BidHistory.builder()
                .id(1L)
                .productName("상품 이름")
                .price(1000L)
                .quantity(1L)
                .buyer(buyer)
                .seller(seller)
                .build();

        // when
        TxHistorySimpleInfo dto = Mapper.convertToTxHistorySimpleInfo(history);

        // then
        assertAll(
                () -> assertEquals(history.getId(), dto.id()),
                () -> assertThat(history.getBidStatus()).isEqualTo(dto.type()),
                () -> assertEquals(history.getAuctionId(), dto.auctionId()),
                () -> assertEquals(history.getQuantity(), dto.quantity()),
                () -> assertEquals(history.getPrice(), dto.price())
        );
    }
}