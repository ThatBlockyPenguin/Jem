package com.blockypenguin.gemini.jem.utils;

public final class ExceptionalRunnableFactory {
    public static Runnable create(ExceptionalRunnable runnable, ExceptionHandler handler) {
        return () -> {
            try {
                runnable.run();
            }catch(Exception ex) {
                handler.handle(ex);
            }
        };
    }

    @FunctionalInterface
    public interface ExceptionalRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface ExceptionHandler {
        void handle(Exception ex);
    }
}