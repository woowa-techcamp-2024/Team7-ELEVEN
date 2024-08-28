package com.wootecam.luckyvickyauction.domain.entity.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(
        use = Id.NAME,
        include = As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PercentagePricePolicy.class, name = "PERCENTAGE"),
        @JsonSubTypes.Type(value = ConstantPricePolicy.class, name = "CONSTANT")
})
public interface PricePolicy {

    long calculatePriceAtVariation(long price, long variationCount);

    static PricePolicy createPercentagePricePolicy(int discountRate) {
        return new PercentagePricePolicy(discountRate);
    }

    static PricePolicy createConstantPricePolicy(int variationWidth) {
        return new ConstantPricePolicy(variationWidth);
    }

    PricePolicyType getType();
}
