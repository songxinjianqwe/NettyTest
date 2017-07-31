package cn.sinjinsong.netty.pack.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by SinjinSong on 2017/7/29.
 */
@Slf4j
public class PackClientHandler extends ChannelHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            cause.printStackTrace();
        } finally {
            ctx.close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端通道已开启...");
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("接收到服务器发来的数据");
            log.info("数据：{}", (String) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
