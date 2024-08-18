package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.snippet.Attributes.key;

import com.wootecam.luckyvickyauction.core.auction.dto.AuctionSearchCondition;
import com.wootecam.luckyvickyauction.core.auction.dto.BuyerAuctionSimpleInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
     */
    // TODO: [판매자 테스트를 먼저 작성해버렸네용 ^^;; 해당 EndPoint 구현시 주석해제하고 활용!] [writeAt: 2024/08/18/15:10] [writeBy: chhs2131]
//    @Nested
//    class 판매자_경매_목록_조회 {
//
//        @Test
//        void 경매_조회_조건을_전달하면_성공적으로_경매_목록을_반환한다() {
//            AuctionSearchCondition condition = new AuctionSearchCondition(10, 2);
//            List<SellerAuctionSimpleInfo> infos = sellerAuctionSimpleInfosSample();
//            given(auctionService.getSellerAuctionSimpleInfos(any())).willReturn(infos);
//
//            docsGiven.contentType(MediaType.APPLICATION_JSON_VALUE)
//                    .body(condition)
//                    .when().get("/auctions/seller")
//                    .then().log().all()
//                    .apply(document("auctions/getSellerAuctions/success",
//                            requestFields(
//                                    fieldWithPath("offset").type(JsonFieldType.NUMBER)
//                                            .description("조회를 시작할 순서"),
//                                    fieldWithPath("size").type(JsonFieldType.NUMBER)
//                                            .description("조회할 페이지 크기")
//                                            .attributes(key("constraints").value("최소: 1 ~ 최대: 100"))
//                            )
//                    ))
//                    .statusCode(HttpStatus.OK.value());
//        }
//
//        private List<SellerAuctionSimpleInfo> sellerAuctionSimpleInfosSample() {
//            List<SellerAuctionSimpleInfo> infos = new ArrayList<>();
//
//            for (long i = 1; i <= 2; i++) {
//                SellerAuctionSimpleInfo simpleInfo = new SellerAuctionSimpleInfo(i, "쓸만한 경매품 " + i, i * 2000,
//                        i * 2000 - 500, i * 100, i * 30,
//                        ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(30L));
//                infos.add(simpleInfo);
//            }
//
//            return infos;
//        }
//    }

}
