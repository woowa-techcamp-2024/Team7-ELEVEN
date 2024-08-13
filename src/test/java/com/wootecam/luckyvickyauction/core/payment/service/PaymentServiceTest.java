package com.wootecam.luckyvickyauction.core.payment.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

// TODO: AuctionService 세부 사항 결정되면 테스트
class PaymentServiceTest {

    @Nested
    class process_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 입찰이_진행된다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_사용자가_구매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_판매자를_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_물건의_금액이_사용자가_가진_포인트보다_많다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }
    }

    @Nested
    class refund_메소드는 {

        @Nested
        class 정상적인_요청_흐름이면 {

            @Test
            void 환불이_진행된다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_요청한_사용자가_구매자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_환불할_입찰_내역을_찾을_수_없다면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_이미_환불된_입찰_내역이라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }

        @Nested
        class 만약_입찰_내역의_구매자가_요청한_사용자가_아니라면 {

            @Test
            void 예외가_발생한다() {
                // given
                // when
                // then
            }
        }
    }
}
