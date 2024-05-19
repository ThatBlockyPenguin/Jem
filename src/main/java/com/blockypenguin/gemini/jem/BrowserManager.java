package com.blockypenguin.gemini.jem;

import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.Navigator;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import com.blockypenguin.gemini.jem.browser.renderer.IRenderer;
import com.blockypenguin.gemini.jem.utils.Utils;
import com.blockypenguin.gemini.jem.windowing.JemWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class BrowserManager {
    private static final Logger LOGGER = LogManager.getLogger("Browser Manager");
    private static final Map<String, IProtocolHandler> PROTOCOL_MAP = new HashMap<>();
    private static final Map<String, IRenderer> RENDERER_MAP = new HashMap<>();
    private static final Map<String, Document> DOCUMENT_CACHE = new HashMap<>();
    private static final Map<String, String> REDIRECT_CACHE = new HashMap<>();

    public static final String DEFAULT_ADDRESS = "gemini://tilde.team/~thatblockypenguin/jem/startpage.gmi";
    public static final String HELP_ADDRESS = "gemini://tilde.team/~thatblockypenguin/jem/help/";
    public static final Navigator NAVIGATOR = new Navigator();
    public static final ExecutorService EXECUTOR = Executors.newThreadPerTaskExecutor(
        Thread.ofVirtual().name("browser-service-executor-", 0).factory()
    );
    public static final JemWindow WINDOW = new JemWindow();
    
    public static void registerProtocolHandler(String protocol, IProtocolHandler handler) {
        PROTOCOL_MAP.put(protocol.trim().toLowerCase(), handler);
    }
    
    public static void registerRenderer(String mimeType, IRenderer renderer) {
        RENDERER_MAP.put(mimeType.trim().toLowerCase(), renderer);
    }

    public static Optional<IProtocolHandler> getProtocolHandler(URL url) {
        return getProtocolHandler(url.getProtocol());
    }

    public static Optional<IProtocolHandler> getProtocolHandler(String protocol) {
        protocol = protocol.trim().toLowerCase();
        var handler = PROTOCOL_MAP.get(protocol);

        if(handler == null) {
            showErrorDialogue("No handler registered for protocol {}!", protocol.toUpperCase());
            return Optional.empty();
        }

        return Optional.of(handler);
    }
    
    public static Optional<IRenderer> getRenderer(String mimeType) {
        mimeType = mimeType.trim().toLowerCase();
        var renderer = RENDERER_MAP.get(mimeType);
        
        if(renderer == null) {
            showErrorDialogue("No renderer registered for content of type {}!", mimeType);
            return Optional.empty();
        }
        
        return Optional.of(renderer);
    }
    
    public static void addDocumentToCache(URL address, Document doc) {
        if(doc.shouldCache() & !getProtocolHandler(address).map(IProtocolHandler::isExemptFromCaching).orElseThrow()) {
            LOGGER.error("Adding {} to cache: {}", address, doc.mime());
            DOCUMENT_CACHE.put(address.toString(), doc);
        }else LOGGER.error("Not adding {} to cache - it is marked as non-cacheable!", address);
    }
    
    public static Optional<Document> getDocumentFromCache(URL address) {
        var result = Optional.ofNullable(DOCUMENT_CACHE.get(address.toString()));
        LOGGER.error("Getting {} from cache: ({})", address, result.map(Document::toString).orElse("empty"));
        return result;
    }
    
    public static void addRedirectionToCache(URL from, URL to) {
        LOGGER.error("Caching redirection from {} to {}", from, to);
        REDIRECT_CACHE.put(from.toString(), to.toString());
    }
    
    @SuppressWarnings("deprecation")
    public static Optional<URL> getRedirectionFromCache(URL from) {
        var to = Optional.ofNullable(REDIRECT_CACHE.get(from.toString()));
        to.ifPresent(url -> LOGGER.error("Getting redirection of {} to {} from cache", from, url));
        return to.map(url -> {
            try {
                return new URL(from, url);
            }catch(MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void showErrorDialogue(String msg) {
        showErrorDialogue(LOGGER, msg);
    }

    public static void showErrorDialogue(Throwable t, String msg) {
        showErrorDialogue(LOGGER, t, msg);
    }

    public static void showErrorDialogue(Throwable t, String msg, Object... params) {
        showErrorDialogue(LOGGER, t, msg, params);
    }

    public static void showErrorDialogue(String msg, Object... params) {
        showErrorDialogue(LOGGER, msg, params);
    }

    public static void showErrorDialogue(Logger logger, String msg, Object... params) {
        showErrorDialogue(logger, null, msg, params);
    }

    public static void showErrorDialogue(Logger logger, Throwable t, String msg, Object... params) {
        var messageFactory = logger.getMessageFactory();

        var dialogMessage = messageFactory.newMessage(msg, params).getFormattedMessage();

        if(t != null) {
            msg += "\n\n{}";
            params = Arrays.copyOf(params, params.length + 1);
            params[params.length - 1] = Utils.getStackTrace(t);
        }

        var logMsg = messageFactory.newMessage(msg, params).getFormattedMessage();

        logger.error(logMsg);

        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                WINDOW,
                dialogMessage,
                "Error!",
                JOptionPane.ERROR_MESSAGE
            )
        );

        SwingUtilities.invokeLater(() ->
            createMsgURL(
                "An error occurred! Sorry about that!\n\n"
                    + (logMsg.contentEquals(dialogMessage) ? "Information:" : "Guru Meditation:")
                    + "\n"
                    + logMsg
            ).ifPresentOrElse(
                url -> NAVIGATOR.go(url, false, false, false),
                () -> WINDOW.setRendererComponent(null)
            )
        );
    }
    
    private static Optional<URL> createMsgURL(String message) {
        return Utils.createURL(URI.create("msg://" + URLEncoder.encode(message, StandardCharsets.UTF_8)));
    }
}