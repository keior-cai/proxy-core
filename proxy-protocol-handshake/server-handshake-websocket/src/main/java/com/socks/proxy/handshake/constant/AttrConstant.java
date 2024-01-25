package com.socks.proxy.handshake.constant;

import io.netty.util.AttributeKey;

public interface AttrConstant{

    AttributeKey<String> CHANNEL_ID = AttributeKey.valueOf("Id");
}
