package com.socks.proxy.protocol;

import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.ConnectStatus;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.UUID;

/**
 * @author: chuangjie
 * @date: 2023/7/8
 **/
@Slf4j
public class DirectLocalMiddleProxy implements ProxyConnect{

    private volatile ConnectStatus status;

    private final String channelId = UUID.randomUUID().toString();

    private final SocketChannel socketChannel;


    public DirectLocalMiddleProxy(SocketChannel socketChannel){
        this.socketChannel = socketChannel;
        status = ConnectStatus.CREATED;
    }


    @Override
    public void write(String message){
        writeBuff(message.getBytes());
    }


    @Override
    public String channelId(){
        return channelId;
    }


    @Override
    public void write(byte[] content){
        writeBuff(content);
    }


    @Override
    public void close(){
        try {
            socketChannel.close();
        } catch (IOException e) {
            // ignore
        }
        status = ConnectStatus.CLOSED;
    }


    private void writeBuff(byte[] content){
        if(Objects.equals(status, ConnectStatus.OPEN)){
            try {
                socketChannel.write(ByteBuffer.wrap(content));
                log.debug("FLUSH");
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }
}
