package com.wootecam.luckyvickyauction.documentation;

import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.snippet.Attributes;

public class DocumentFormatGenerator {

    public static Attributes.Attribute getConstraints(final String key, final String value) {
        return key(key).value(value);
    }
}
