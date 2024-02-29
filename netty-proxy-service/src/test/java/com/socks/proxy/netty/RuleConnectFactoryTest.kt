package com.socks.proxy.netty

import com.socks.proxy.handshake.WebsocketProxyConnectFactory
import com.socks.proxy.netty.connect.DirectConnectFactory
import com.socks.proxy.protocol.factory.RuleLocalConnectServerFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RuleConnectFactoryTest {

    @Test
    fun testRule() {

        val proxy = WebsocketProxyConnectFactory.createDefault("ws://chuangjie.icu")
        val direct = DirectConnectFactory.INSTANCE
        val factory = RuleLocalConnectServerFactory(
            direct,
            proxy
        )
        factory.addDomain("baidu.com", DirectConnectFactory.INSTANCE)
        factory.addDomain("google.com", proxy)
        factory.addDomain("openai.com", proxy)

        factory.domainRule("www.google.com")?.let {
            Assertions.assertTrue(it[0] == proxy)
            println(it[0])
        }
        factory.domainRule("google.com")?.let {
            Assertions.assertTrue(it[0] == proxy)
            println(it[0])
        }

        factory.domainRule("www.baidu.com")?.let {
            Assertions.assertTrue(it[0] == direct)
            println(it[0])
        }
        factory.domainRule("baidu.com")?.let {
            Assertions.assertTrue(it[0] == direct)
            println(it[0])
        }
    }
}
