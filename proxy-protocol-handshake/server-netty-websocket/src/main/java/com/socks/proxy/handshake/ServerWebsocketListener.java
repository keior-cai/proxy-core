package com.socks.proxy.handshake;

import com.socks.proxy.protocol.ServerMiddleProxy;
import com.socks.proxy.protocol.TargetConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.handshake.ServerHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.message.PublicKeyMessage;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/6/5
 **/
@Slf4j
@AllArgsConstructor
public class ServerWebsocketListener extends AdaptorMessageListener{

    private final Map<Class<? extends ProxyMessage>, List<ServerHandshakeMessageHandler>> handlerMap;

    private final ProxyCodes<? super ProxyMessage> codes;

    private final String publicKey;


    @Override
    public void onConnect(ServerMiddleProxy proxy, Map<String, String> headers, String s, String selectedSubprotocol){
        try {
            String encode = codes.encodeObject(new PublicKeyMessage(publicKey));
            log.debug("send public key message = {}", encode);
            proxy.write(encode);
        } catch (Throwable cause) {
            onCallbackError(proxy, cause);
        }
    }


    @Override
    public void onText(ServerMiddleProxy proxy, String message){
        if(StringUtil.isNullOrEmpty(message)){
            log.error("socket message is empty now skip this message and return.");
            return;
        }
        log.debug("receive text message = {}", message);
        ProxyMessage proxyMessage = codes.decode(message);
        log.debug("receive text message = {}", proxyMessage);
        Class<? extends ProxyMessage> clazz = proxyMessage.getClass();
        List<ServerHandshakeMessageHandler> handlers = handlerMap.get(proxyMessage.getClass());
        if(handlers == null || handlers.isEmpty()){
            log.warn("proxy class {}, handler is empty.", clazz);
            return;
        }
        TargetConnect target = proxy.getTarget();
        for(ServerHandshakeMessageHandler handler : handlers) {
            handler.handle(proxy, proxyMessage, target);
        }
    }


    @Override
    public void onBinary(ServerMiddleProxy proxy, byte[] content){
        log.debug("receive local byte size = {}", content.length);
        proxy.write(content);
    }


    @Override
    public void onClose(ServerMiddleProxy proxy, int status, String reason){
        proxy.getTarget().close();
    }


    @Override
    public void onError(ServerMiddleProxy proxy, Throwable cause){
        proxy.getTarget().close();
        proxy.close();
    }


    @Override
    public void onCallbackError(ServerMiddleProxy proxy, Throwable cause){
        log.error("on callback error message = {}", cause.getMessage());
        proxy.getTarget().close();
    }


    @Override
    public void onDisconnect(ServerMiddleProxy proxy){
        log.debug("channel close...");
        proxy.getTarget().close();
    }
}
