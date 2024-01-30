package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.util.FieldNameUtils;
import com.socks.proxy.util.RSAUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认协议，协议处理消息
 * <pre>
 *     1、客户端连接到服务器之后，会先发送一个证书（public key）；这里暂时先简单的使用public key
 *     2、客户端收到public key 之后就会生成一个随机数；
 *        把加密方式，加密随机数。通过public key 加密之后发送到服务端
 *     3、服务端收到客户端发送过来的随机数，使用private key解密，解密之后在连接维护一个解密上下文
 *     4、服务端通知客户端密码解密成功，客户端发送连接信息给到服务端
 *     5、服务端收到连接信息之后，创建连接目标服务器上下文；并通知客户端连接成功
 *     6、客户端发送加密请求数据
 *     7、服务端转发请求数据。并等待响应数据
 *     .......
 * </pre>
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@Slf4j
@AllArgsConstructor
public abstract class AbstractProxyMessageHandler implements ProxyMessageHandler{

    protected final RSAUtil rsaUtil;

    protected final ProxyCodes codes;

    @Getter
    private final Map<String, ProxyContext> contextMap = new ConcurrentHashMap<>();


    @Override
    public void handleLocalTextMessage(ProxyConnect local, String text){
        JSONObject msg = JSON.parseObject(codes.decodeStr(text));
        if(log.isDebugEnabled()){
            log.debug("receive message = {}", msg);
        }
        int commandValue = msg.getIntValue(FieldNameUtils.getFieldName(ProxyMessage::getCommand));
        handelProxyMessage(local, commandValue, msg);
    }


    abstract protected void handelProxyMessage(ProxyConnect connect, int commandValue, JSONObject msg);


    @Override
    public void handleTargetClose(ProxyConnect target, String reason){
        ProxyContext proxyContext = contextMap.get(target.channelId());
        contextMap.remove(target.channelId());
        if(Objects.nonNull(proxyContext)){
            Optional.ofNullable(proxyContext.getConnect()).map(ProxyConnect::channelId).ifPresent(contextMap::remove);
        }
    }


    @Override
    public void handleLocalClose(ProxyConnect local, String reason){
        if(log.isDebugEnabled()){
            log.debug("remove connect = {}， reason = {}", local.channelId(), reason);
        }
        ProxyContext proxyContext = contextMap.get(local.channelId());
        contextMap.remove(local.channelId());
        if(Objects.nonNull(proxyContext)){
            Optional.ofNullable(proxyContext.getConnect()).map(ProxyConnect::channelId).ifPresent(contextMap::remove);
        }
    }


    protected ProxyContext getProxyContext(ProxyConnect connect){
        return contextMap.get(connect.channelId());
    }


    protected void remove(ProxyConnect connect, String reason){
        Optional<String> optional = Optional.ofNullable(connect).map(ProxyConnect::channelId);
        if(optional.isPresent()){
            log.debug("connect Id = {} remove reason = {}", connect.channelId(), reason);
            ProxyContext remove = contextMap.remove(optional.get());
            if(Objects.nonNull(remove)){
                Optional.ofNullable(remove.getConnect()).map(ProxyConnect::channelId).ifPresent(contextMap::remove);
            }
        } else {
            log.debug("remove connect Id is empty.");
        }
    }


    protected void putProxyContext(ProxyConnect connect, ProxyContext context){
        contextMap.put(connect.channelId(), context);
    }
}
