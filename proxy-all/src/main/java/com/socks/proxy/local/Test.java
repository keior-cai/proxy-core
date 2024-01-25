package com.socks.proxy.local;

import com.socks.proxy.netty.local.LocalServiceBuilder;
import com.socks.proxy.service.DefaultLocalServiceBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Collections;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Slf4j
public class Test{
    public static void main(String[] args) throws Exception{
        LocalServiceBuilder localServiceBuilder = new DefaultLocalServiceBuilder().setServerList(
                Collections.singletonList(new URI("ws://chuangjie.icu:8041"))).setPort(1082);
        localServiceBuilder.builder().start();
    }
}
