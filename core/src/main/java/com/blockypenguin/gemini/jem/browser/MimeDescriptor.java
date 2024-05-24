package com.blockypenguin.gemini.jem.browser;

import java.util.Optional;

public record MimeDescriptor(String type, Optional<String> param) {
    public static MimeDescriptor create(String str) {
        int mimeTypeEndIndex = str.indexOf(";");
        if(mimeTypeEndIndex == -1) mimeTypeEndIndex = str.length();

        return new MimeDescriptor(
            str.substring(0, mimeTypeEndIndex).trim(),
            Optional.of(str.substring(mimeTypeEndIndex).trim())
        );
    }
    
    @Override
    public String toString() {
        return type + param.orElse("");
    }
}