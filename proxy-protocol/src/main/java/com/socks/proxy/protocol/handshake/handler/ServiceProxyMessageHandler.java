package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.DefaultTargetServer;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.LocalProxyCommand;
import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
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
public class ServiceProxyMessageHandler extends AbstractProxyMessageHandler{

    private final ProxyFactory factory;


    public ServiceProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes codes, ConnectContextManager manager,
                                      ProxyFactory factory){
        super(rsaUtil, codes, manager);
        this.factory = factory;
    }


    @Override
    protected void handelProxyMessage(ProxyConnect connect, int commandValue, JSONObject msg){
        LocalProxyCommand command = LocalProxyCommand.of(commandValue);
        log.debug("receive local commandValue = {} command = {}", commandValue, command);
        ProxyContext proxyContext = manager.getContext(connect);
        switch(command) {
            case SEND_USER_INFO:
                SendUserMessage sendUserMessage = msg.toJavaObject(SendUserMessage.class);
                String decrypt = rsaUtil.decrypt(AESUtil.decryptByDefaultKey(sendUserMessage.getRandom()));
                proxyContext.getProxyInfo().setCipher(CipherProvider.getByName(sendUserMessage.getMethod(), decrypt));
                proxyContext.getProxyInfo().setRandom(decrypt);
                connect.write(codes.encodeStr(JSON.toJSONString(new AckUserMessage())));
                break;
            case SEND_DST_ADDR:
                SenTargetAddressMessage addrMessage = msg.toJavaObject(SenTargetAddressMessage.class);
                ProxyConnect proxyConnect = targetConnect(connect,
                        new DefaultTargetServer(addrMessage.getHost(), addrMessage.getPort(), Protocol.UNKNOWN));

                proxyContext.setConnect(proxyConnect);
                proxyContext.getProxyInfo().setServer(
                        new DefaultTargetServer(addrMessage.getHost(), addrMessage.getPort(), Protocol.UNKNOWN));
                ProxyContext localContext = new ProxyContext();
                localContext.setProxyInfo(proxyContext.getProxyInfo());
                localContext.setConnect(connect);
                // 正方向维护
                manager.putTargetConnect(proxyConnect, localContext);
                connect.write(codes.encodeStr(JSON.toJSONString(new AckTargetAddressMessage())));
                break;
            case CLOSE:
                handleLocalClose(connect, new Exception("服务端发送关闭连接命令"));
                break;
            default:
            case UNKNOWN:
                connect.close();
                break;
        }
    }


    @Override
    public void handlerShakeEvent(ProxyConnect local, Map<String, Object> context){
        local.write(codes.encodeStr(JSON.toJSONString(new PublicKeyMessage(rsaUtil.getPublicKey()))));
        manager.putLocalConnect(local, new ProxyContext());
    }


    @Override
    public void handleLocalBinaryMessage(ProxyConnect local, byte[] binary){
        ProxyContext proxyContext = manager.getContext(local);
        Optional.ofNullable(proxyContext).ifPresent(context->context.decodeWrite(binary));
    }


    @Override
    public void handleTargetBinaryMessage(ProxyConnect target, byte[] binary){
        ProxyContext proxyContext = manager.getContext(target);
        Optional.ofNullable(proxyContext).ifPresent(context->context.encodeWrite(binary));
    }


    @Override
    public ProxyConnect targetConnect(ProxyConnect local, TargetServer target){
        try {
            return factory.create(target, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
