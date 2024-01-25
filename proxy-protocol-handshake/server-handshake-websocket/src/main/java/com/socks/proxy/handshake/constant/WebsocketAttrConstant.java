package com.socks.proxy.handshake.constant;

import com.socks.proxy.protocol.ICipher;
import io.netty.util.AttributeKey;

public interface WebsocketAttrConstant{

    AttributeKey<ICipher> CIPHER = AttributeKey.valueOf("cipher");


}
