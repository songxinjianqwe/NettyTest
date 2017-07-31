package cn.sinjinsong.netty.discard.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by SinjinSong on 2017/7/29.
 */
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
                            //加进来
                            ch.pipeline().addLast(new DiscardServerHandler());
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
                    //这里的option是针对于上面的NioServerSocketChannel
                    //复杂的时候可能会设置多个Channel
                    //option()是提供给NioServerSocketChannel用来接收进来的连接。
                    //childOption()是提供给由父管道ServerChannel接收到的连接，在这个例子中也是NioServerSocketChannel。
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //绑定端口，开始监听
            ChannelFuture future = bootstrap.bind(PORT).sync();
            //阻塞在这里
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
