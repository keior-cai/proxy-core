package com.socks.proxy.handshake.constant;

import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.TargetConnect;
import io.netty.util.AttributeKey;

public interface WebsocketAttrConstant{

    AttributeKey<ICipher> CIPHER = AttributeKey.valueOf("cipher");

    AttributeKey<TargetConnect> TARGET = AttributeKey.valueOf("target");

}
