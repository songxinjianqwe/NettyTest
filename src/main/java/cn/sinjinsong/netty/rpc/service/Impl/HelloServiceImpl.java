package cn.sinjinsong.netty.rpc.service.Impl;

import cn.sinjinsong.netty.rpc.annotation.RPCService;
import cn.sinjinsong.netty.rpc.domain.user.User;
import cn.sinjinsong.netty.rpc.service.HelloService;

/**
 * Created by SinjinSong on 2017/7/30.
 */
@RPCService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(User user) {
        return "Hello, " + user.getUsername();
    }
}
