package com.wootecam.luckyvickyauction.core.auction.controller;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.documentation.DocumentationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class BuyerAuctionControllerTest extends DocumentationTest {

    @Nested
    class 구매자_경매_취소_API {

        @Test
        void 성공시() throws Exception {
            // given
            Long receiptId = 1L;
            Member buyer = MemberFixture.create()
                    .build();

            // when
            ResultActions perform = mockMvc.perform(delete("/auctions/bids/{receiptId}", receiptId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .cookie(new Cookie("JSESSIONID", "sessionId"))
                    .sessionAttr("signInMember", buyer)
            );

            // then
            ResultActions docs = perform.andExpect(status().isOk());

            // docs
            docs.andDo(
                    document("auctions/bids/{receiptId}/success",
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            pathParameters(
                                    parameterWithName("receiptId").description("환불할 경매의 영수증 ID")
                            ))
            );
        }
    }

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
                    .apply(document("auctions/{auctionId}/constant_policy/success",
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
                    .apply(document("auctions/{auctionId}/percentage_policy/success",
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