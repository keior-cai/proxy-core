package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
import com.socks.proxy.protocol.handshake.message.SendUserMessage;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public abstract class AbstractLocalProxyMessageHandler extends AbstractProxyMessageHandler{

    public AbstractLocalProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes codes, ConnectContextManager manager){
        super(rsaUtil, codes, manager);
    }


    @Override
    protected void handelProxyMessage(ProxyConnect connect, int commandValue, JSONObject msg){
        ServerProxyCommand command = ServerProxyCommand.of(commandValue);
        log.debug("receive service value = {} command = {}", commandValue, command);
        ProxyContext proxyContext = manager.getContext(connect);
        switch(command) {
            case SEND_PUBLIC_KEY:
                String s = RandomStringUtils.randomAlphanumeric(10);
                ProxyInfo proxyInfo = proxyContext.getProxyInfo();
                proxyInfo.setRandom(s);
                AbstractCipher cipher = CipherProvider.getByName("aes-256-cfb", s);
                proxyInfo.setCipher(cipher);
                connect.write(codes.encodeStr(JSON.toJSONString(new SendUserMessage("aes-256-cfb", "test", "test",
                        AESUtil.encryptByDefaultKey(rsaUtil.encrypt(s))))));
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
        try {
            ProxyContext proxyContext = manager.getContext(local);
            proxyContext.getProxyInfo().getCount().await();
            Optional.of(proxyContext).ifPresent(context->context.encodeWrite(binary));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void handleTargetBinaryMessage(ProxyConnect target, byte[] binary){
        ProxyContext proxyContext = manager.getContext(target);
        Optional.of(proxyContext).ifPresent(context->context.decodeWrite(binary));

    }


    /**
     * 本地创建与服务端连接
     */
    public abstract void serviceConnect(ProxyConnect local, TargetServer targetServer);

}
