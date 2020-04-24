package org.test.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

public class WebSocketIndexPageHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(),
                    HttpResponseStatus.BAD_REQUEST, ctx.alloc().buffer(0)));
            return;
        }

        if (!HttpMethod.GET.equals(request.method())) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(request.protocolVersion(),
                    HttpResponseStatus.FORBIDDEN, ctx.alloc().buffer(0)));
            return;
        }

        String requestUri = request.uri();
        if ("/".equals(requestUri) || "/index.html".equals(requestUri)) {
            ByteBuf content = WebSocketServerIndexPage.getContent();
            FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK, content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(response, content.readableBytes());

            sendHttpResponse(ctx, request, response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        HttpResponseStatus responseStatus = response.status();
        boolean isOk;
        if (!(isOk = responseStatus.code() == HttpResponseStatus.OK.code())) {
            ByteBufUtil.writeUtf8(response.content(), responseStatus.toString());
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }

        boolean keepAlive = isOk && HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, keepAlive);
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
