package com.blockypenguin.gemini.jem.browser;

public record Document(MimeDescriptor mime, byte[] data, boolean shouldCache) {
    public Document(MimeDescriptor mime, byte[] data) {
        this(mime, data, true);
    }
}