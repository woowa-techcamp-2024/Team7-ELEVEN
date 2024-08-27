package com.wootecam.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wootecam.core.domain.entity.type.ConstantPricePolicy;
import com.wootecam.core.domain.entity.type.PercentagePricePolicy;
import com.wootecam.core.domain.entity.type.PricePolicy;
import com.wootecam.core.domain.entity.type.PricePolicyType;
import com.wootecam.core.exception.BadRequestException;
import com.wootecam.core.exception.ErrorCode;
import com.wootecam.core.exception.InfraStructureException;
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
            return objectMapper.writeValueAsString(pricePolicy);
        } catch (IOException e) {
            throw new InfraStructureException("해당 객체를 String으로 변환할 수 없습니다.", ErrorCode.A023);
        }
    }

    @Override
    public PricePolicy convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(dbData);
            PricePolicyType type = PricePolicyType.valueOf(jsonNode.get("type").asText());

            return switch (type) {
                case PERCENTAGE -> objectMapper.treeToValue(jsonNode, PercentagePricePolicy.class);
                case CONSTANT -> objectMapper.treeToValue(jsonNode, ConstantPricePolicy.class);
                default -> throw new BadRequestException("해당 type으로 변환할 수 없습니다. 현재 type=" + type, ErrorCode.A024);
            };
        } catch (IOException e) {
            throw new InfraStructureException("해당 JSON을 PricePolicy 객체로 변환하는 데 실패했습니다." + e, ErrorCode.A025);
        }
    }
}
