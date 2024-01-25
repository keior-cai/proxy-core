//package com.socks.proxy.protocol.handshake.handler;
//
//import com.alibaba.fastjson2.JSON;
//import com.socks.proxy.protocol.DefaultTargetServer;
//import com.socks.proxy.protocol.connect.ProxyConnect;
//import com.socks.proxy.protocol.handshake.SimpleServerHandshakeMessageHandler;
//import com.socks.proxy.protocol.handshake.message.AckTargetAddressMessage;
//import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * <p>ack 确认消息处理器实现</p>
// * <p>向本地连接发送连接成功处理器</p>
// * <p>eg：操作系统使用的http代理，或者socks代理；发送连接成功消息；
// * 之后操作系统，或者本地连接才会发送正常的数据到代理服务</p>
// * <p>参考LocaProxyChannel实现：{@link LocalConnect#writeConnectSuccess()}</p>
// *
// * @author: chuangjie
// * @date: 2023/5/21
// **/
//@Slf4j
//@AllArgsConstructor
//public class ConnectSuccessMessageHandler extends SimpleServerHandshakeMessageHandler<SenTargetAddressMessage>{
//
//    private final TargetConnectFactory factory;
//
//
//    @Override
//    protected void handleServerMessage(ProxyConnect proxy, SenTargetAddressMessage message, ProxyConnect remote){
//        log.debug("connect to target service = {}:{}", message.getHost(), message.getPort());
//        TargetConnect target = factory.getProxyService(proxy,
//                new DefaultTargetServer(message.getHost(), message.getPort(), null));
//        try {
//            target.connect();
//            proxy.setTarget(target);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        proxy.write(JSON.toJSONString(new AckTargetAddressMessage()));
//    }
//}
