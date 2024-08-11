package com.wootecam.luckyvickyauction.core.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.global.exception.BadRequestException;
import com.wootecam.luckyvickyauction.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void Role을_찾을_수_있다() {
        // given
        String seller = "SELLER";
        String buyer = "BUYER";

        // then
        assertAll(
                () -> assertThat(Role.find(seller)).isEqualTo(Role.SELLER),
                () -> assertThat(Role.find(buyer)).isEqualTo(Role.BUYER)
        );
    }

    @Test
    void Role을_찾을_수_없으면_예외가_발생한다() {
        // expect
        assertThatThrownBy(() -> Role.find("invalidValue"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("사용자의 역할을 찾을 수 없습니다. userRole = invalidValue")
                .satisfies(exception -> assertThat(exception).hasFieldOrPropertyWithValue("errorCode", ErrorCode.M001));
    }
}
