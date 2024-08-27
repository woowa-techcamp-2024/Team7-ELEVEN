package com.wootecam.core.service.receipt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.core.domain.entity.Member;
import com.wootecam.core.domain.entity.Receipt;
import com.wootecam.core.domain.entity.type.ReceiptStatus;
import com.wootecam.core.domain.entity.type.Role;
import com.wootecam.core.dto.member.info.SignInInfo;
import com.wootecam.core.dto.receipt.condition.BuyerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.condition.SellerReceiptSearchCondition;
import com.wootecam.core.dto.receipt.info.BuyerReceiptSimpleInfo;
import com.wootecam.core.dto.receipt.info.ReceiptInfo;
import com.wootecam.core.dto.receipt.info.SellerReceiptSimpleInfo;
import com.wootecam.core.exception.AuthorizationException;
import com.wootecam.core.exception.ErrorCode;
import com.wootecam.core.exception.NotFoundException;
import com.wootecam.test.context.ServiceTest;
import com.wootecam.test.fixture.MemberFixture;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReceiptServiceTest extends ServiceTest {

    @Nested
    class getReceiptInfo_메서드는 {

        static Stream<Arguments> provideMembersForSuccess() {
            Member seller = MemberFixture.createSellerWithDefaultPoint();  // 소유자
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();  // 소유자

            return Stream.of(
                    Arguments.of(seller.getId(), "판매자의 구매이력 조회"),
                    Arguments.of(buyer.getId(), "구매자의 구매이력 조회")
            );
        }

        @ParameterizedTest(name = "{1} 시 성공한다")
        @MethodSource("provideMembersForSuccess")
        void 소유자가_거래내역_조회시_성공한다(Long memberId, String description) {
            // given
            Member buyer = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
            Member seller = memberRepository.save(MemberFixture.createSellerWithDefaultPoint());

            Receipt receipt = Receipt.builder()
                    .id(UUID.randomUUID())
                    .productName("멋진 상품")
                    .price(1000000)
                    .quantity(1)
                    .receiptStatus(ReceiptStatus.PURCHASED)
                    .auctionId(1L)
                    .sellerId(seller.getId())
                    .buyerId(buyer.getId())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            Receipt savedReceipt = receiptRepository.save(receipt);

            // when
            ReceiptInfo receiptInfo = receiptService.getReceiptInfo(
                    new SignInInfo(seller.getId(), Role.SELLER), savedReceipt.getId());

            // then
            assertAll(
                    () -> assertThat(receiptInfo.auctionId()).isEqualTo(1L),
                    () -> assertThat(receiptInfo.productName()).isEqualTo("멋진 상품"),
                    () -> assertThat(receiptInfo.price()).isEqualTo(1000000),
                    () -> assertThat(receiptInfo.quantity()).isEqualTo(1),
                    () -> assertThat(receiptInfo.receiptStatus()).isEqualTo(ReceiptStatus.PURCHASED),
                    () -> assertThat(receiptInfo.auctionId()).isEqualTo(1L),
                    () -> assertThat(receiptInfo.sellerId()).isEqualTo(seller.getId()),
                    () -> assertThat(receiptInfo.buyerId()).isEqualTo(buyer.getId())
            );
        }

        @Test
        void 존재하지않는_거래내역을_조회할때_예외가_발생한다() {
            // given
            Member member = memberRepository.save(MemberFixture.createBuyerWithDefaultPoint());
            UUID receiptId = UUID.randomUUID();

            // expect
            assertThatThrownBy(
                    () -> receiptService.getReceiptInfo(new SignInInfo(member.getId(), Role.BUYER), receiptId))
                    .isInstanceOf(NotFoundException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.R000));
        }

        @Test
        void 해당_거래내역의_소유자가_아닌경우_예외가_발생한다() {
            // given
            Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
                    .build();  // 소유자
            Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
                    .build();  // 소유자
            SignInInfo nonOwner = new SignInInfo(3L, Role.BUYER);

            Receipt receipt = Receipt.builder()
                    .sellerId(seller.getId())
                    .buyerId(buyer.getId())
                    .build();
            Receipt savedReceipt = receiptRepository.save(receipt);

            // expect
            assertThatThrownBy(() -> receiptService.getReceiptInfo(nonOwner, savedReceipt.getId()))
                    .isInstanceOf(AuthorizationException.class)
                    .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode",
                            ErrorCode.R001));
        }
    }

    @Nested
    class getBuyerReceiptSimpleInfos_메소드는 {

        @Nested
        class 정상적인_요청이_들어오면 {

            @Test
            void 특정_구매자의_거래이력을_조회할_수_있다() {
                // given
                Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
                        .build();
                Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
                        .build();

                Receipt receipt = Receipt.builder()
                        .id(UUID.randomUUID())
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .build();
                receiptRepository.save(receipt);

                // when
                List<BuyerReceiptSimpleInfo> buyerReceiptSimpleInfos = receiptService.getBuyerReceiptSimpleInfos(
                        new SignInInfo(buyer.getId(), Role.BUYER),
                        new BuyerReceiptSearchCondition(0, 5)
                );

                // then
                assertAll(
                        () -> assertThat(buyerReceiptSimpleInfos).hasSize(1)
                );
            }
        }
    }

    @Nested
    class getSellerReceiptSimpleInfos_메소드는 {

        @Nested
        class 정상적인_요청이_들어오면 {

            @Test
            void 특정_구매자의_거래이력을_조회할_수_있다() {
                // given
                Member seller = Member.builder().id(1L).signInId("판매자").password("password00").role(Role.SELLER)
                        .build();
                Member buyer = Member.builder().id(2L).signInId("구매자").password("password00").role(Role.BUYER)
                        .build();

                Receipt receipt = Receipt.builder()
                        .id(UUID.randomUUID())
                        .sellerId(seller.getId())
                        .buyerId(buyer.getId())
                        .build();
                receiptRepository.save(receipt);

                // when
                List<SellerReceiptSimpleInfo> sellerReceiptSimpleInfos = receiptService.getSellerReceiptSimpleInfos(
                        new SignInInfo(seller.getId(), Role.SELLER),
                        new SellerReceiptSearchCondition(0, 5)
                );

                // then
                assertAll(
                        () -> assertThat(sellerReceiptSimpleInfos).hasSize(1)
                );
            }
        }
    }
}
