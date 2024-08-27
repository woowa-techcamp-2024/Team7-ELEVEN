package com.wootecam.api.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.api.dto.CreateAuctionRequest;
import com.wootecam.core.domain.entity.Member;
import com.wootecam.core.domain.entity.Point;
import com.wootecam.core.domain.entity.type.ConstantPricePolicy;
import com.wootecam.core.domain.entity.type.PercentagePricePolicy;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.dto.auction.condition.AuctionSearchCondition;
import com.wootecam.core.dto.auction.info.SellerAuctionInfo;
import com.wootecam.core.dto.auction.info.SellerAuctionSimpleInfo;
import com.wootecam.core.dto.member.info.SignInInfo;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.CookieDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;

class SellerAuctionDocument extends DocumentationTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/198">Github issue</a>
     */
    @Nested
    class 판매자_경매_등록 {

        @Test
        void ConstantPolicy_경매_생성() throws Exception {
            // given
            LocalDateTime now = LocalDateTime.now();
            CreateAuctionRequest request = new CreateAuctionRequest(
                    "productName", 10000L, 100L, 10L, new ConstantPricePolicy(100L), Duration.ofMinutes(10),
                    now.plusHours(1), now.plusHours(2), true);
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(signInInfo);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    LocalDateTime.now());

            // expect
            mockMvc.perform(post("/auctions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(
                            MockMvcRestDocumentation.document("sellerAuctions/createConstantPolicy/success",
                                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                    CookieDocumentation.requestCookies(
                                            CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                                    ),
                                    PayloadDocumentation.requestFields(
                                            PayloadDocumentation.fieldWithPath("productName").description("상품 이름"),
                                            PayloadDocumentation.fieldWithPath("originPrice").description("상품 원가"),
                                            PayloadDocumentation.fieldWithPath("stock").description("상품 재고"),
                                            PayloadDocumentation.fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy").description("가격 정책"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.variationWidth").description(
                                                    "절대 가격 정책시 가격 절대 변동폭"),
                                            PayloadDocumentation.fieldWithPath("variationDuration").description("경매 기간"),
                                            PayloadDocumentation.fieldWithPath("isShowStock").description("재고 노출 여부"),
                                            PayloadDocumentation.fieldWithPath("startedAt").description("경매 시작 시간"),
                                            PayloadDocumentation.fieldWithPath("finishedAt").description("경매 종료 시간"),
                                            PayloadDocumentation.fieldWithPath("isShowStock").description("재고 노출 여부")
                                    )
                            )
                    ).andExpect(status().isOk());
        }

        @Test
        void PercentagePolicy_경매_생성() throws Exception {
            // given
            LocalDateTime now = LocalDateTime.now();
            CreateAuctionRequest request = new CreateAuctionRequest(
                    "productName", 10000L, 100L, 10L, new PercentagePricePolicy(10.0), Duration.ofMinutes(10),
                    now.plusHours(1), now.plusHours(2), true);
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(signInInfo);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    LocalDateTime.now());

            // expect
            mockMvc.perform(post("/auctions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(
                            MockMvcRestDocumentation.document("sellerAuctions/createPercentagePolicy/success",
                                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                    CookieDocumentation.requestCookies(
                                            CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")
                                    ),
                                    PayloadDocumentation.requestFields(
                                            PayloadDocumentation.fieldWithPath("productName").description("상품 이름"),
                                            PayloadDocumentation.fieldWithPath("originPrice").description("상품 원가"),
                                            PayloadDocumentation.fieldWithPath("stock").description("상품 재고"),
                                            PayloadDocumentation.fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy").description("가격 정책"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.discountRate").description("퍼센트 가격 정책시 가격 할인율"),
                                            PayloadDocumentation.fieldWithPath("variationDuration").description("경매 기간"),
                                            PayloadDocumentation.fieldWithPath("isShowStock").description("재고 노출 여부"),
                                            PayloadDocumentation.fieldWithPath("startedAt").description("경매 시작 시간"),
                                            PayloadDocumentation.fieldWithPath("finishedAt").description("경매 종료 시간"),
                                            PayloadDocumentation.fieldWithPath("isShowStock").description("재고 노출 여부")
                                    )
                            )
                    ).andExpect(status().isOk());
        }
    }

    @Nested
    class 판매자_경매_등록_취소_API {

        @Test
        void 판매자_경매_취소() throws Exception {
            // given
            Long auctionId = 1L;
            Member seller = Member.builder()
                    .id(1L)
                    .signInId("sellerId")
                    .password("password00")
                    .role(Role.SELLER)
                    .point(new Point(1000L))
                    .build();
            SignInInfo signInInfo = new SignInInfo(seller.getId(), Role.SELLER);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    LocalDateTime.now());

            mockMvc.perform(RestDocumentationRequestBuilders.delete("/auctions/{auctionId}", auctionId)
                    .cookie(new Cookie("JSESSIONID", "sessionId"))
                    .sessionAttr("signInMember", signInInfo)
            ).andDo(
                    MockMvcRestDocumentation.document("sellerAuctions/delete/success",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            CookieDocumentation.requestCookies(CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")),
                            RequestDocumentation.pathParameters(RequestDocumentation.parameterWithName("auctionId").description("취소할 경매 ID"))
                    )
            ).andExpect(status().isOk());
        }
    }

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/130">Github issue</a>
     */
    @Nested
    class 판매자_경매_목록_조회 {

        @Test
        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() throws Exception {
            AuctionSearchCondition condition = new AuctionSearchCondition(10, 2);
            List<SellerAuctionSimpleInfo> infos = sellerAuctionSimpleInfosSample();
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(auctionService.getSellerAuctionSimpleInfos(any())).willReturn(infos);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/auctions/seller")
                            .queryParam("offset", "10")
                            .queryParam("size", "2")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(MockMvcRestDocumentation.document("sellerAuctions/findAll/success",
                            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                            CookieDocumentation.requestCookies(CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")),
                            RequestDocumentation.queryParameters(
                                    RequestDocumentation.parameterWithName("offset").description("조회를 시작할 순서"),
                                    RequestDocumentation.parameterWithName("size").description("조회할 페이지 크기")
                                            .attributes(Attributes.key("constraints").value("최소: 1 ~ 최대: 100"))
                            ),
                            PayloadDocumentation.responseFields(
                                    PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.NUMBER)
                                            .description("경매 ID"),
                                    PayloadDocumentation.fieldWithPath("[].title").type(JsonFieldType.STRING)
                                            .description("경매 노출 제목"),
                                    PayloadDocumentation.fieldWithPath("[].originPrice").type(JsonFieldType.NUMBER)
                                            .description("상품 원가"),
                                    PayloadDocumentation.fieldWithPath("[].currentPrice").type(JsonFieldType.NUMBER)
                                            .description("현재 가격"),
                                    PayloadDocumentation.fieldWithPath("[].totalStock").type(JsonFieldType.NUMBER)
                                            .description("총 재고"),
                                    PayloadDocumentation.fieldWithPath("[].currentStock").type(JsonFieldType.NUMBER)
                                            .description("현재 남은 재고"),
                                    PayloadDocumentation.fieldWithPath("[].startedAt").type(JsonFieldType.STRING)
                                            .description("경매 시작 시간"),
                                    PayloadDocumentation.fieldWithPath("[].finishedAt").type(JsonFieldType.STRING)
                                            .description("경매 종료 시간")
                            )
                    ))
                    .andExpect(status().isOk());
        }

        private List<SellerAuctionSimpleInfo> sellerAuctionSimpleInfosSample() {
            List<SellerAuctionSimpleInfo> infos = new ArrayList<>();

            for (long i = 1; i <= 2; i++) {
                SellerAuctionSimpleInfo simpleInfo = new SellerAuctionSimpleInfo(i, "내가 판매하는 경매품 " + i, i * 2000,
                        i * 2000 - 500, i * 100, i * 30,
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(30L));
                infos.add(simpleInfo);
            }

            return infos;
        }
    }

    @Nested
    class 판매자_경매_상세_조회 {

        @Test
        void 경매_id를_전달하면_성공적으로_고정_할인_경매_상세정보를_반환한다() throws Exception {
            String auctionId = "1";
            SellerAuctionInfo sellerAuctionInfo = SellerAuctionInfo.builder()
                    .auctionId(1L)
                    .productName("쓸만한 경매품")
                    .originPrice(10000)
                    .currentPrice(8000)
                    .originStock(100)
                    .currentStock(50)
                    .maximumPurchaseLimitCount(10)
                    .pricePolicy(new ConstantPricePolicy(10L))
                    .variationDuration(Duration.ofMinutes(10))
                    .startedAt(LocalDateTime.now())
                    .finishedAt(LocalDateTime.now().plusHours(1))
                    .isShowStock(true)
                    .build();
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(auctionService.getSellerAuction(sellerInfo, 1L)).willReturn(sellerAuctionInfo);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/auctions/{auctionId}/seller", auctionId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(
                            MockMvcRestDocumentation.document("sellerAuctions/findOneConstantPolicy/success",
                                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                    CookieDocumentation.requestCookies(CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")),
                                    RequestDocumentation.pathParameters(
                                            RequestDocumentation.parameterWithName("auctionId").description("조회할 경매 ID")
                                    ),
                                    PayloadDocumentation.responseFields(
                                            PayloadDocumentation.fieldWithPath("auctionId").type(JsonFieldType.NUMBER)
                                                    .description("경매 ID"),
                                            PayloadDocumentation.fieldWithPath("productName").type(JsonFieldType.STRING)
                                                    .description("상품 이름"),
                                            PayloadDocumentation.fieldWithPath("originPrice").type(JsonFieldType.NUMBER)
                                                    .description("상품 원가"),
                                            PayloadDocumentation.fieldWithPath("currentPrice").type(JsonFieldType.NUMBER)
                                                    .description("현재 가격"),
                                            PayloadDocumentation.fieldWithPath("originStock").type(JsonFieldType.NUMBER)
                                                    .description("원래 재고"),
                                            PayloadDocumentation.fieldWithPath("currentStock").type(JsonFieldType.NUMBER)
                                                    .description("현재 재고"),
                                            PayloadDocumentation.fieldWithPath("maximumPurchaseLimitCount").type(JsonFieldType.NUMBER)
                                                    .description("최대 구매 수량 제한"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.type").type(JsonFieldType.STRING)
                                                    .description("가격 정책 유형"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.variationWidth").type(JsonFieldType.NUMBER)
                                                    .description("가격 변동 폭(고정 할인 정책)"),
                                            PayloadDocumentation.fieldWithPath("variationDuration").type(JsonFieldType.STRING)
                                                    .description("가격 변동 주기(분)"),
                                            PayloadDocumentation.fieldWithPath("startedAt").type(JsonFieldType.STRING)
                                                    .description("경매 시작 시간"),
                                            PayloadDocumentation.fieldWithPath("finishedAt").type(JsonFieldType.STRING)
                                                    .description("경매 종료 시간"),
                                            PayloadDocumentation.fieldWithPath("isShowStock").type(JsonFieldType.BOOLEAN)
                                                    .description("재고 표시 여부")
                                    )
                            )
                    )
                    .andExpect(status().isOk());
        }

        @Test
        void 경매_id를_전달하면_성공적으로_퍼센트_할인_경매_상세정보를_반환한다() throws Exception {
            String auctionId = "1";
            SellerAuctionInfo sellerAuctionInfo = SellerAuctionInfo.builder()
                    .auctionId(1L)
                    .productName("쓸만한 경매품")
                    .originPrice(10000)
                    .currentPrice(8000)
                    .originStock(100)
                    .currentStock(50)
                    .maximumPurchaseLimitCount(10)
                    .pricePolicy(new PercentagePricePolicy(10.0))
                    .variationDuration(Duration.ofMinutes(10))
                    .startedAt(LocalDateTime.now())
                    .finishedAt(LocalDateTime.now().plusHours(1))
                    .isShowStock(true)
                    .build();
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(auctionService.getSellerAuction(sellerInfo, 1L)).willReturn(sellerAuctionInfo);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(RestDocumentationRequestBuilders.get("/auctions/{auctionId}/seller", auctionId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(
                            MockMvcRestDocumentation.document("sellerAuctions/findOnePercentagePolicy/success",
                                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                                    CookieDocumentation.requestCookies(CookieDocumentation.cookieWithName("JSESSIONID").description("세션 ID")),
                                    RequestDocumentation.pathParameters(
                                            RequestDocumentation.parameterWithName("auctionId").description("조회할 경매 ID")
                                    ),
                                    PayloadDocumentation.responseFields(
                                            PayloadDocumentation.fieldWithPath("auctionId").type(JsonFieldType.NUMBER)
                                                    .description("경매 ID"),
                                            PayloadDocumentation.fieldWithPath("productName").type(JsonFieldType.STRING)
                                                    .description("상품 이름"),
                                            PayloadDocumentation.fieldWithPath("originPrice").type(JsonFieldType.NUMBER)
                                                    .description("상품 원가"),
                                            PayloadDocumentation.fieldWithPath("currentPrice").type(JsonFieldType.NUMBER)
                                                    .description("현재 가격"),
                                            PayloadDocumentation.fieldWithPath("originStock").type(JsonFieldType.NUMBER)
                                                    .description("원래 재고"),
                                            PayloadDocumentation.fieldWithPath("currentStock").type(JsonFieldType.NUMBER)
                                                    .description("현재 재고"),
                                            PayloadDocumentation.fieldWithPath("maximumPurchaseLimitCount").type(JsonFieldType.NUMBER)
                                                    .description("최대 구매 수량 제한"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.type").type(JsonFieldType.STRING)
                                                    .description("가격 정책 유형"),
                                            PayloadDocumentation.fieldWithPath("pricePolicy.discountRate").type(JsonFieldType.NUMBER)
                                                    .description("가격 변동 폭(퍼센트 할인 정책)"),
                                            PayloadDocumentation.fieldWithPath("variationDuration").type(JsonFieldType.STRING)
                                                    .description("가격 변동 주기(분)"),
                                            PayloadDocumentation.fieldWithPath("startedAt").type(JsonFieldType.STRING)
                                                    .description("경매 시작 시간"),
                                            PayloadDocumentation.fieldWithPath("finishedAt").type(JsonFieldType.STRING)
                                                    .description("경매 종료 시간"),
                                            PayloadDocumentation.fieldWithPath("isShowStock").type(JsonFieldType.BOOLEAN)
                                                    .description("재고 표시 여부")
                                    )
                            )
                    )
                    .andExpect(status().isOk());
        }
    }
}
