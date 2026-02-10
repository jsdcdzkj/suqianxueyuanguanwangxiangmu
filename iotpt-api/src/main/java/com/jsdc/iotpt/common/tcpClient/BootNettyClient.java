package com.jsdc.iotpt.common.tcpClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BootNettyClient {

    public void connect(String host, int port) throws Exception {
        /**
         * 客户端的NIO线程组
         *
         */
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            /**
             * Bootstrap 是一个启动NIO服务的辅助启动类 客户端的
             */
            Bootstrap bootstrap = new Bootstrap();
            bootstrap = bootstrap.group(group);
            bootstrap = bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
            /**
             * 设置 I/O处理类,主要用于网络I/O事件，记录日志，编码、解码消息
             */
            bootstrap = bootstrap.handler(new BootNettyChannelInitializer<SocketChannel>());
            /**
             * 连接服务端
             */
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!future.isSuccess()) {
                        //重连交给后端线程执行
                        future.channel().eventLoop().schedule(() -> {
                            System.err.println("重连服务端...");
                            try {
                                connect(host, port);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 3000, TimeUnit.MILLISECONDS);
                    } else {
                        System.out.println("服务端连接成功...");
                    }
                }
            });
            if (future.isSuccess()) {
                Channel channel = future.channel();
                String id = future.channel().id().toString();
                BootNettyClientChannel bootNettyClientChannel = new BootNettyClientChannel();
                bootNettyClientChannel.setChannel(channel);
                bootNettyClientChannel.setCode("clientId:" + id);
                BootNettyClientChannelCache.save("clientId:" + id, bootNettyClientChannel);
                System.out.println("netty client start success=" + id);
                /**
                 * 等待连接端口关闭
                 */

                future.channel().closeFuture().sync();
            }
        } finally {
            /**
             * 退出，释放资源
             */
            group.shutdownGracefully().sync();
        }
    }
}
