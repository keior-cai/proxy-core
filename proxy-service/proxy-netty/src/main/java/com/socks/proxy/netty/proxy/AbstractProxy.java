package com.socks.proxy.netty.proxy;

import com.socks.proxy.netty.constant.AttrConstant;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.factory.LocalConnectServerFactory;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;

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

    private final List<LocalConnectListener> listeners;

    private final ExecutorService executor;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, I msg){
        TargetServer remoteServer = resolveRemoteServer(msg);
        ChannelPipeline pipeline = ctx.pipeline();
        LocalConnect localConnect = createProxyConnect(ctx, remoteServer, listeners);
        ctx.channel().attr(AttrConstant.LOCAL_CONNECT).set(localConnect);
        executor.execute(()->{
            LocalMiddleService connect = null;
            try {
                connect = factory.getProxyService(localConnect, remoteServer);
                localConnect.setRemoteChannel(connect);
                for(LocalConnectListener listener : listeners) {
                    listener.onCreate(localConnect, remoteServer, connect);
                }
                connect.connect();
                for(LocalConnectListener listener : listeners) {
                    listener.onConnect(localConnect, remoteServer, connect);
                }
            } catch (Exception e) {
                for(LocalConnectListener listener : listeners) {
                    listener.onCallbackError(localConnect, connect, e);
                }
                log.error("connect server fail message = {}", e.getMessage());
                localConnect.writeConnectFail();
            }
        });
        pipeline.remove(this);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        LocalMiddleService remoteProxyConnect = ctx.channel().attr(AttrConstant.TARGET_SERVICE).get();
        LocalConnect localConnect = ctx.channel().attr(AttrConstant.LOCAL_CONNECT).get();
        for(LocalConnectListener listener : listeners) {
            listener.onLocalClose(localConnect, remoteProxyConnect);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        LocalMiddleService remoteProxyConnect = ctx.channel().attr(AttrConstant.TARGET_SERVICE).get();
        LocalConnect localConnect = ctx.channel().attr(AttrConstant.LOCAL_CONNECT).get();
        for(LocalConnectListener listener : listeners) {
            listener.onError(localConnect, remoteProxyConnect, cause);
        }
    }


    /**
     * 解决socket消息生成目标地址连接信息
     *
     * @param msg socket 消息对象
     * @return 目标地址连接信息
     */
    protected abstract TargetServer resolveRemoteServer(I msg);


    /**
     * 创建ss-server服务连接对象
     *
     * @param ctx 本地连接通道，操作系统连接过着代理程序代理连接
     * @param dstServer 目标服务地址，端口
     * @param listeners 监听处理器
     */
    protected abstract LocalConnect createProxyConnect(ChannelHandlerContext ctx, TargetServer dstServer,
                                                       List<LocalConnectListener> listeners);
}
