package com.blockypenguin.gemini.jem.plugins;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import com.blockypenguin.gemini.jem.browser.renderer.IRenderer;

import java.util.Map;

public interface JemPlugin {
    default Map<String, IRenderer> getRenderers() { return Map.of(); }
    default Map<String, IProtocolHandler> getProtocolHandlers() { return Map.of(); }
    
    static void load(JemPlugin p) {
        p.getProtocolHandlers().forEach(BrowserManager::registerProtocolHandler);
        p.getRenderers().forEach(BrowserManager::registerRenderer);
    }
}