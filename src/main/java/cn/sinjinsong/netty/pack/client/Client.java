package cn.sinjinsong.netty.pack.client;

import cn.sinjinsong.netty.constant.CharsetProperties;
import cn.sinjinsong.netty.constant.DelimeterProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by SinjinSong on 2017/7/29.
 */
public class Client {
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf buf = Unpooled.copiedBuffer(DelimeterProperties.DELIMETER.getBytes(CharsetProperties.UTF_8));
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new PackClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            //必须write一个ByteBuf
            //可以使用writeAndFlush，将缓冲区的内容直接发送到服务器       
            //两次发送，服务器接收到的数据被分为两个包
            future.channel().writeAndFlush(Unpooled.copiedBuffer(("777" + DelimeterProperties.DELIMETER).getBytes(CharsetProperties.UTF_8)));
            future.channel().writeAndFlush(Unpooled.copiedBuffer(("777" + DelimeterProperties.DELIMETER).getBytes(CharsetProperties.UTF_8)));

            //因为这里采用的是短连接，服务器接收到数据后会关闭Channel，
            //所以当Channel关闭时，下面这行代码会解除主线程的阻塞，程序执行完毕
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
