package com.socks.proxy.protocol.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleService;
import com.socks.proxy.protocol.LocalMiddleServiceProxyFactory;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.util.FieldNameUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: chuangjie
 * @date: 2023/7/9
 **/
@Slf4j
@AllArgsConstructor
public class DefaultSendBinaryListener implements LocalConnectListener{

    private final Map<ProxyCommand, LocalHandshakeMessageHandler<?>> messageHandlerMap;

    private final LocalMiddleServiceProxyFactory factory;

    private final Map<LocalConnect, LocalMiddleService> connectMap = new ConcurrentHashMap<>();


    @Override
    public void onCreate(LocalConnect local, TargetServer remoteServer, LocalMiddleService remote){
    }


    @Override
    public void onConnect(LocalConnect local, TargetServer remoteServer, LocalMiddleService remote){
        connectMap.put(local, remote);
    }


    @Override
    public void onCallbackError(LocalConnect local, LocalMiddleService remote, Throwable e){

    }


    @Override
    public void onLocalClose(LocalConnect context, LocalMiddleService remote){
        connectMap.remove(context);
    }


    @Override
    public void onError(LocalConnect context, LocalMiddleService connect, Throwable cause){

    }


    @Override
    public void onSendBinary(LocalConnect local, byte[] message, LocalMiddleService remote){

    }


    @Override
    public void onBinary(LocalConnect local, byte[] message, LocalMiddleService remote){

    }


    @Override
    public void onMessage(LocalConnect context, String text, LocalMiddleService localMiddleService){
        JSONObject object = JSON.parseObject(text);
        if(object == null){
            return;
        }
        String commandField = FieldNameUtils.getFieldName(ProxyMessage::getCommand);
        Integer commandCode = object.getInteger(commandField);
        ProxyCommand command = ServerProxyCommand.of(commandCode);
        LocalHandshakeMessageHandler<?> remoteMessageHandler = messageHandlerMap.get(command);
        ParameterizedType type = (ParameterizedType) remoteMessageHandler.getClass().getGenericInterfaces()[0];
        if(log.isDebugEnabled()){
            log.debug("handle receive message class - {}, command = {}", type, command);
        }
        remoteMessageHandler.handle(context, object.to(type.getActualTypeArguments()[0]),
                factory.getService(localMiddleService));
    }

}
