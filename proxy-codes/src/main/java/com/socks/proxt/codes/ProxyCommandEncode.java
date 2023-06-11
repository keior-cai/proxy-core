package com.socks.proxt.codes;

public interface ProxyCommandEncode<T>{

    String encodeObject(T message);

    String encodeStr(String message);
}
