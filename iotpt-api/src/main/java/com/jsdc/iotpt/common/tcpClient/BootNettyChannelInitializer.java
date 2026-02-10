package com.jsdc.iotpt.common.tcpClient;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;

@ChannelHandler.Sharable
public class BootNettyChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast("encoder", new MyEncoder());

        channel.pipeline().addLast("decoder", new MyDecoder());
        /**
         * 自定义ChannelInboundHandlerAdapter
         */
        channel.pipeline().addLast(new BootNettyChannelInboundHandlerAdapter());
    }
}
