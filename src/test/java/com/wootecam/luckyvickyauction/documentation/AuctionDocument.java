package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionInfo;
import com.wootecam.luckyvickyauction.core.auction.dto.SellerAuctionSimpleInfo;
import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class AuctionDocument extends DocumentationTest {

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

    /**
     * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/130">Github issue</a>
     * TODO: 현식햄 머지하면 진행할 것!
     */
    @Nested
    @Disabled
    class 판매자_경매_목록_조회 {

        @Test
        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() {
            AuctionSearchCondition condition = new AuctionSearchCondition(10, 2);
            List<SellerAuctionSimpleInfo> infos = sellerAuctionSimpleInfosSample();
            given(auctionService.getSellerAuctionSimpleInfos(any())).willReturn(infos);

            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(condition)
                    .when().get("/auctions/seller")
                    .then().log().all()
                    .apply(document("auctions/getSellerAuctions/success",
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
}
