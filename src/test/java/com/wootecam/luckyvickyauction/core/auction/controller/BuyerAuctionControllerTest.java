package com.wootecam.luckyvickyauction.core.auction.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.fixture.BuyerAuctionInfoFixture;
import com.wootecam.luckyvickyauction.documentation.DocumentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class BuyerAuctionControllerTest extends DocumentationTest {

    @Nested
    class 구매자_경매_단건_조회_API {

        @Test
        void ConstantPolicy_경매_조회시() {
            // given
            Long auctionId = 1L;
            BuyerAuctionInfo response = BuyerAuctionInfoFixture.create()
                    .auctionId(auctionId)
                    .pricePolicy(PricePolicy.createConstantPricePolicy(10))
                    .build();
            given(auctionService.getBuyerAuction(auctionId))
                    .willReturn(response);

            // when
            docsGiven
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/auctions/{auctionId}", auctionId.toString())
                    .then().log().all()
                    .apply(document("auctions/{auctionId}",
                            responseFields(
                                    fieldWithPath("auctionId").description("경매 ID"),
                                    fieldWithPath("sellerId").description("판매자 ID"),
                                    fieldWithPath("productName").description("상품 이름"),
                                    fieldWithPath("originPrice").description("상품 원가"),
                                    fieldWithPath("currentPrice").description("현재 가격"),
                                    fieldWithPath("stock").description("현재 재고"),
                                    fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 수량 제한"),
                                    fieldWithPath("pricePolicy").description("가격 정책"),
                                    fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                    fieldWithPath("pricePolicy.variationWidth").description("절대 변동 폭"),
                                    fieldWithPath("variationDuration").description("가격 변동 주기"),
                                    fieldWithPath("startedAt").description("경매 시작 시간"),
                                    fieldWithPath("finishedAt").description("경매 종료 시간")
                            )
                    ))
                    .statusCode(200);

        }

        @Test
        void PercentagePolicy_경매_조회시() {
            // given
            Long auctionId = 1L;
            BuyerAuctionInfo response = BuyerAuctionInfoFixture.create()
                    .auctionId(auctionId)
                    .pricePolicy(PricePolicy.createPercentagePricePolicy(10))
                    .build();
            given(auctionService.getBuyerAuction(auctionId))
                    .willReturn(response);

            // when
            docsGiven
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/auctions/{auctionId}", auctionId.toString())
                    .then().log().all()
                    .apply(document("auctions/{auctionId}",
                            responseFields(
                                    fieldWithPath("auctionId").description("경매 ID"),
                                    fieldWithPath("sellerId").description("판매자 ID"),
                                    fieldWithPath("productName").description("상품 이름"),
                                    fieldWithPath("originPrice").description("상품 원가"),
                                    fieldWithPath("currentPrice").description("현재 가격"),
                                    fieldWithPath("stock").description("현재 재고"),
                                    fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 수량 제한"),
                                    fieldWithPath("pricePolicy").description("가격 정책"),
                                    fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                    fieldWithPath("pricePolicy.discountRate").description("가격 변동 할인율"),
                                    fieldWithPath("variationDuration").description("가격 변동 주기"),
                                    fieldWithPath("startedAt").description("경매 시작 시간"),
                                    fieldWithPath("finishedAt").description("경매 종료 시간")
                            )
                    ))
                    .statusCode(200);

        }
    }
}