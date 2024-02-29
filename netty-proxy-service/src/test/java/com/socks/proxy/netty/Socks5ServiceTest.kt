package com.socks.proxy.netty

import com.socks.proxy.netty.client.Socks5HttpClient
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Slf4j
class Socks5ServiceTest {

    @Test
    fun socks5ProtocolTest() {
        val newCachedThreadPool = Executors.newCachedThreadPool()
        for (i in 1..100) {
            newCachedThreadPool.submit {
                Socks5HttpClient("www.baidu.com", 80)
                    .connect("127.0.0.1", 1089)
            }
        }
        TimeUnit.SECONDS.sleep(30)
        newCachedThreadPool.shutdown()
    }
}
