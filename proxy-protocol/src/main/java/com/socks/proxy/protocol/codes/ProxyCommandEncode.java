package com.socks.proxy.protocol.codes;

public interface ProxyCommandEncode<T extends ProxyMessage>{

    String encodeObject(T message);


    String encodeStr(String message);
}
