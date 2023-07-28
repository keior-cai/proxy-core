package com.socks.proxy.protocol.listener;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.websocket.LocalWebsocketProxyConnect;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
@AllArgsConstructor
public class WebsocketMessageListener extends WebSocketAdapter{

    private final LocalConnect context;

    private final List<LocalConnectListener> listeners;


    @Override
    public void onTextMessage(WebSocket websocket, String text){
        for(LocalConnectListener listener : listeners) {
            listener.onMessage(context, text, new LocalWebsocketProxyConnect(websocket));
        }
    }


    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary){
        for(LocalConnectListener listener : listeners) {
            listener.onBinary(context, binary, new LocalWebsocketProxyConnect(websocket));
        }
        byte[] decode = context.getCipher().decode(binary);
        context.write(decode);
    }


    @Override
    public void onError(WebSocket websocket, WebSocketException cause){
        for(LocalConnectListener listener : listeners) {
            listener.onError(context, new LocalWebsocketProxyConnect(websocket), cause);
        }
    }


    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame){
        websocket.sendPong();
    }


    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause){
        for(LocalConnectListener listener : listeners) {
            listener.onCallbackError(context, new LocalWebsocketProxyConnect(websocket), cause);
        }
    }


    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame){
        for(LocalConnectListener listener : listeners) {
            listener.onLocalClose(context, new LocalWebsocketProxyConnect(websocket));
        }
        //        context.close();
    }
}
