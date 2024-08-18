package com.wootecam.luckyvickyauction.core.auction.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.CreateAuctionCommand;
import com.wootecam.luckyvickyauction.core.auction.fixture.CreateAuctionCommandFixture;
import com.wootecam.luckyvickyauction.documentation.DocumentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class SellerAuctionControllerTest extends DocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/198">Github issue</a>
     */
    @Nested
    class 판매자_경매_등록_API {

        @Test
        void ConstantPolicy_경매_생성() throws Exception {
            // given
            CreateAuctionCommand condition = CreateAuctionCommandFixture.create()
                    .pricePolicy(PricePolicy.createConstantPricePolicy(100))
                    .build();

            // expect
            docsGiven
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(objectMapper.writeValueAsString(condition))
                    .when().post("post /auctions success constantPolicy")
                    .then().log().all()
                    .apply(document("auctions/create",
                            requestFields(
                                    fieldWithPath("sellerId").description("판매자 ID"),
                                    fieldWithPath("productName").description("상품 이름"),
                                    fieldWithPath("originPrice").description("상품 원가"),
                                    fieldWithPath("stock").description("상품 재고"),
                                    fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                    fieldWithPath("pricePolicy").description("가격 정책"),
                                    fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                    fieldWithPath("pricePolicy.variationWidth").description("절대 가격 정책시 가격 절대 변동폭"),
                                    fieldWithPath("variationDuration").description("경매 기간"),
                                    fieldWithPath("isShowStock").description("재고 노출 여부"),
                                    fieldWithPath("requestTime").description("요청 시간"),
                                    fieldWithPath("startedAt").description("경매 시작 시간"),
                                    fieldWithPath("finishedAt").description("경매 종료 시간"),
                                    fieldWithPath("isShowStock").description("재고 노출 여부")
                            )
                    ))
                    .statusCode(200);
        }

        @Test
        void PercentagePolicy_경매_생성() throws Exception {
            // given
            CreateAuctionCommand condition = CreateAuctionCommandFixture.create()
                    .pricePolicy(PricePolicy.createPercentagePricePolicy(10))
                    .build();

            // expect
            docsGiven
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(objectMapper.writeValueAsString(condition))
                    .when().post("/auctions")
                    .then().log().all()
                    .apply(document("post /auctions success percentagePolicy",
                            requestFields(
                                    fieldWithPath("sellerId").description("판매자 ID"),
                                    fieldWithPath("productName").description("상품 이름"),
                                    fieldWithPath("originPrice").description("상품 원가"),
                                    fieldWithPath("stock").description("상품 재고"),
                                    fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                    fieldWithPath("pricePolicy").description("가격 정책"),
                                    fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                    fieldWithPath("pricePolicy.discountRate").description("퍼센트 가격 정책시 가격 할인율"),
                                    fieldWithPath("variationDuration").description("경매 기간"),
                                    fieldWithPath("isShowStock").description("재고 노출 여부"),
                                    fieldWithPath("requestTime").description("요청 시간"),
                                    fieldWithPath("startedAt").description("경매 시작 시간"),
                                    fieldWithPath("finishedAt").description("경매 종료 시간"),
                                    fieldWithPath("isShowStock").description("재고 노출 여부")
                            )
                    ))
                    .statusCode(200);
        }
    }
}