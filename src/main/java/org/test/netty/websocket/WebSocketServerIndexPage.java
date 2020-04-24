package org.test.netty.websocket;

import java.nio.file.Files;
import java.nio.file.Paths;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class WebSocketServerIndexPage {

    private static final byte[] BYTES_WEBSOCKET_INDEX_PAGE;

    static {
        try {
            BYTES_WEBSOCKET_INDEX_PAGE = Files.readAllBytes(Paths.get(WebSocketServerIndexPage.class.getResource("/index.html").toURI()));
        } catch (Exception e) {
            throw new IllegalStateException("Web Socket index page not found!");
        }
    }

    public static ByteBuf getContent() {
        return Unpooled.copiedBuffer(BYTES_WEBSOCKET_INDEX_PAGE);
    }

    private WebSocketServerIndexPage() {
        // Unused
    }
}
