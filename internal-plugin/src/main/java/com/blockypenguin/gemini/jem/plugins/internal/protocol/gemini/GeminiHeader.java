package com.blockypenguin.gemini.jem.plugins.internal.protocol.gemini;

import com.blockypenguin.gemini.jem.BrowserManager;

import java.util.Optional;

public record GeminiHeader(StatusType statusType, byte statusDigit, String data) {
    public static Optional<GeminiHeader> of(String header) {
        StatusType statusType;

        try {
            statusType = StatusType.getStatus(Byte.parseByte(header.substring(0, 2))).orElseThrow(
                () -> new NumberFormatException("Header status code is out of range!")
            );
        }catch(NumberFormatException e) {
            BrowserManager.showErrorDialogue(e, "Could not parse status code sent from server!");
            return Optional.empty();
        }

        return Optional.of(
            new GeminiHeader(
                statusType,
                Byte.parseByte(header.substring(1, 2)),
                header.substring(2).trim()
            )
        );
    }

    public static enum StatusType {
        INPUT,
        SUCCESS,
        REDIRECT,
        TEMPFAIL,
        PERMFAIL,
        CLIENT_CERT;

        public static Optional<StatusType> getStatus(byte statusCode) {
            if(statusCode < 10 || statusCode > 69)
                return Optional.empty();

            for(StatusType type : StatusType.values()) {
                int min = (type.ordinal() + 1) * 10;
                int max = min + 9;

                if(statusCode >= min && statusCode <= max)
                    return Optional.of(type);
            }

            return Optional.empty();
        }
    }
}