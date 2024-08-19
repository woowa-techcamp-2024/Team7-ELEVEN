package com.wootecam.luckyvickyauction.documentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wootecam.luckyvickyauction.core.member.domain.Role;
import com.wootecam.luckyvickyauction.core.member.dto.SignInInfo;
import com.wootecam.luckyvickyauction.core.payment.domain.BidStatus;
import com.wootecam.luckyvickyauction.core.payment.dto.BidHistoryInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.BuyerReceiptSimpleInfo;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSearchCondition;
import com.wootecam.luckyvickyauction.core.payment.dto.SellerReceiptSimpleInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReceiptDocument extends DocumentationTest {

    @Nested
    class 거래내역_상세_조회 {

        @Test
        void 거래내역_id에_해당하는_거래내역을_조회한다() throws Exception {
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
            SignInInfo memberInfo = new SignInInfo(1L, Role.SELLER);
            given(authenticationContext.getPrincipal()).willReturn(memberInfo);
            given(bidHistoryService.getBidHistoryInfo(memberInfo, 1L)).willReturn(bidHistoryInfo);

            mockMvc.perform(get("/receipts/{receiptId}", receiptId)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", memberInfo))
                    .andDo(
                            document("receipts/getReceipt/success",
                                    pathParameters(
                                            parameterWithName("receiptId").description("조회할 거래내역 ID")
                                    )
                            )
                    ).andExpect(status().isOk());
        }
    }

    @Nested
    class 거래_이력 {

        @Test
        void 구매자는_정상적으로_거래이력을_확인한다() throws Exception {
            BuyerReceiptSearchCondition condition = new BuyerReceiptSearchCondition(3);
            List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfos = buyerReceiptSimpleInfosSample();
            SignInInfo buyerInfo = new SignInInfo(1L, Role.BUYER);
            given(bidHistoryService.getBuyerReceiptSimpleInfos(buyerInfo, condition)).willReturn(
                    buyerReceiptSimpleInfos);
            given(authenticationContext.getPrincipal()).willReturn(buyerInfo);

            mockMvc.perform(get("/receipts/buyer")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", buyerInfo)
                            .content(objectMapper.writeValueAsString(condition)))
                    .andDo(
                            document("receipts/getReceipts/success",
                                    requestFields(
                                            fieldWithPath("size").type(JsonFieldType.NUMBER)
                                                    .description("조회할 거래 내역의 개수")
                                                    .attributes(key("constraints").value("최소:1 ~ 최대:100"))
                                    )
                            )
                    )
                    .andExpect(status().isOk());
        }

        private List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfosSample() {
            List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfos = new ArrayList<>();

            for (long i = 1; i <= 3; i++) {
                BuyerReceiptSimpleInfo receipt = BuyerReceiptSimpleInfo.builder()
                        .id(i)
                        .auctionId(i)
                        .price(i * 1000)
                        .productName("내가 구매한 상품" + i)
                        .quantity(i * 10)
                        .type(i % 2 == 1 ? BidStatus.BID : BidStatus.REFUND)
                        .build();
                buyerReceiptSimpleInfos.add(receipt);
            }

            return buyerReceiptSimpleInfos;
        }

        @Test
        void 판매자는_정상적으로_거래이력을_확인한다() throws Exception {
            SellerReceiptSearchCondition condition = new SellerReceiptSearchCondition(3);
            List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfos = sellerReceiptSimpleInfosSample();
            SignInInfo sellerInfo = new SignInInfo(1L, Role.SELLER);
            given(bidHistoryService.getSellerReceiptSimpleInfos(sellerInfo, condition)).willReturn(
                    sellerReceiptSimpleInfos);
            given(authenticationContext.getPrincipal()).willReturn(sellerInfo);

            mockMvc.perform(get("/receipts/seller")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .sessionAttr("signInMember", sellerInfo)
                            .content(objectMapper.writeValueAsString(condition)))
                    .andDo(document("receipts/getSellerReceipts/success",
                            requestFields(
                                    fieldWithPath("size").type(JsonFieldType.NUMBER)
                                            .description("조회할 거래 내역의 개수")
                                            .attributes(key("constraints").value("최소:1 ~ 최대:100"))
                            )
                    ))
                    .andExpect(status().isOk());
        }

        private List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfosSample() {
            List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfos = new ArrayList<>();

            for (long i = 1; i <= 3; i++) {
                SellerReceiptSimpleInfo receipt = SellerReceiptSimpleInfo.builder()
                        .id(i)
                        .auctionId(i)
                        .price(i * 1000)
                        .productName("내가 판매한 상품" + i)
                        .quantity(i * 10)
                        .type(i % 2 == 0 ? BidStatus.BID : BidStatus.REFUND)
                        .build();
                sellerReceiptSimpleInfos.add(receipt);
            }

            return sellerReceiptSimpleInfos;
        }
    }
}

