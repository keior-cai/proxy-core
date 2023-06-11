package com.socks.proxy.protocol.websocket;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.socks.proxy.protocol.LocalProxyConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.command.ProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
@AllArgsConstructor
public class WebsocketMessageListener extends WebSocketAdapter{

    private final LocalProxyConnect context;

    private final WebsocketPoolFactory websocketPoolFactory;

    private final Map<ProxyCommand, LocalHandshakeMessageHandler> messageHandlerMap;

    private ProxyCodes<? super ProxyMessage> codes;


    @Override
    public void onTextMessage(WebSocket websocket, String text){
        log.debug("receive text message = {}", text);
        ProxyMessage decode = codes.decode(text);
        log.debug("receive proxy message = {}", decode);
        ProxyCommand command = ServerProxyCommand.of(decode.getCommand());
        LocalHandshakeMessageHandler remoteMessageHandler = messageHandlerMap.get(command);
        remoteMessageHandler.handle(context, decode, new WebsocketProxyConnect(websocketPoolFactory, websocket, codes));
    }


    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary){
        context.write(binary);
    }


    @Override
    public void onError(WebSocket websocket, WebSocketException cause){
        log.error("", cause);
        if(websocket.isOpen()){
            websocketPoolFactory.invalidateObject(websocket);
        }
        context.close();
    }


    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame){
        websocket.sendPong();
    }


    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause){
        log.error("handle error", cause);
        websocketPoolFactory.returnClient(websocket);
        context.close();
    }


    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame){
        websocketPoolFactory.invalidateObject(websocket);
    }
}
