package com.socks.proxy.netty.client

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBufUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultFullHttpRequest
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.socksx.v5.*
import io.netty.handler.logging.ByteBufFormat
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import lombok.extern.slf4j.Slf4j
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit

@Slf4j
class Socks5HttpClient(
    val dstAddr: String, val dstPort: Int,
    val callback: ClientMessageCallback = object :
        ClientMessageCallback {
        override fun read(ins: InputStream) {
            val readLines = InputStreamReader(ins)
                .readLines()
            readLines.stream().forEach { println(it) }
        }
    },
) {
    private val bootstrap : Bootstrap = Bootstrap()
        .handler(LoggingHandler(LogLevel.DEBUG, ByteBufFormat.HEX_DUMP))
        .channel(NioSocketChannel::class.java)
        .group(NioEventLoopGroup())

    fun connect(host: String, port: Int):Channel {
        val connect = bootstrap.connect(host, port)
        connect.channel()
            .pipeline()
            .addLast(Socks5ClientEncoder(Socks5AddressEncoder.DEFAULT))
            .addLast(Socks5InitialResponseDecoder())
            .addLast(object : ChannelInitializer<Channel>() {
                override fun initChannel(p0: Channel?) {
                    p0.let {
                        it?.pipeline()?.remove(this)
                        p0?.writeAndFlush(DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH))
                    }
                }
            })
            .addLast(object : SimpleChannelInboundHandler<Socks5InitialResponse>() {
                override fun channelRead0(p0: ChannelHandlerContext?, p1: Socks5InitialResponse?) {
                    p0?.pipeline()?.remove(this)
                        ?.addLast(Socks5CommandResponseDecoder())
                        ?.addLast(object : SimpleChannelInboundHandler<Socks5CommandResponse>() {
                            override fun channelRead0(ctx: ChannelHandlerContext?, msg: Socks5CommandResponse?) {
                                if (Objects.equals(msg?.status(), Socks5CommandStatus.SUCCESS)) {
                                    ctx?.channel()?.pipeline()?.let {
                                        it.remove(this)
                                            .remove(Socks5ClientEncoder::class.java)
                                        it.remove(Socks5CommandResponseDecoder::class.java)
                                        it.addLast(HttpClientCodec())
                                            .addLast(object : SimpleChannelInboundHandler<FullHttpResponse>() {
                                                override fun channelRead0(ctx: ChannelHandlerContext?, msg: FullHttpResponse?) {
                                                    msg?.let {
                                                        callback.read(ByteArrayInputStream(ByteBufUtil.getBytes(msg.content())))
                                                    }
                                                }

                                                override fun channelInactive(ctx: ChannelHandlerContext?) {
                                                    println("关闭连接1")
                                                    super.channelInactive(ctx)
                                                }

                                                override fun channelUnregistered(ctx: ChannelHandlerContext?) {
                                                    println("关闭连接2")
                                                    super.channelUnregistered(ctx)
                                                }
                                            })
                                    }
                                    ctx?.channel()?.writeAndFlush(DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"))
                                }
                            }
                        })
                        ?.remove(Socks5InitialResponseDecoder::class.java)
                    p0?.channel()?.writeAndFlush(
                        DefaultSocks5CommandRequest(
                            Socks5CommandType.CONNECT,
                            Socks5AddressType.DOMAIN,
                            dstAddr,
                            dstPort
                        )
                    )
                }
            })
        connect.sync().await(3, TimeUnit.SECONDS)
        return connect.channel()
    }
}
