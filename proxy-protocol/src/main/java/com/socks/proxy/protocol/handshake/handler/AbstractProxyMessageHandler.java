package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.util.FieldNameUtils;
import com.socks.proxy.util.RSAUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    protected final ConnectContextManager manager;


    @Override
    public void handleLocalTextMessage(ProxyConnect local, String text){
        JSONObject msg = JSON.parseObject(codes.decodeStr(text));
        if(log.isDebugEnabled()){
            log.debug("receive message = {}", msg);
        }
        int commandValue = msg.getIntValue(FieldNameUtils.getFieldName(ProxyMessage::getCommand));
        try {
            handelProxyMessage(local, commandValue, msg);
        } catch (Exception e) {
            throw new Error(e);
        }
    }


    abstract protected void handelProxyMessage(ProxyConnect connect, int commandValue, JSONObject msg) throws Exception;


    @Override
    public void handleTargetClose(ProxyConnect target, String reason){
        if(log.isDebugEnabled()){
            log.debug("remove target connect = {}， reason = {}", target.channelId(), reason);
        }
        manager.removeAll(target);
    }


    @Override
    public void handleLocalClose(ProxyConnect local, String reason){
        if(log.isDebugEnabled()){
            log.debug("remove local connect = {}， reason = {}", local.channelId(), reason);
        }
        manager.removeAll(local);
    }
}
