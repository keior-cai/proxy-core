package com.socks.proxy.protocol;

import com.socks.proxy.protocol.codes.ProxyCommandEncode;
import com.socks.proxy.protocol.codes.ProxyMessage;
import lombok.AllArgsConstructor;

/**
 * @author: chuangjie
 * @date: 2023/7/9
 **/
@AllArgsConstructor
public class EncodeLocalMiddleServiceProxyFactory implements LocalMiddleServiceProxyFactory{

    private ProxyCommandEncode<? super ProxyMessage> encode;


    @Override
    public LocalMiddleService getService(LocalMiddleService service){
        return new EncodeLocalMiddleServiceProxy(service, encode);
    }
}
