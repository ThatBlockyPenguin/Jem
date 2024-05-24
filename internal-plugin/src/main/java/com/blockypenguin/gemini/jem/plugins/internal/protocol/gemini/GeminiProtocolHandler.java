package com.blockypenguin.gemini.jem.plugins.internal.protocol.gemini;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.MimeDescriptor;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import com.blockypenguin.gemini.jem.utils.CoreUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

//TODO: Warn when TLS 1.2 is used
public class GeminiProtocolHandler implements IProtocolHandler {
    private static final Logger LOGGER = LogManager.getLogger("Gemini Protocol Handler");
    private static final String CRLF = "\r\n";
    private static final byte[] CRLF_BYTES = CRLF.getBytes(StandardCharsets.UTF_8);
    private static final String[] PROTOCOLS = { "TLSv1.2", "TLSv1.3" };
    private static final String[] CIPHER_SUITES = { "TLS_AES_128_GCM_SHA256" };

    private static final SSLContext sslContext;

    static {
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new X509TrustManager[]{new TofuTrustManager()}, null);
        }catch(KeyManagementException | NoSuchAlgorithmException e) {
            BrowserManager.showErrorDialogue(LOGGER, e, "Could not set up Gemini Trust Manager - Loading Gemini URLs will not work!");
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getDefaultPort() {
        return 1965;
    }
    
    @Override
    public Optional<Document> getDocument(byte[] data, URL url) {
        int index = CoreUtils.byteArrayIndexOf(data, CRLF_BYTES);
        
        if(CoreUtils.byteArrayIndexOf(data, CRLF_BYTES) == -1) {
            BrowserManager.showErrorDialogue(LOGGER, new Throwable("Header invalid: " + new String(data)), "Invalid response from server!");
            return Optional.empty();
        }

        var header = GeminiHeader.of(new String(data, 0, index, StandardCharsets.UTF_8));

        return header.map(geminiHeader -> processResponse(
            geminiHeader,
            Arrays.copyOfRange(data, index + CRLF_BYTES.length, data.length),
            new String(data),
            url
        ));

    }

    private Document processResponse(GeminiHeader header, byte[] content, String fullResponse, URL address) {
        switch(header.statusType()) {
            case SUCCESS -> {
                return new Document(MimeDescriptor.create(header.data()), content);
            }
            case REDIRECT -> {
                CoreUtils.createURLWithHandler(URI.create(header.data())).ifPresentOrElse(
                    toUrl -> {
                        if(header.statusDigit() == 1)
                            BrowserManager.addRedirectionToCache(address, toUrl);
                        
                        BrowserManager.NAVIGATOR.go(toUrl, false, true, true);
                    },
                    () -> BrowserManager.showErrorDialogue(LOGGER, "Redirection took you to an invalid URL: \"{}\"", address)
                );
            }
            case TEMPFAIL -> {
                String data = !header.data().isBlank() ? "\n\nThe server sends the following message:\n" + header.data() : "";

                if(header.statusDigit() == 0)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "An unspecified condition exists on the server that is preventing the content from being served, but you can try again to potentially obtain the content.{}",
                        data
                    );
                else if(header.statusDigit() == 1)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "The server at {} is unavailable due to overload or maintenance.{}", address.getHost(),
                        data
                    );
                else if(header.statusDigit() == 2)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "A CGI process or similar system on the server, died unexpectedly or timed out.{}",
                        data
                    );
                else if(header.statusDigit() == 3)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "A proxy request failed because the server was unable to successfully complete a transaction with the remote host.{}",
                        data
                    );
                else if(header.statusDigit() == 4)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "Woah! Too fast! The server is asking you to stop sending so many requests so quickly.{}",
                        data
                    );
                else
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "The server sent an invalid \"Temporary Failure\" message. This page should be working again later.{}",
                        data
                    );
            }
            case PERMFAIL -> {
                String data = !header.data().isBlank() ? "\n\nThe server sends the following message:\n" + header.data() : "";

                if(header.statusDigit() == 0)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "An unspecified condition exists on the server that is preventing the content from being served. Trying again will likely not change this.{}",
                        data
                    );
                else if(header.statusDigit() == 1)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "The requested resource could not be found and no further information is available. It may exist in the future, it may not. Who knows?{}",
                        data
                    );
                else if(header.statusDigit() == 2)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "The resource requested is no longer available and will not be available again.{}",
                        data
                    );
                else if(header.statusDigit() == 3)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "You requested a resource at a domain not served by this server, and the server does not accept proxy requests.{}",
                        data
                    );
                else if(header.statusDigit() == 9)
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "The server was unable to parse your request, presumably due to a malformed request.{}",
                        data
                    );
                else
                    BrowserManager.showErrorDialogue(
                        LOGGER,
                        "The server sent an invalid \"Permanent Failure\" message. This page will most likely not be working later.{}",
                        data
                    );
            }
            default -> BrowserManager.showErrorDialogue("Unimplemented response type:\n>   {}\n>   {}", header.statusType(), fullResponse);
        }

        return null;
    }

    @Override
    public URLConnection createURLConnection(URL url) {
        return new URLConnection(url) {
            private SSLSocket socket;

            @Override
            public void connect() throws IOException {
                String request = getURL().toExternalForm() + CRLF;

                String host = getURL().getHost();
                int port = getURL().getPort();

                if(port == -1)
                    port = url.getDefaultPort();

                SSLSocketFactory factory = sslContext.getSocketFactory();
                socket = (SSLSocket) factory.createSocket(host, port);
                socket.setEnabledProtocols(PROTOCOLS);
                socket.setEnabledCipherSuites(CIPHER_SUITES);

                var toServer = new OutputStreamWriter(
                    socket.getOutputStream(),
                    StandardCharsets.UTF_8
                );

                toServer.write(request);
                toServer.flush();

                connected = true;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                if(!connected)
                    connect();

                return socket.getInputStream();
            }
        };
    }
}