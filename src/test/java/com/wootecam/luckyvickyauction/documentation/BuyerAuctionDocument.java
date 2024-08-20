package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.auction.controller.dto.BidRequest;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.fixture.BuyerAuctionInfoFixture;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import jakarta.servlet.http.Cookie;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

class BuyerAuctionDocument extends DocumentationTest {

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/89">Github issue</a>
     */
    @Nested
    class 구매자_경매_목록_조회 {

        @Test
        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() {
            AuctionSearchCondition condition = new AuctionSearchCondition(0, 2);
            List<BuyerAuctionSimpleInfo> infos = buyerAuctionSimpleInfosSample();
            given(auctionService.getBuyerAuctionSimpleInfos(any())).willReturn(infos);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(condition)
                    .when().get("/auctions")
                    .then().log().all()
                    .apply(document("auctions/getAuctions/success",
                            requestFields(
                                    fieldWithPath("offset").type(JsonFieldType.NUMBER)
                                            .description("조회를 시작할 순서"),
                                    fieldWithPath("size").type(JsonFieldType.NUMBER)
                                            .description("조회할 페이지 크기")
                                            .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }

        private List<BuyerAuctionSimpleInfo> buyerAuctionSimpleInfosSample() {
            List<BuyerAuctionSimpleInfo> infos = new ArrayList<>();

            for (long i = 1; i <= 2; i++) {
                BuyerAuctionSimpleInfo simpleInfo = new BuyerAuctionSimpleInfo(i, "쓸만한 경매품 " + i, i * 2000,
                        ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(30L));
                infos.add(simpleInfo);
            }

            return infos;
        }
    }

    @Nested
    class 구매자_경매_취소_API {

        @Test
        void 성공시() throws Exception {
            // given
            Long receiptId = 1L;
            SignInInfo signInInfo = new SignInInfo(1L, Role.BUYER);

            // when
            ResultActions perform = mockMvc.perform(delete("/auctions/bids/{receiptId}", receiptId)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .cookie(new Cookie("JSESSIONID", "sessionId"))
                    .sessionAttr("signInMember", signInInfo)
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
            Long auctionId = 1L;
            BuyerAuctionInfo response = BuyerAuctionInfoFixture.create()
                    .auctionId(auctionId)
                    .pricePolicy(PricePolicy.createPercentagePricePolicy(10))
                    .build();
            given(auctionService.getBuyerAuction(auctionId))
                    .willReturn(response);

            docsGiven.accept(MediaType.APPLICATION_JSON_VALUE)
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

    @Nested
    class 구매자_경매_입찰 {

        @Test
        void 경매_입찰을_성공하면_OK응답을_반환한다() throws Exception {
            String auctionId = "1";
            BidRequest bidRequest = new BidRequest(10000L, 20L);
            SignInInfo buyerInfo = new SignInInfo(1L, Role.BUYER);
            willDoNothing().given(paymentService)
                    .process(any(SignInInfo.class), anyLong(), anyLong(), anyLong(), any(ZonedDateTime.class));
            given(authenticationContext.getPrincipal()).willReturn(buyerInfo);

            mockMvc.perform(post("/auctions/{auctionId}/bids", auctionId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", buyerInfo)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .content(objectMapper.writeValueAsString(bidRequest)))
                    .andDo(document("auctions/bids/success",
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            pathParameters(
                                    parameterWithName("auctionId").description("입찰한 경매의 ID")
                            ),
                            requestFields(
                                    fieldWithPath("price").type(JsonFieldType.NUMBER)
                                            .description("입찰을 희망하는 가격"),
                                    fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                            .description("입찰을 희망하는 수량")
                            )
                    ))
                    .andExpect(status().isOk());
        }
    }
}
