package cn.sinjinsong.netty.rpc.service;

import cn.sinjinsong.netty.rpc.domain.user.User;

/**
 * Created by SinjinSong on 2017/7/30.
 */
public interface HelloService {
    String hello(User user);
}
