package com.socks.proxy.protocol.codes;

public interface ProxyCodes<T extends ProxyMessage> extends ProxyCommandEncode<T>, ProxyCommandDecode<T>{
}
