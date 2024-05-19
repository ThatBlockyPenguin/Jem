package com.blockypenguin.gemini.jem.utils;

import com.blockypenguin.gemini.jem.BrowserManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class MouseHandlerFactory {
    public static MouseListener create(Type type, Handler handler) {
        return switch(type) {
            case CLICK -> new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BrowserManager.EXECUTOR.execute(() -> handler.handle(e));
                }
            };
            case PRESS -> new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    BrowserManager.EXECUTOR.execute(() -> handler.handle(e));
                }
            };
            case RELEASE -> new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    BrowserManager.EXECUTOR.execute(() -> handler.handle(e));
                }
            };
            case ENTER -> new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    BrowserManager.EXECUTOR.execute(() -> handler.handle(e));
                }
            };
            case EXIT -> new MouseAdapter() {
                @Override // Navigator runs in new thread later
                public void mouseExited(MouseEvent e) { handler.handle(e); }
            };
        };
    }
    
    @FunctionalInterface
    public static interface Handler {
        void handle(MouseEvent e);
    }
    
    public static enum Type {
        CLICK,
        PRESS,
        RELEASE,
        ENTER,
        EXIT
    }
}
