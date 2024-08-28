package com.wootecam.luckyvickyauction.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wootecam.luckyvickyauction.domain.entity.type.PercentagePricePolicy;
import com.wootecam.luckyvickyauction.domain.entity.type.PricePolicy;
import com.wootecam.luckyvickyauction.domain.entity.type.PricePolicyType;
import org.junit.jupiter.api.Test;

public class PricePolicyConverterTest {

    private PricePolicyConverter pricePolicyConverter = new PricePolicyConverter();

    @Test
    void 올바른_가격정책_객체를_JSON으로_변환한다() {
        // given
        PricePolicy pricePolicy = new PercentagePricePolicy(10L);

        // when
        String json = pricePolicyConverter.convertToDatabaseColumn(pricePolicy);

        // then
        assertThat(json).isEqualTo("{\"type\":\"PERCENTAGE\",\"discountRate\":10.0}");
    }

    @Test
    void 올바른_JSON을_가격정책_객체로_변환한다() {
        // given
        String json = "{\"type\":\"PERCENTAGE\",\"discountRate\":10.0}";

        // when
        PricePolicy pricePolicy = pricePolicyConverter.convertToEntityAttribute(json);

        // then
        assertAll(
                () -> assertThat(pricePolicy.getType()).isEqualTo(PricePolicyType.PERCENTAGE),
                () -> assertThat(((PercentagePricePolicy) pricePolicy).getDiscountRate()).isEqualTo(10.0)
        );
    }
}
