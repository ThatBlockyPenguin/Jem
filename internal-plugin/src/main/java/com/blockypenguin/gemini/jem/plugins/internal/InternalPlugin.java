package com.blockypenguin.gemini.jem.plugins.internal;

import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import com.blockypenguin.gemini.jem.browser.renderer.IRenderer;
import com.blockypenguin.gemini.jem.plugins.internal.protocol.debug.DebugProtocolHandler;
import com.blockypenguin.gemini.jem.plugins.internal.protocol.gemini.GeminiProtocolHandler;
import com.blockypenguin.gemini.jem.plugins.internal.protocol.msg.MsgProtocolHandler;
import com.blockypenguin.gemini.jem.plugins.internal.renderer.gemtext.GemtextRenderer;
import com.blockypenguin.gemini.jem.plugins.internal.renderer.plaintext.PlainTextRenderer;
import com.blockypenguin.gemini.jem.plugins.JemPlugin;

import java.util.Map;

public final class InternalPlugin implements JemPlugin {
    @Override
    public Map<String, IProtocolHandler> getProtocolHandlers() {
        return Map.of(
            "hello",
            new DebugProtocolHandler(),
            "msg",
            new MsgProtocolHandler(),
            "gemini",
            new GeminiProtocolHandler()
        );
    }
    
    @Override
    public Map<String, IRenderer> getRenderers() {
        return Map.of(
            "text/plain",
            new PlainTextRenderer(),
            "text/gemini",
            new GemtextRenderer()
        );
    }
}