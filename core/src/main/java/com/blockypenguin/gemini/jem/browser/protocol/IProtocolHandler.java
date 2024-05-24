package com.blockypenguin.gemini.jem.browser.protocol;

import com.blockypenguin.gemini.jem.browser.Document;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Optional;

public interface IProtocolHandler {
    URLConnection createURLConnection(URL url);
    int getDefaultPort();
    Optional<Document> getDocument(byte[] data, URL url);
    
    default boolean isExemptFromCaching() {
        return false;
    }

    default URLStreamHandler getCustomURLStreamHandler() {
        return new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(URL url) {
                return createURLConnection(url);
            }

            @Override
            protected int getDefaultPort() {
                return IProtocolHandler.this.getDefaultPort();
            }
            
            @Override
            protected String toExternalForm(URL u) {
                return super.toExternalForm(u).split("#")[0];
            }
        };
    }
}