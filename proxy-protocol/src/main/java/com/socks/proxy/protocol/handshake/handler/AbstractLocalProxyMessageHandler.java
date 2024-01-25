package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
import com.socks.proxy.protocol.handshake.message.SendUserMessage;
import com.socks.proxy.util.RSAUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * 客户端处理协议
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
public abstract class AbstractLocalProxyMessageHandler extends AbstractProxyMessageHandler{

    public AbstractLocalProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes<? super ProxyMessage> codes){
        super(rsaUtil, codes);
    }


    @Override
    public void handlerShakeEvent(ProxyConnect local, Map<String, Object> context){
        ProxyContext proxyContext = new ProxyContext();
        proxyContext.setCount(new CountDownLatch(1));
        putProxyContext(local, proxyContext);
    }


    @Override
    public void handleLocalBinaryMessage(ProxyConnect local, byte[] binary){
        ProxyContext proxyContext = getProxyContext(local);
        Optional.ofNullable(proxyContext).ifPresent(context->context.encodeWrite(binary));
    }


    @Override
    public void handleTargetBinaryMessage(ProxyConnect target, byte[] binary){
        try {
            ProxyContext proxyContext = getProxyContext(target);
            proxyContext.getCount().await();
            Optional.of(proxyContext).ifPresent(context->context.decodeWrite(binary));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void handleTargetClose(ProxyConnect target, String reason){
        remove(target, reason);
    }


    @Override
    public void handleLocalClose(ProxyConnect local, String reason){
        remove(local, reason);
    }


    /**
     * 本地创建与服务端连接
     */
    public abstract ProxyConnect serviceConnect(ProxyConnect local, TargetServer targetServer);


    @Override
    protected void handleServiceMessage(ProxyConnect connect, JSONObject msg, ServerProxyCommand command){
        ProxyContext proxyContext = getProxyContext(connect);
        switch(command) {
            case SEND_PUBLIC_KEY:
                String s = RandomStringUtils.randomAlphanumeric(10);
                proxyContext.setRandom(s);
                proxyContext.setCipher(CipherProvider.getByName("aes-256-cfb", s));
                connect.write(codes.encodeStr(
                        JSON.toJSONString(new SendUserMessage("aes-256-cfb", "test", "test", rsaUtil.encrypt(s)))));
                break;
            case CONNECT_SUCCESS:
                // 这里来处理连接成功问题
                proxyContext.getCount().countDown();
                break;
            case ACK_USER_MESSAGE:
                TargetServer server = proxyContext.getServer();
                connect.write(
                        codes.encodeStr(JSON.toJSONString(new SenTargetAddressMessage(server.host(), server.port()))));
                break;
            default:
            case UNKNOWN:
            case CLOSE:
                connect.close();
                break;
        }
    }


    @Override
    protected void handleLocalMessage(ProxyConnect connect, JSONObject msg, LocalProxyCommand command){
        throw new NoSuchMethodError();
    }
}
