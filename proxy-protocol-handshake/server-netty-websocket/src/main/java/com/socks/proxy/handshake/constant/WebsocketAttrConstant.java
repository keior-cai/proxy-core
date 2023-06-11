package com.socks.proxy.handshake.constant;

import com.socks.proxy.protocol.DstServer;
import io.netty.util.AttributeKey;

public interface WebsocketAttrConstant{

    /**
     * 目标地址
     */
    AttributeKey<DstServer> DST_SERVER = AttributeKey.valueOf("dstServer");

}
