package com.wootecam.luckyvickyauction.documentation;

import static com.wootecam.luckyvickyauction.documentation.DocumentFormatGenerator.getAttribute;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.domain.entity.type.PricePolicy;
import com.wootecam.luckyvickyauction.domain.entity.type.Role;
import com.wootecam.luckyvickyauction.dto.PurchaseRequest;
import com.wootecam.luckyvickyauction.dto.auction.info.BuyerAuctionInfo;
import com.wootecam.luckyvickyauction.dto.auction.info.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.dto.member.info.SignInInfo;
import com.wootecam.luckyvickyauction.fixture.BuyerAuctionInfoFixture;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;

class BuyerAuctionDocument extends DocumentationTest {

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/89">Github issue</a>
     */
    @Nested
    class 사용자_경매_목록_조회 {

        @Test
        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() {
            List<BuyerAuctionSimpleInfo> infos = buyerAuctionSimpleInfosSample();
            given(auctionService.getBuyerAuctionSimpleInfos(any())).willReturn(infos);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParams("offset", 0, "size", 2)
                    .when().get("/auctions")
                    .then().log().all()
                    .apply(MockMvcRestDocumentation.document("memberAuctions/findAllBuyerAuctions/success",
                            RequestDocumentation.queryParameters(
                                    RequestDocumentation.parameterWithName("offset").description("조회를 시작할 순서"),
                                    RequestDocumentation.parameterWithName("size").description("조회할 페이지 크기")
                                            .attributes(Attributes.key("constraints").value("최소: 1 ~ 최대: 100"))
                            ),
                            PayloadDocumentation.responseFields(
                                    PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                            .description("경매 상품 ID"),
                                    PayloadDocumentation.fieldWithPath("[].title").type(JsonFieldType.STRING)
                                            .description("경매 상품 이름"),
                                    PayloadDocumentation.fieldWithPath("[].price").type(JsonFieldType.NUMBER)
                                            .description("경매 상품 가격"),
                                    PayloadDocumentation.fieldWithPath("[].startedAt").type(JsonFieldType.STRING)
                                            .description("경매 시작 시간"),
                                    PayloadDocumentation.fieldWithPath("[].finishedAt").type(JsonFieldType.STRING)
                                            .description("경매 종료 시간")
                            )
                    ))
                    .statusCode(HttpStatus.OK.value());
        }

        private List<BuyerAuctionSimpleInfo> buyerAuctionSimpleInfosSample() {
            List<BuyerAuctionSimpleInfo> infos = new ArrayList<>();

            for (long i = 1; i <= 2; i++) {
                BuyerAuctionSimpleInfo simpleInfo = new BuyerAuctionSimpleInfo(i, "쓸만한 경매품 " + i, i * 2000,
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(30L));
                infos.add(simpleInfo);
            }

            return infos;
        }
    }

    @Nested
    class 사용자_경매_상세_조회 {

        @Test
        void ConstantPolicy_경매_조회시() {
            // given
            Long auctionId = 1L;
            BuyerAuctionInfo response = BuyerAuctionInfoFixture.create()
                    .auctionId(auctionId)
                    .pricePolicy(PricePolicy.createConstantPricePolicy(10))
                    .build();
            given(auctionService.getBuyerAuction(auctionId)).willReturn(response);

            // when
            docsGiven.accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/auctions/{auctionId}", auctionId.toString())
                    .then().log().all()
                    .apply(MockMvcRestDocumentation.document("memberAuctions/findOneConstantPolicy/success",
                            RequestDocumentation.pathParameters(
                                    RequestDocumentation.parameterWithName("auctionId").description("상세 조회 할 경매의 ID")
                            ),
                            PayloadDocumentation.responseFields(
                                    PayloadDocumentation.fieldWithPath("auctionId").description("경매 ID"),
                                    PayloadDocumentation.fieldWithPath("sellerId").description("판매자 ID"),
                                    PayloadDocumentation.fieldWithPath("productName").description("상품 이름"),
                                    PayloadDocumentation.fieldWithPath("originPrice").description("상품 원가"),
                                    PayloadDocumentation.fieldWithPath("currentPrice").description("현재 가격"),
                                    PayloadDocumentation.fieldWithPath("originStock").description("원래 재고").attributes(
                                            getAttribute("format", "재고를 보여주지 않는다면 NULL이고 응답 값에서 제외됨")),
                                    PayloadDocumentation.fieldWithPath("currentStock").description("현재 재고").attributes(
                                            getAttribute("format", "재고를 보여주지 않는다면 NULL이고 응답 값에서 제외됨")),
                                    PayloadDocumentation.fieldWithPath("maximumPurchaseLimitCount")
                                            .description("최대 구매 수량 제한"),
                                    PayloadDocumentation.fieldWithPath("pricePolicy").description("가격 정책"),
                                    PayloadDocumentation.fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                    PayloadDocumentation.fieldWithPath("pricePolicy.variationWidth")
                                            .description("절대 변동 폭"),
                                    PayloadDocumentation.fieldWithPath("variationDuration").description("가격 변동 주기"),
                                    PayloadDocumentation.fieldWithPath("startedAt").description("경매 시작 시간"),
                                    PayloadDocumentation.fieldWithPath("finishedAt").description("경매 종료 시간")
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
                    .apply(MockMvcRestDocumentation.document("memberAuctions/findOnePercentagePolicy/success",
                            RequestDocumentation.pathParameters(
                                    RequestDocumentation.parameterWithName("auctionId").description("상세 조회 할 경매의 ID")
                            ),
                            PayloadDocumentation.responseFields(
                                    PayloadDocumentation.fieldWithPath("auctionId").description("경매 ID"),
                                    PayloadDocumentation.fieldWithPath("sellerId").description("판매자 ID"),
                                    PayloadDocumentation.fieldWithPath("productName").description("상품 이름"),
                                    PayloadDocumentation.fieldWithPath("originPrice").description("상품 원가"),
                                    PayloadDocumentation.fieldWithPath("currentPrice").description("현재 가격"),
                                    PayloadDocumentation.fieldWithPath("originStock").description("원래 재고").attributes(
                                            getAttribute("format", "재고를 보여주지 않는다면 NULL이고 응답 값에서 제외됨")),
                                    PayloadDocumentation.fieldWithPath("currentStock").description("현재 재고").attributes(
                                            getAttribute("format", "재고를 보여주지 않는다면 NULL이고 응답 값에서 제외됨")),
                                    PayloadDocumentation.fieldWithPath("maximumPurchaseLimitCount")
                                            .description("최대 구매 수량 제한"),
                                    PayloadDocumentation.fieldWithPath("pricePolicy").description("가격 정책"),
                                    PayloadDocumentation.fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                    PayloadDocumentation.fieldWithPath("pricePolicy.discountRate")
                                            .description("가격 변동 할인율"),
                                    PayloadDocumentation.fieldWithPath("variationDuration").description("가격 변동 주기"),
                                    PayloadDocumentation.fieldWithPath("startedAt").description("경매 시작 시간"),
                                    PayloadDocumentation.fieldWithPath("finishedAt").description("경매 종료 시간")
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
            PurchaseRequest purchaseRequest = new PurchaseRequest(10000L, 20L);
            SignInInfo buyerInfo = new SignInInfo(1L, Role.BUYER);
            willDoNothing().given(auctioneer)
                    .process(any());
            given(authenticationContext.getPrincipal()).willReturn(buyerInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.post("/auctions/{auctionId}/purchase", auctionId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", buyerInfo)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .content(objectMapper.writeValueAsString(purchaseRequest)))
                    .andDo(MockMvcRestDocumentation.document("buyerAuctions/purchase/success",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            CookieDocumentation.requestCookies(
                                    CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            RequestDocumentation.pathParameters(
                                    RequestDocumentation.parameterWithName("auctionId").description("입찰한 경매의 ID")
                            ),
                            PayloadDocumentation.requestFields(
                                    PayloadDocumentation.fieldWithPath("price").type(JsonFieldType.NUMBER)
                                            .description("입찰을 희망하는 가격"),
                                    PayloadDocumentation.fieldWithPath("quantity").type(JsonFieldType.NUMBER)
                                            .description("입찰을 희망하는 수량")
                            ),
                            PayloadDocumentation.responseFields(
                                    PayloadDocumentation.fieldWithPath("receiptId").type(JsonFieldType.STRING)
                                            .description("구매 완료된 영수증의 id값 (UUID)")
                            )
                    ))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class 구매자_경매_취소 {

        @Test
        void 성공시() throws Exception {
            UUID receiptId = UUID.randomUUID();
            SignInInfo signInInfo = new SignInInfo(1L, Role.BUYER);

            mockMvc.perform(RestDocumentationRequestBuilders.put("/receipts/{receiptId}/refund", receiptId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                    ).andDo(MockMvcRestDocumentation.document("buyerAuctions/cancel/success",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            CookieDocumentation.requestCookies(
                                    CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                            ),
                            RequestDocumentation.pathParameters(
                                    RequestDocumentation.parameterWithName("receiptId").description("환불할 경매의 영수증 ID")
                            )
                    ))
                    .andExpect(status().isOk());
        }
    }
}
