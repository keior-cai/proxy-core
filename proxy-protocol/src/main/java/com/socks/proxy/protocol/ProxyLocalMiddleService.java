package com.socks.proxy.protocol;

import com.socks.proxy.protocol.enums.ConnectStatus;
import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2023/7/10
 **/
@AllArgsConstructor
public class ProxyLocalMiddleService implements LocalMiddleService{

    private final LocalMiddleService localMiddleService;


    @Override
    public void write(String message){
        localMiddleService.write(message);
    }


    @Override
    public void connect() throws Exception{
        localMiddleService.connect();
    }


    @Override
    public ConnectStatus status(){
        return localMiddleService.status();
    }


    @Override
    public String channelId(){
        return localMiddleService.channelId();
    }


    @Override
    public void write(byte[] content){
        localMiddleService.write(content);
    }


    @Override
    public void close(){
        localMiddleService.close();
    }
}
