package com.blockypenguin.gemini.jem;

import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.MimeDescriptor;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import com.blockypenguin.gemini.jem.browser.protocol.gemini.GeminiProtocolHandler;
import com.blockypenguin.gemini.jem.browser.renderer.gemtext.GemtextRenderer;
import com.blockypenguin.gemini.jem.browser.renderer.plaintext.PlainTextRenderer;
import com.blockypenguin.gemini.jem.windowing.NavBar;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class JemInit {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollPane.arc", 12);
        UIManager.put("Button.arc", 12);
        
        BrowserManager.registerRenderer("text/gemini", new GemtextRenderer());
        BrowserManager.registerRenderer("text/plain", new PlainTextRenderer());

        BrowserManager.registerProtocolHandler("gemini", new GeminiProtocolHandler());
        BrowserManager.registerProtocolHandler("hello", new IProtocolHandler() {
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
            public int getDefaultPort() {
                return 0;
            }
            
            @Override
            public Optional<Document> getDocument(byte[] reader, URL url) {
                NavBar.setURL("hello://world");
                return Optional.of(new Document(
                    new MimeDescriptor(
                        "text/plain",
                        Optional.empty()
                    ),
                    reader
                ));
            }
        });

        BrowserManager.registerProtocolHandler("msg", new IProtocolHandler() {
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
            public int getDefaultPort() {
                return 0;
            }
            
            @Override
            public boolean isExemptFromCaching() { return true; };

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
        });
        
        NavBar.navigateGo();
        SwingUtilities.invokeLater(() -> BrowserManager.WINDOW.setVisible(true));
    }
}