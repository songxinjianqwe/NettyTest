package cn.sinjinsong.netty.discard.client;

import cn.sinjinsong.netty.constant.CharsetProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by SinjinSong on 2017/7/29.
 */
public class Client {
    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            //必须write一个ByteBuf
            future.channel().write(Unpooled.copiedBuffer("777".getBytes(CharsetProperties.UTF_8)));
            future.channel().write(Unpooled.copiedBuffer("777".getBytes(CharsetProperties.UTF_8)));
            future.channel().write(Unpooled.copiedBuffer("777".getBytes(CharsetProperties.UTF_8)));
            //必须刷新
            //只有flush时才会将数据发送给服务器
            //服务器接收到的会是连在一起的三个777
            future.channel().flush();
            
            
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Client().run();
    }
}
