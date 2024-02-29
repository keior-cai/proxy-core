package com.socks.proxy.netty.client

import java.io.InputStream

interface ClientMessageCallback {

    fun read(ins: InputStream)

}
