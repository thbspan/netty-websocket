package org.test.netty.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    private static final String WEBSOCKET_PATH = "/ws";

    private static final int WEBSOCKET_PORT = 8060;

    public void start() {
        LOGGER.info("websocket server starting...");

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    // 添加日志
                                    .addLast("logging", new LoggingHandler(LogLevel.INFO))
                                    // 设置http编解码器
                                    .addLast("http-codec", new HttpServerCodec())
                                    .addLast("aggregator", new HttpObjectAggregator(65536))
                                    .addLast(new WebSocketServerCompressionHandler())
                                    .addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true))
                                    .addLast(new WebSocketIndexPageHandler())
                                    .addLast(new WebSocketFrameHandler());
                        }
                    });
            // Start the server.
            ChannelFuture f = serverBootstrap.bind(WEBSOCKET_PORT).sync();
            LOGGER.info("websocket server successful start");
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("websocket server start exception", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            LOGGER.info("websocket server shutdown");
        }

    }

    public static void main(String[] args) {
        new WebSocketServer().start();
    }
}
