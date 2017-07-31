package cn.sinjinsong.netty.pack.server;

import cn.sinjinsong.netty.constant.CharsetProperties;
import cn.sinjinsong.netty.constant.DelimeterProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class Server {
    public static final int PORT = 8080;
    
    public void run(){
        //两个事件循环器，第一个用于接收客户端连接，第二个用于处理客户端的读写请求
        //是线程组，持有一组线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //服务器辅助类，用于配置服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置服务器参数
            bootstrap.group(bossGroup,workerGroup)
                    //使用这种类型的NIO通道，现在是基于TCP协议的
                    .channel(NioServerSocketChannel.class)
                    //对Channel进行初始化，绑定实际的事件处理器，要么实现ChannelHandler接口，要么继承ChannelHandlerAdapter类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //顺序不能变
                            
                            //使用定长数据包解决粘包问题
                            //如果不够长，那么会使用空格来补位
                            //ch.pipeline().addLast(new FixedLengthFrameDecoder(5));
                            
                            //使用分隔符解决粘包问题
                            //发送的数据最后加一个$_，表示本次分包结束
                            ByteBuf buf = Unpooled.copiedBuffer(DelimeterProperties.DELIMETER.getBytes(CharsetProperties.UTF_8));
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf));
                            //设置字符串形式的解码，可以直接把msg转成String，已经解析好了。
                            //注意：只是在channelRead方法中接收到的msg会被转为String，调用write方法时仍需发送一个ByteBuf而非String
                            //这只是一个Decoder，自动地将字节转为字符，并没有Encoder
                            ch.pipeline().addLast(new StringDecoder());
                            //加进来
                            ch.pipeline().addLast(new PackServerHandler());
                        }
                    })
                    //服务器配置项
                    //BACKLOG
                    //TCP维护有两个队列，分别称为A和B
                    //客户端发送SYN，服务器接收到后发送SYN ACK，将客户端放入到A队列
                    //客户端接收到后再次发送ACK，服务器接收到后将客户端从A队列移至B队列，服务器的accept返回。
                    //A和B队列长度之和为backlog
                    //当A和B队列长度之和大于backlog时，新的连接会被TCP内核拒绝
                    //注意：backlog对程序的连接数并无影响，影响的只是还没有被accept取出的连接数。
                    .option(ChannelOption.SO_BACKLOG,128)
                    //指定发送缓冲区大小
                    .option(ChannelOption.SO_SNDBUF,32*1024)
                    //指定接收缓冲区大小
                    .option(ChannelOption.SO_RCVBUF,32*1024)
                    //这里的option是针对于上面的NioServerSocketChannel
                    //复杂的时候可能会设置多个Channel
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //.sync表示是一个同步阻塞执行，普通的Netty的IO操作都是异步执行的
            //一个ChannelFuture代表了一个还没有发生的I/O操作。这意味着任何一个请求操作都不会马上被执行
            //Netty强烈建议直接通过添加监听器的方式获取I/O结果，而不是通过同步等待(.sync)的方式
            //如果用户操作调用了sync或者await方法，会在对应的future对象上阻塞用户线程
            
            //绑定端口，开始监听
            //注意这里可以绑定多个端口，每个端口都针对某一种类型的数据（控制消息，数据消息）
            ChannelFuture future = bootstrap.bind(PORT).sync();
            log.info("服务器启动");
            //应用程序会一直等待，直到channel关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) {
        new Server().run();
    }
}
