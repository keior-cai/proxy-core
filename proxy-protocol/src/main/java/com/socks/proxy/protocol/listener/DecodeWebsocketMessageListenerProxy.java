package com.socks.proxy.protocol.listener;

import com.neovisionaries.ws.client.*;
import com.socks.proxy.protocol.codes.ProxyCommandDecode;
import com.socks.proxy.protocol.codes.ProxyMessage;

import java.util.List;
import java.util.Map;

/**
 * @author: chuangjie
 * @date: 2023/7/9
 **/
public class DecodeWebsocketMessageListenerProxy implements WebSocketListener{

    private final WebSocketListener delegate;

    private final ProxyCommandDecode<? super ProxyMessage> decode;


    public DecodeWebsocketMessageListenerProxy(WebSocketListener webSocketAdapter,
                                               ProxyCommandDecode<? super ProxyMessage> decode){
        this.delegate = webSocketAdapter;
        this.decode = decode;
    }


    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception{
        delegate.onStateChanged(websocket, newState);
    }


    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception{
        delegate.onConnected(websocket, headers);
    }


    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception{
        delegate.onConnectError(websocket, cause);

    }


    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                               boolean closedByServer) throws Exception{
        delegate.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

    }


    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onFrame(websocket, frame);
    }


    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onContinuationFrame(websocket, frame);

    }


    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onTextFrame(websocket, frame);
    }


    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onBinaryFrame(websocket, frame);
    }


    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onCloseFrame(websocket, frame);
    }


    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onPingFrame(websocket, frame);
    }


    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onPongFrame(websocket, frame);
    }


    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception{
        delegate.onTextMessage(websocket, decode.decodeStr(text));
    }


    @Override
    public void onTextMessage(WebSocket websocket, byte[] data) throws Exception{
        delegate.onTextMessage(websocket, data);
    }


    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception{
        delegate.onBinaryMessage(websocket, binary);
    }


    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onSendingFrame(websocket, frame);
    }


    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onFrameSent(websocket, frame);
    }


    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception{
        delegate.onFrameUnsent(websocket, frame);
    }


    @Override
    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception{
        delegate.onThreadCreated(websocket, threadType, thread);
    }


    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception{
        delegate.onThreadStarted(websocket, threadType, thread);
    }


    @Override
    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception{
        delegate.onThreadStopping(websocket, threadType, thread);
    }


    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception{
        delegate.onError(websocket, cause);
    }


    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception{
        delegate.onFrameError(websocket, cause, frame);
    }


    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames)
            throws Exception{
        delegate.onMessageError(websocket, cause, frames);

    }


    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed)
            throws Exception{
        delegate.onMessageDecompressionError(websocket, cause, compressed);

    }


    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception{
        delegate.onTextMessageError(websocket, cause, data);
    }


    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception{
        delegate.onSendError(websocket, cause, frame);
    }


    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception{
        delegate.onUnexpectedError(websocket, cause);
    }


    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception{
        delegate.handleCallbackError(websocket, cause);
    }


    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception{
        delegate.onSendingHandshake(websocket, requestLine, headers);
    }
}
