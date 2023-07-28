package com.socks.proxy.protocol;

import com.socks.proxy.protocol.enums.ConnectStatus;
import com.socks.proxy.protocol.listener.LocalConnectListener;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author: chuangjie
 * @date: 2023/7/8
 **/
@Slf4j
public class DirectLocalMiddleProxy implements LocalMiddleService{

    private final Socket socket;

    private volatile ConnectStatus status;

    private final TargetServer server;

    private final String channelId = UUID.randomUUID().toString();

    private final List<LocalConnectListener> listenerList;

    private Thread thread;

    private final LocalConnect localConnect;


    public DirectLocalMiddleProxy(LocalConnect channel, List<LocalConnectListener> listenerList,
                                  TargetServer targetServer){
        this.socket = new Socket();
        status = ConnectStatus.CREATED;
        this.listenerList = listenerList;
        for(LocalConnectListener listener : listenerList) {
            listener.onCreate(channel, targetServer, this);
        }
        this.server = targetServer;
        this.localConnect = channel;
    }


    @Override
    public void write(String message){
        writeBuff(message.getBytes());
    }


    @Override
    public void connect() throws Exception{
        status = ConnectStatus.CONNECTING;
        socket.connect(new InetSocketAddress(server.host(), server.port()));
        status = ConnectStatus.OPEN;
        for(LocalConnectListener listener : listenerList) {
            listener.onConnect(localConnect, server, this);
        }
        thread = new Thread(()->{
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    InputStream inputStream = socket.getInputStream();
                    int available = inputStream.available();
                    byte[] bytes = new byte[available];
                    int read = inputStream.read(bytes);
                    if(read == -1){
                        continue;
                    }
                    for(LocalConnectListener listener : listenerList) {
                        listener.onBinary(localConnect, bytes, this);
                    }
                } catch (IOException e) {
                    for(LocalConnectListener listener : listenerList) {
                        listener.onError(localConnect, this, e);
                    }
                    close();
                }
            }
        });
        thread.start();
    }


    @Override
    public ConnectStatus status(){
        return status;
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
            thread.interrupt();
            socket.close();
        } catch (IOException e) {
            // ignore
        }
        status = ConnectStatus.CLOSED;
        for(LocalConnectListener listener : listenerList) {
            listener.onLocalClose(localConnect, this);
        }
    }


    private void writeBuff(byte[] content){
        log.debug("{}", status);
        if(Objects.equals(status, ConnectStatus.OPEN)){
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(content);
                log.debug("WRITE");
                outputStream.flush();
                log.debug("FLUSH");
            } catch (IOException e) {
                for(LocalConnectListener listener : listenerList) {
                    listener.onError(localConnect, this, e);
                }
            }
        }
    }
}
