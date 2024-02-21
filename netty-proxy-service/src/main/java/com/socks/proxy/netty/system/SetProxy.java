package com.socks.proxy.netty.system;

public interface SetProxy{


    void turnOnProxy(String host, int port);

    default void turnOnProxy(int port){
        turnOnProxy("127.0.0.1", port);
    }

    void turnOffProxy();
}
