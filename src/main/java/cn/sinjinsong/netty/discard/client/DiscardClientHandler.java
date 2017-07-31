package cn.sinjinsong.netty.discard.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class DiscardClientHandler extends ChannelHandlerAdapter {
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
            
        } finally {
            ((ByteBuf) msg).release();
        }
    }
}
