package com.wootecam.core.dto.auction.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wootecam.core.exception.BadRequestException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AuctionSearchConditionTest {

    @Nested
    class 구매자가_경매목록을_조회할때 {

        /**
         * @see <a href="https://github.com/woowa-techcamp-2024/Team7-ELEVEN/issues/88">GitHub issue</a>
         */
        @ParameterizedTest
        @ValueSource(ints = {0, 101})
        void size가_1보다_작거나_100보다_크면_예외가_발생한다(int size) {
            assertThrows(BadRequestException.class, () -> new AuctionSearchCondition(0, size));
        }
    }

}