package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.message.*;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.RSAUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * 服务协议处理消息
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@Getter
@Slf4j
public abstract class AbstractServiceProxyMessageHandler extends AbstractProxyMessageHandler{

    public AbstractServiceProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes<? super ProxyMessage> codes){
        super(rsaUtil, codes);
    }


    @Override
    public void handlerShakeEvent(ProxyConnect local, Map<String, Object> context){
        local.write(codes.encodeObject(new PublicKeyMessage(AESUtil.encryptByDefaultKey(rsaUtil.getPublicKey()))));
        putProxyContext(local, new ProxyContext());
    }


    @Override
    public void handleLocalBinaryMessage(ProxyConnect local, byte[] binary){
        ProxyContext proxyContext = getProxyContext(local);
        Optional.ofNullable(proxyContext).ifPresent(context->context.decodeWrite(binary));
    }


    @Override
    public void handleTargetBinaryMessage(ProxyConnect target, byte[] binary){
        ProxyContext proxyContext = getProxyContext(target);
        Optional.ofNullable(proxyContext).ifPresent(context->context.encodeWrite(binary));
    }


    @Override
    protected void handleLocalMessage(ProxyConnect connect, JSONObject msg, LocalProxyCommand command){
        ProxyContext proxyContext = getProxyContext(connect);
        switch(command) {
            case SEND_USER_INFO:
                SendUserMessage sendUserMessage = msg.toJavaObject(SendUserMessage.class);
                String decrypt = rsaUtil.decrypt(sendUserMessage.getRandom());
                proxyContext.setCipher(
                        CipherProvider.getByName(sendUserMessage.getMethod(), decrypt));
                proxyContext.setRandom(decrypt);
                connect.write(codes.encodeStr(JSON.toJSONString(new AckUserMessage())));
                break;
            case SEND_DST_ADDR:
                SenTargetAddressMessage addrMessage = msg.toJavaObject(SenTargetAddressMessage.class);
                ProxyConnect proxyConnect = targetConnect(addrMessage.getHost(), addrMessage.getPort());
                proxyContext.setConnect(proxyConnect);
                ProxyContext localContext = new ProxyContext();
                localContext.setCipher(proxyContext.getCipher());
                localContext.setConnect(connect);
                // 正方向维护
                putProxyContext(proxyConnect, localContext);
                connect.write(codes.encodeStr(JSON.toJSONString(new AckTargetAddressMessage())));
                break;
            case CLOSE:
                handleLocalClose(connect, "服务端发送关闭连接命令");
                break;
            default:
            case UNKNOWN:
                connect.close();
                break;
        }
    }


    @Override
    protected void handleServiceMessage(ProxyConnect connect, JSONObject msg, ServerProxyCommand command){
        throw new NoSuchMethodError();
    }


    /**
     * 服务端创建与目标服务连接
     *
     * @param host 目标服务地址
     * @param port 目标服务端口
     * @return 目标服务连接
     */
    protected abstract ProxyConnect targetConnect(String host, int port);
}
