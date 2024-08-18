package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ReceiptDocument extends DocumentationTest {

    @Nested
    class 거래내역_상세_조회 {

        @Test
        void 거래내역_id에_해당하는_거래내역을_조회한다() {
            String receiptId = "1";
            BidHistoryInfo bidHistoryInfo = BidHistoryInfo.builder()
                    .bidHistoryId(1L)
                    .productName("상품명")
                    .price(1000L)
                    .quantity(1L)
                    .bidStatus(BidStatus.BID)
                    .auctionId(1L)
                    .sellerId(1L)
                    .buyerId(2L)
                    .createdAt(ZonedDateTime.now())
                    .updatedAt(ZonedDateTime.now().plusHours(1))
                    .build();
            given(bidHistoryService.getBidHistoryInfo(anyLong())).willReturn(bidHistoryInfo);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/receipts/{receiptId}", receiptId)
                    .then().log().all()
                    .apply(document("receipts/getReceipt/success",
                                    pathParameters(
                                            parameterWithName("receiptId").description("조회할 거래내역 ID")
                                    )
                            )
                    )
                    .statusCode(HttpStatus.OK.value());
        }
    }
}
