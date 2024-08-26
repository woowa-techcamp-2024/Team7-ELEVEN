package com.wootecam.test.fixture;

import com.wootecam.core.domain.Member;
import com.wootecam.core.domain.Point;
import com.wootecam.core.domain.type.Role;

public class MemberFixture {

    private Long id = 1L;
    private String signInId = "buyerId";
    private String password = "password00";
    private Role role = Role.BUYER;
    private Point point = new Point(1000L);

    private MemberFixture() {
    }

    public static Member createBuyerWithDefaultPoint() {
        return Member.builder()
                .signInId("buyerId")
                .password("password00")
                .role(Role.BUYER)
                .point(new Point(1000L))
                .build();
    }

    public static Member createSellerWithDefaultPoint() {
        return Member.builder()
                .signInId("sellerId")
                .password("password00")
                .role(Role.SELLER)
                .point(new Point(1000L))
                .build();
    }

    /**
     * 아무런 설정을 하지 않는다면 구매자 역할의 회원이 생성된다.
     */
    public static MemberFixture create() {
        return new MemberFixture();
    }

    public MemberFixture id(Long id) {
        this.id = id;
        return this;
    }

    public MemberFixture signInId(String signInId) {
        this.signInId = signInId;
        return this;
    }

    public MemberFixture password(String password) {
        this.password = password;
        return this;
    }

    public MemberFixture role(Role role) {
        this.role = role;
        return this;
    }

    public MemberFixture point(Point point) {
        this.point = point;
        return this;
    }

    public Member build() {
        return new Member(id, signInId, password, role, point);
    }
}
