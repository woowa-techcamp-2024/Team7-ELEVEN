package com.wootecam.api.documentation;

import org.springframework.restdocs.snippet.Attributes;

public class DocumentFormatGenerator {

    public static Attributes.Attribute getAttribute(final String key, final String value) {
        return Attributes.key(key).value(value);
    }
}
