//package com.socks.proxy.protocol.listener;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.socks.proxy.protocol.LocalMiddleServiceProxyFactory;
//import com.socks.proxy.protocol.connect.ProxyConnect;
//import com.socks.proxy.protocol.TargetServer;
//import com.socks.proxy.protocol.codes.ProxyMessage;
//import com.socks.proxy.protocol.command.ProxyCommand;
//import com.socks.proxy.protocol.enums.ServerProxyCommand;
//import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
//import com.socks.proxy.util.FieldNameUtils;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.lang.reflect.ParameterizedType;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author: chuangjie
// * @date: 2023/7/9
// **/
//@Slf4j
//@AllArgsConstructor
//public class DefaultSendBinaryListener implements LocalConnectListener{
//
//    private final Map<ProxyCommand, LocalHandshakeMessageHandler<?>> messageHandlerMap;
//
//    private final LocalMiddleServiceProxyFactory factory;
//
//    private final Map<LocalConnect, LocalMiddleService> connectMap = new ConcurrentHashMap<>();
//
//
//    @Override
//    public void onCreate(ProxyConnect local, TargetServer remoteServer, ProxyConnect remote){
//    }
//
//
//    @Override
//    public void onConnect(ProxyConnect local, TargetServer remoteServer, ProxyConnect remote){
//        connectMap.put(local, remote);
//    }
//
//
//    @Override
//    public void onCallbackError(ProxyConnect local, ProxyConnect remote, Throwable e){
//
//    }
//
//
//    @Override
//    public void onLocalClose(ProxyConnect context, ProxyConnect remote){
//        connectMap.remove(context);
//    }
//
//
//    @Override
//    public void onError(ProxyConnect context, ProxyConnect connect, Throwable cause){
//
//    }
//
//
//    @Override
//    public void onSendBinary(ProxyConnect local, byte[] message, ProxyConnect remote){
//
//    }
//
//
//    @Override
//    public void onBinary(ProxyConnect local, byte[] message, ProxyConnect remote){
//
//    }
//
//
//    @Override
//    public void onMessage(ProxyConnect context, String text, ProxyConnect localMiddleService){
//        JSONObject object = JSON.parseObject(text);
//        if(object == null){
//            return;
//        }
//        String commandField = FieldNameUtils.getFieldName(ProxyMessage::getCommand);
//        Integer commandCode = object.getInteger(commandField);
//        ProxyCommand command = ServerProxyCommand.of(commandCode);
//        LocalHandshakeMessageHandler<?> remoteMessageHandler = messageHandlerMap.get(command);
//        ParameterizedType type = (ParameterizedType) remoteMessageHandler.getClass().getGenericInterfaces()[0];
//        if(log.isDebugEnabled()){
//            log.debug("handle receive message class - {}, command = {}", type, command);
//        }
//        remoteMessageHandler.handle(context, object.to(type.getActualTypeArguments()[0]),
//                factory.getService(localMiddleService));
//    }
//
//}
