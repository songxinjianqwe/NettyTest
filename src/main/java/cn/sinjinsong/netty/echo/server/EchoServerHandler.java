package cn.sinjinsong.netty.echo.server;

import cn.sinjinsong.netty.constant.CharsetProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/29.
 * 实际的业务处理器
 */
@Slf4j
public class EchoServerHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            cause.printStackTrace();
        } finally {
            ctx.close();
        }
    }

    /**
     * @param ctx
     * @param msg 一般是一个ByteBuf对象，使用引用计数实现，需要手动释放掉，否则会出现内存泄露
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("接收到客户端发来的数据");
            //ByteBuf不同于NIO库的ByteBuffer,不需要进行flip
            //因为ByteBuf内置了两个指针，一个读指针，一个写指针
            //
            ByteBuf buf = (ByteBuf) msg;
            byte[] data = new byte[buf.readableBytes()];
            //把Buf里的数据放到Byte
            buf.readBytes(data);
            String text = new String(data, CharsetProperties.UTF_8);
            log.info("数据：{}", text);

            //将数据echo给客户端
            //如果再次写回，那么不需要释放

//      根据上面的谁最后谁负责原则，每一个Handler对消息可能有三种处理方式
//      对原消息不做处理，调用 ctx.fireChannelRead(msg) 把原消息往下传，那不用做什么释放。
//      将原消息转化为新的消息并调用 ctx.fireChannelRead(newMsg) 往下传，那必须把原消息release掉。
//      如果已经不再调用ctx.fireChannelRead(msg) 传递任何消息，那更要把原消息release掉。
//      假设每一个Handler都把消息往下传，Handler并也不知道谁是启动Netty时所设定的Handler链的最后一员，所以Netty会在Handler链的最末补一个TailHandler，
//      如果此时消息仍然是ReferenceCounted类型就会被release掉。
//      不过如果我们的业务Hanlder不再把消息往下传了，这个TailHandler就派不上用场。

            //在Netty里所有的操作都是异步的。举个例子下面的代码中在消息被发送之前可能会先关闭连接。
            //Channel ch = ...;
            //ch.writeAndFlush(message);
            //ch.close();
            //因此你需要在write()方法返回的ChannelFuture完成后调用close()方法，然后当他的写操作已经完成他会通知他的监听者。

            ctx.channel().writeAndFlush(Unpooled.copiedBuffer(data));//这里返回一个ChannelFuture
            //发送完即把连接关闭
            //如果加这个，就是一个短连接，一次请求一次响应即关闭连接
            //如果不加这个，就是一个长连接，会长期保持服务器和客户端之间的连接
//                .addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
