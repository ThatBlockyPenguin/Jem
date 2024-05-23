package com.blockypenguin.gemini.jem.plugins.internal.protocol.msg;

import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.MimeDescriptor;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class MsgProtocolHandler implements IProtocolHandler {
    @Override
    public URLConnection createURLConnection(URL url) {
        return new URLConnection(url) {
            @Override
            public void connect() {}
            
            @Override
            public InputStream getInputStream() {
                String urlString = url.toExternalForm();
                
                urlString = urlString.substring(urlString.indexOf(":") + 1);
                if(urlString.startsWith("//"))
                    urlString = urlString.substring(2);
                
                return new ByteArrayInputStream(
                    URLDecoder.decode(urlString, StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8)
                );
            }
        };
    }
    
    @Override
    public int getDefaultPort() { return 0; }
    
    @Override
    public boolean isExemptFromCaching() { return true; }
    
    @Override
    public Optional<Document> getDocument(byte[] reader, URL url) {
        return Optional.of(new Document(
            new MimeDescriptor(
                "text/plain",
                Optional.empty()
            ),
            reader
        ));
    }
}