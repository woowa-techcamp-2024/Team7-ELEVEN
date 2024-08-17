package com.wootecam.luckyvickyauction.core.auction.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.luckyvickyauction.core.auction.domain.ConstantPricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PercentagePricePolicy;
import com.wootecam.luckyvickyauction.core.auction.domain.PricePolicy;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;

@Converter
public class PricePolicyConverter implements AttributeConverter<PricePolicy, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PricePolicy pricePolicy) {
        if (pricePolicy == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(PricePolicy.class);
        } catch (IOException e) {
            throw new RuntimeException("해당 객체를 String으로 변환할 수 없습니다.", e);
        }
    }

    @Override
    public PricePolicy convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(dbData);
            String type = jsonNode.get("type").asText();

            switch (type) {
                case "PERCENTAGE":
                    return objectMapper.treeToValue(jsonNode, PercentagePricePolicy.class);
                case "CONSTANT":
                    return objectMapper.treeToValue(jsonNode, ConstantPricePolicy.class);
                default:
                    throw new IllegalArgumentException("해당 type으로 변환할 수 없습니다. 현재 type=" + type);
            }
        } catch (IOException e) {
            throw new RuntimeException("해당 JSON을 PricePolicy 객체로 변환하는 데 실패했습니다.", e);
        }
    }
}
