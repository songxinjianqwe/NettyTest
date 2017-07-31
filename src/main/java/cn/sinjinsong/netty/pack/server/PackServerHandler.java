package cn.sinjinsong.netty.pack.server;

import cn.sinjinsong.netty.constant.CharsetProperties;
import cn.sinjinsong.netty.constant.DelimeterProperties;
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
public class PackServerHandler extends ChannelHandlerAdapter {
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
            log.info("数据：{}", (String) msg);
            ctx.channel().writeAndFlush(
                    Unpooled.copiedBuffer(
                            (msg + DelimeterProperties.DELIMETER)
                    .getBytes(CharsetProperties.UTF_8))
            );
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
