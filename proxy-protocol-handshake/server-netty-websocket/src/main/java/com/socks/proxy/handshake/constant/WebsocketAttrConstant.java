package com.socks.proxy.handshake.constant;

import com.socks.proxy.protocol.ICipher;
import com.socks.proxy.protocol.TargetServer;
import io.netty.util.AttributeKey;

public interface WebsocketAttrConstant{

    /**
     * 目标地址
     */
    AttributeKey<TargetServer> DST_SERVER = AttributeKey.valueOf("dstServer");


    AttributeKey<ICipher> CIPHER = AttributeKey.valueOf("cipher");

}
