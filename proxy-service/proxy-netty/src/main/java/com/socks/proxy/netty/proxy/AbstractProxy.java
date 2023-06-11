package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.DstServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.RemoteProxyConnect;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 代理处理器
 *
 * @author: chuangjie
 * @date: 2023/5/26
 **/
@Slf4j
@AllArgsConstructor
public abstract class AbstractProxy<I> extends SimpleChannelInboundHandler<I>{

    private final LocalConnectServerFactory factory;

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8192);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg){
        DstServer remoteServer = resolveRemoteServer(msg);
        ctx.channel().attr(AttrConstant.REMOTE_SERVER).set(remoteServer);
        ChannelPipeline pipeline = ctx.pipeline();
        LocalProxyConnect localProxyConnect = createProxyConnect(ctx, remoteServer);
        executor.execute(()->{
            RemoteProxyConnect connect = factory.getProxyService(localProxyConnect);
            try {
                connect.connect();
                localProxyConnect.setRemoteChannel(connect);
            } catch (Exception e) {
                log.error("connect server fail message = {}", e.getMessage());
                localProxyConnect.writeConnectFail();
            }
        });
        pipeline.remove(this);
    }


    /**
     * 解决socket消息生成目标地址连接信息
     *
     * @param msg socket 消息对象
     * @return 目标地址连接信息
     */
    protected abstract DstServer resolveRemoteServer(I msg);


    /**
     * 创建ss-server服务连接对象
     *
     * @param ctx 本地连接通道，操作系统连接过着代理程序代理连接
     * @param dstServer 目标服务地址，端口
     */
    protected abstract LocalProxyConnect createProxyConnect(ChannelHandlerContext ctx, DstServer dstServer);
}
