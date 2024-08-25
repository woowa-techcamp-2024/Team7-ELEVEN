package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.core.member.fixture.MemberFixture;
import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @Test
    void 회원을_생성할_수_있다() {
        // when
        Member member = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // then
        assertAll(
                () -> assertThat(member.getSignInId()).isEqualTo("testId"),
                () -> assertThat(member.getPassword()).isEqualTo("password00"),
                () -> assertThat(member.getRole()).isEqualTo(Role.BUYER),
                () -> assertThat(member.getPoint()).isEqualTo(new Point(100))
        );
    }

    @Nested
    class validatePassword_메소드는 {

        @ParameterizedTest
        @MethodSource("generateInvalidPassword")
        void 유효하지_않은_비밀번호면_예외가_발생한다(String expectedMessage, String password, ErrorCode expectedErrorCode) {
            // expect
            assertThatThrownBy(() -> Member.builder()
                    .signInId("testId")
                    .password(password)
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build())
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(expectedMessage)
                    .hasFieldOrPropertyWithValue("errorCode", expectedErrorCode);
        }

        private static Stream<Arguments> generateInvalidPassword() {
            return Stream.of(
                    Arguments.of("비밀번호는 빈칸 또는 공백일 수 없습니다.", "", ErrorCode.M006),
                    Arguments.of("비밀번호는 8자 이상 20자 이하로 입력해주세요. 현재 길이=3", "p00", ErrorCode.M007),
                    Arguments.of("비밀번호는 8자 이상 20자 이하로 입력해주세요. 현재 길이=26", "passwordpasswordpassword00", ErrorCode.M007),
                    Arguments.of("비밀번호는 숫자가 반드시 포함되어야 합니다.", "password", ErrorCode.M008),
                    Arguments.of("비밀번호는 알파벳 소문자가 반드시 포함되어야 합니다.", "PASSWORD00", ErrorCode.M009),
                    Arguments.of("비밀번호는 영문자와 숫자만 사용할 수 있습니다.", "password00!", ErrorCode.M010)
            );
        }
    }

    @Test
    void usePoint_포인트를_사용할_수_있다() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();
        long price = 10L;
        long quantity = 10L;

        // when
        buyer.usePoint(price * quantity);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(0));
    }

    @Test
    void usePoint_보유한_포인트보다_많은_포인트를_사용하려하면_예외가_발생한다() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // expect
        assertThatThrownBy(() -> buyer.usePoint(10 * 11))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("포인트가 부족합니다.")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.P001));
    }

    @Test
    void chargePoint_포인트를_충전할_수_있다() {
        // given
        Member buyer = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // when
        buyer.chargePoint(100);

        // then
        assertThat(buyer.getPoint()).isEqualTo(new Point(200));
    }

    @Nested
    class confirmPassword_메소드는 {

        @Test
        void 비밀번호가_동일하면_true를_반환한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.confirmPassword("password00")).isTrue();
        }

        @Test
        void 비밀번호가_다르면_false를_반환한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.confirmPassword("password1")).isFalse();
        }
    }

    @Nested
    class isBuyer_메소드는 {

        @Test
        void 구매자라면_true를_반환한다() {
            // given
            Member buyer = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.BUYER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(buyer.isBuyer()).isTrue();
        }

        @Test
        void 판매자라면_false를_반환한다() {
            // given
            Member seller = Member.builder()
                    .signInId("testId")
                    .password("password00")
                    .role(Role.SELLER)
                    .point(new Point(100))
                    .build();

            // then
            assertThat(seller.isBuyer()).isFalse();
        }
    }

    @Test
    void 동일한_사용자인지_확인할_수_있다() {
        // given
        Member member = Member.builder()
                .signInId("testId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(100))
                .build();

        // when
        boolean isSameMember = member.isSameMember("testId");

        // then
        assertThat(isSameMember).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    "})
    void 아이디가_빈칸인_경우_예외가_발생한다(String userId) {
        // expect
        assertThatThrownBy(() ->
                Member.builder()
                        .signInId(userId)
                        .password("password1234")
                        .role(Role.BUYER)
                        .point(new Point(100))
                        .build())
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M004);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "21ccccc21212121212121"})
    void 아이디_길이가_정책과_맞지않으면_예외가_발생한다(String userId) {
        // expect
        assertThatThrownBy(() ->
                Member.builder()
                        .signInId(userId)
                        .password("password1234")
                        .role(Role.BUYER)
                        .point(new Point(100))
                        .build())
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.M005);
    }

    @Nested
    class pointTransfer_메소드는 {

        @Test
        void 다른_사용자에게_포인트를_송금할_수_있다() {
            // given
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();
            Member seller = MemberFixture.createSellerWithDefaultPoint();

            // when
            buyer.pointTransfer(seller, 500);

            // then
            assertThat(buyer.getPoint()).isEqualTo(new Point(500));
            assertThat(seller.getPoint()).isEqualTo(new Point(1500));
        }

        @Test
        void 포인트가_부족한_경우_예외가_발생한다() {
            // given
            Member buyer = MemberFixture.createBuyerWithDefaultPoint();

            // expect
            assertThatThrownBy(() -> buyer.pointTransfer(buyer, 1234567890))
                    .isInstanceOf(BadRequestException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.P001);
        }

    }

}
