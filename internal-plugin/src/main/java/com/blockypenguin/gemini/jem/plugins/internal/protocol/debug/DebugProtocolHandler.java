package com.blockypenguin.gemini.jem.plugins.internal.protocol.debug;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.MimeDescriptor;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class DebugProtocolHandler implements IProtocolHandler {
    @Override
    public URLConnection createURLConnection(URL url) {
        return new URLConnection(url) {
            @Override
            public void connect() {}
            
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(
                    "Hello, world! You found Jem's secret debugging thingamajig. Well done!".getBytes(StandardCharsets.UTF_8)
                );
            }
        };
    }
    
    @Override
    public int getDefaultPort() { return 0; }
    
    @Override
    public Optional<Document> getDocument(byte[] reader, URL url) {
        BrowserManager.getUserInterface().setNavURLText("hello://world");
        return Optional.of(new Document(
            new MimeDescriptor(
                "text/plain",
                Optional.empty()
            ),
            reader
        ));
    }
}