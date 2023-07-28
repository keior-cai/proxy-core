package com.socks.proxy.protocol.codes;

public interface ProxyCommandDecode<T extends ProxyMessage>{

    T decode(String str);

    String decodeStr(String str);
}
