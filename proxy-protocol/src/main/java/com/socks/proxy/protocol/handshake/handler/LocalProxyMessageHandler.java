package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ConnectProxyConnect;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.ConnectType;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.factory.ProxyFactory;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.message.PublicKeyMessage;
import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
import com.socks.proxy.protocol.handshake.message.SendUserMessage;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.RSAUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * 客户端处理协议
 *
 * @author: chuangjie
 * @date: 2024/1/25
 **/
@Slf4j
@Setter
public class LocalProxyMessageHandler extends AbstractProxyMessageHandler{

    /**
     * 代理工厂集合
     */
    private ProxyFactory factory;


    public LocalProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes codes, ConnectContextManager manager){
        super(rsaUtil, codes, manager);
    }


    @Override
    protected void handelProxyMessage(ProxyConnect connect, int commandValue, JSONObject msg){
        ServerProxyCommand command = ServerProxyCommand.of(commandValue);
        log.debug("receive service value = {} command = {}", commandValue, command);
        ProxyContext proxyContext = manager.getContext(connect);
        switch(command) {
            case SEND_PUBLIC_KEY:
                PublicKeyMessage message = msg.toJavaObject(PublicKeyMessage.class);
                String s = RandomStringUtils.randomAlphanumeric(10);
                ProxyInfo proxyInfo = proxyContext.getProxyInfo();
                proxyInfo.setRandom(s);
                AbstractCipher cipher = CipherProvider.getByName("aes-256-cfb", s);
                proxyInfo.setCipher(cipher);
                connect.write(codes.encodeStr(JSON.toJSONString(new SendUserMessage("aes-256-cfb", "test", "test",
                        AESUtil.encryptByDefaultKey(rsaUtil.encrypt(s, message.getPublicKey()))))));
                break;
            case CONNECT_SUCCESS:
                // 这里来处理连接成功问题
                proxyContext.getProxyInfo().getCount().countDown();
                log.debug("now send message byte to target");
                break;
            case ACK_USER_MESSAGE:
                TargetServer server = proxyContext.getProxyInfo().getServer();
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
    public void handlerShakeEvent(ProxyConnect local, Map<String, Object> context){
        ProxyContext proxyContext = new ProxyContext();
        proxyContext.getProxyInfo().setCount(new CountDownLatch(1));
        manager.putLocalConnect(local, proxyContext);
    }


    @Override
    public void handleLocalBinaryMessage(ProxyConnect local, byte[] binary){
        ProxyContext proxyContext = manager.getContext(local);
        if(proxyContext == null){
            // 这里说明远程连接已经关闭了，需要重新连接;直接断开本地连接
            local.close();
            return;
        }
        if(Objects.equals(proxyContext.getConnect().type(), ConnectType.PROXY)){
            Optional.of(proxyContext).ifPresent(context->context.encodeWrite(binary));
        } else {
            Optional.of(proxyContext).ifPresent(context->context.write(binary));
        }
    }


    @Override
    public void handleTargetBinaryMessage(ProxyConnect target, byte[] binary){
        ProxyContext proxyContext = manager.getContext(target);
        if(proxyContext == null){
            // 这说明本地客户端已经断开连接了，不需要发送数据;关闭代理连接
            target.close();
            return;
        }
        if(Objects.equals(target.type(), ConnectType.PROXY)){
            Optional.of(proxyContext).ifPresent(context->context.decodeWrite(binary));
        } else {
            Optional.of(proxyContext).ifPresent(context->context.write(binary));
        }

    }


    /**
     * 本地创建与服务端连接
     */
    @Override
    public ProxyConnect targetConnect(ProxyConnect local, TargetServer target){
        ProxyContext proxyContext = manager.getContext(local);
        ProxyInfo proxyInfo = proxyContext.getProxyInfo();
        proxyInfo.setServer(target);
        try {
            ConnectProxyConnect targetConnect = factory.create(target, this);
            proxyContext.setConnect(targetConnect);
            ProxyContext targetContext = new ProxyContext();
            targetContext.setProxyInfo(proxyInfo);
            targetContext.setConnect(local);
            targetConnect.connect();
            if(Objects.equals(targetConnect.type(), ConnectType.DIRECT)){
                // 直接连接不需要等待，直接发送数据
                proxyContext.getProxyInfo().getCount().countDown();
            }
            manager.putTargetConnect(targetConnect, targetContext);
            proxyContext.getProxyInfo().getCount().await();
            return targetConnect;
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
}
