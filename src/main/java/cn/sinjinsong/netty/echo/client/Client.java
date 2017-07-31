package cn.sinjinsong.netty.echo.client;

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
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            //必须write一个ByteBuf
            //可以使用writeAndFlush，将缓冲区的内容直接发送到服务器            
            future.channel().writeAndFlush(Unpooled.copiedBuffer("777".getBytes(CharsetProperties.UTF_8)));
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
