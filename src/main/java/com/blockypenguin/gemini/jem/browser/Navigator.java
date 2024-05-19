package com.blockypenguin.gemini.jem.browser;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.utils.ExceptionalRunnableFactory;
import com.blockypenguin.gemini.jem.utils.ForkingList;
import com.blockypenguin.gemini.jem.utils.Utils;
import com.blockypenguin.gemini.jem.windowing.NavBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public final class Navigator {
    private static final Logger EXECUTOR_ERR_LOGGER = LogManager.getLogger("Browser Service Executor");
    private static final Logger LOGGER = LogManager.getLogger("Navigator");
    private final ForkingList<URL> addresses = new ForkingList<>();
    
    public void root() {
        go(addresses.peek().map(Utils::getURLRoot).orElseThrow());
    }

    public void up() {
        go(addresses.peek().map(Utils::getURLUpper).orElseThrow());
    }

    public void forward() {
        addresses.scroll(1);
        go(addresses.peek().orElseThrow());
    }

    public void back() {
        addresses.scroll(-1);
        go(addresses.peek().orElseThrow(), false, true, true);
    }

    public void reload() {
        BrowserManager.WINDOW.setRendererComponent(null);
        go(addresses.peek().orElseThrow(), false, false, false);
    }

    public void go(URL url) {
        go(url, true, true, true);
    }

    public void go(URL originalUrl, boolean affectHistory, boolean affectNavBar, boolean getFromCache) {
        if(affectHistory) addresses.add(originalUrl);
        
        var cachedRedirect = BrowserManager.getRedirectionFromCache(originalUrl);
        final var url = cachedRedirect.orElse(originalUrl);
        
        SwingUtilities.invokeLater(
            () -> {
                NavBar.checkButtons(addresses.hasAt(-1), addresses.hasAt(1), url);
                
                if(getFromCache)
                    BrowserManager.getDocumentFromCache(url).ifPresentOrElse(
                        document -> {
                            LOGGER.error("Advance rendering!");
                            this.renderDocument(document);
                        },
                        () -> LOGGER.error("Not advance rendering!")
                    );
            }
        );
        
        BrowserManager.EXECUTOR.execute(ExceptionalRunnableFactory.create(
            () -> {
                if(affectNavBar) NavBar.setURL(url);

                BrowserManager.getProtocolHandler(url).flatMap(iProtocolHandler -> {
                    try(var is = url.openConnection().getInputStream()) {
                        return iProtocolHandler.getDocument(is.readAllBytes(), url);
                    }catch(IOException e) {
                        BrowserManager.showErrorDialogue(LOGGER, e, "Error communicating with server: {}", e.getMessage());
                        return Optional.empty();
                    }
                }).ifPresent(
                    document -> {
                        SwingUtilities.invokeLater(() -> renderDocument(document));
                        BrowserManager.addDocumentToCache(url, document);
                    }
                );
            },
            ex -> {
                BrowserManager.showErrorDialogue(EXECUTOR_ERR_LOGGER, ex, "An error occurred whilst navigating to {}!", url);
            }
        ));
    }
    
    private void renderDocument(Document doc) {
        if(!SwingUtilities.isEventDispatchThread())
            throw new WrongThreadException("Must be called from the Swing thread.");
        
        BrowserManager.getRenderer(doc.mime().type()).ifPresent(
            iRenderer -> BrowserManager.WINDOW.setRendererComponent(
                iRenderer.getComponent(doc)
            )
        );
    }
}