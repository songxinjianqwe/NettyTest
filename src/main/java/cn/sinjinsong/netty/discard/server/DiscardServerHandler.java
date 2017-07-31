package cn.sinjinsong.netty.discard.server;

import cn.sinjinsong.netty.constant.CharsetProperties;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/29.
 * 实际的业务处理器
 */
@Slf4j
public class DiscardServerHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try{
            cause.printStackTrace();
        }finally {
            ctx.close();
        }
    }

    /**
     * 
     * @param ctx
     * @param msg 一般是一个ByteBuf对象，使用引用计数实现，需要手动释放掉，否则会出现内存泄露
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            log.info("接收到客户端发来的数据");
            //ByteBuf不同于NIO库的ByteBuffer,不需要进行flip
            //因为ByteBuf内置了两个指针，一个读指针，一个写指针
            //
            ByteBuf buf = (ByteBuf) msg;
            byte[] data = new byte[buf.readableBytes()];
            //把Buf里的数据放到Byte
            buf.readBytes(data);
            String text = new String(data, CharsetProperties.UTF_8);
            log.info("数据：{}",text);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
