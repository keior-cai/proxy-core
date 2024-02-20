package com.socks.proxy.netty.http;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpHandle{

    void get(FullHttpRequest request, FullHttpResponse response);


    default void post(FullHttpRequest request, FullHttpResponse response){
        get(request, response);
    }


    default void delete(FullHttpRequest request, FullHttpResponse response){
        get(request, response);
    }
}
