package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.core.auction.controller.dto.CreateAuctionRequest;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PercentagePricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Member;
import com.wootecam.luckyvickyauction.core.member.domain.Point;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class SellerAuctionDocument extends DocumentationTest {

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
            ZonedDateTime now = ZonedDateTime.now();
            CreateAuctionRequest request = new CreateAuctionRequest(
                    "productName", 10000L, 100L, 10L, new ConstantPricePolicy(100L), Duration.ofMinutes(10),
                    now.plusHours(1), now.plusHours(2), true);
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(signInInfo);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    ZonedDateTime.now());

            // expect
            mockMvc.perform(post("/auctions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(
                            document("auctions/post/constant_policy/success",
                                    requestFields(
                                            fieldWithPath("productName").description("상품 이름"),
                                            fieldWithPath("originPrice").description("상품 원가"),
                                            fieldWithPath("stock").description("상품 재고"),
                                            fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                            fieldWithPath("pricePolicy").description("가격 정책"),
                                            fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                            fieldWithPath("pricePolicy.variationWidth").description(
                                                    "절대 가격 정책시 가격 절대 변동폭"),
                                            fieldWithPath("variationDuration").description("경매 기간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부"),
                                            fieldWithPath("startedAt").description("경매 시작 시간"),
                                            fieldWithPath("finishedAt").description("경매 종료 시간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부")
                                    )
                            )
                    ).andExpect(status().isOk());
        }

        @Test
        void PercentagePolicy_경매_생성() throws Exception {
            // given
            ZonedDateTime now = ZonedDateTime.now();
            CreateAuctionRequest request = new CreateAuctionRequest(
                    "productName", 10000L, 100L, 10L, new PercentagePricePolicy(10.0), Duration.ofMinutes(10),
                    now.plusHours(1), now.plusHours(2), true);
            SignInInfo signInInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(signInInfo);
            given(currentTimeArgumentResolver.supportsParameter(any())).willReturn(true);
            given(currentTimeArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
                    ZonedDateTime.now());

            // expect
            mockMvc.perform(post("/auctions")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(
                            document("auctions/post/percentage_policy/success",
                                    requestFields(
                                            fieldWithPath("productName").description("상품 이름"),
                                            fieldWithPath("originPrice").description("상품 원가"),
                                            fieldWithPath("stock").description("상품 재고"),
                                            fieldWithPath("maximumPurchaseLimitCount").description("최대 구매 제한 수량"),
                                            fieldWithPath("pricePolicy").description("가격 정책"),
                                            fieldWithPath("pricePolicy.type").description("가격 정책 타입"),
                                            fieldWithPath("pricePolicy.discountRate").description("퍼센트 가격 정책시 가격 할인율"),
                                            fieldWithPath("variationDuration").description("경매 기간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부"),
                                            fieldWithPath("startedAt").description("경매 시작 시간"),
                                            fieldWithPath("finishedAt").description("경매 종료 시간"),
                                            fieldWithPath("isShowStock").description("재고 노출 여부")
                                    )
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

            mockMvc.perform(get("/auctions/seller")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", sellerInfo)
                            .content(objectMapper.writeValueAsString(condition)))
                    .andDo(document("auctions/getSellerAuctions/success",
                            requestFields(
                                    fieldWithPath("offset").type(JsonFieldType.NUMBER)
                                            .description("조회를 시작할 순서"),
                                    fieldWithPath("size").type(JsonFieldType.NUMBER)
                                            .description("조회할 페이지 크기")
                                            .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
                            )
                    ))
                    .andExpect(status().isOk());
        }

        private List<SellerAuctionSimpleInfo> sellerAuctionSimpleInfosSample() {
            List<SellerAuctionSimpleInfo> infos = new ArrayList<>();

            for (long i = 1; i <= 2; i++) {
                SellerAuctionSimpleInfo simpleInfo = new SellerAuctionSimpleInfo(i, "내가 판매하는 경매품 " + i, i * 2000,
                        i * 2000 - 500, i * 100, i * 30,
                        ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(30L));
                infos.add(simpleInfo);
            }

            return infos;
        }
    }

    @Nested
    class 판매자_경매_상세_조회 {

        @Test
        void 경매_id를_전달하면_성공적으로_경매_상세정보를_반환한다() throws Exception {
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
                    .startedAt(ZonedDateTime.now())
                    .finishedAt(ZonedDateTime.now().plusHours(1))
                    .isShowStock(true)
                    .build();
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(auctionService.getSellerAuction(sellerInfo, 1L)).willReturn(sellerAuctionInfo);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(get("/auctions/{auctionId}/seller", auctionId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", sellerInfo))
                    .andDo(
                            document("auctions/getSellerAuctionDetail/success",
                                    pathParameters(
                                            parameterWithName("auctionId").description("조회할 경매 ID")
                                    )
                            )
                    )
                    .andExpect(status().isOk());
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
                    ZonedDateTime.now());

            mockMvc.perform(
                    delete("/auctions/{auctionId}", auctionId)
                            .cookie(new Cookie("JSESSIONID", "sessionId"))
                            .sessionAttr("signInMember", signInInfo)
            ).andDo(
                    document("auctions/delete/success",
                            requestCookies(cookieWithName("JSESSIONID").description("세션 ID")),
                            pathParameters(parameterWithName("auctionId").description("취소할 경매 ID"))
                    )
            ).andExpect(status().isOk());
        }
    }
}
