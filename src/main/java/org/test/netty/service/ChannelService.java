package org.test.netty.service;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelService {
    private static final ChannelGroup GLOBAL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void addChannel(Channel channel) {
        GLOBAL_GROUP.add(channel);
    }

    public static void removeChannel(Channel channel) {
        GLOBAL_GROUP.remove(channel);
    }

    public static void send2All(TextWebSocketFrame msg) {
        GLOBAL_GROUP.writeAndFlush(msg);
    }
}
