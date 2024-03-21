package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.TargetServer;
import com.socks.proxy.protocol.codes.DefaultProxyCommandCodes;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.enums.ConnectType;
import com.socks.proxy.protocol.enums.ServerProxyCommand;
import com.socks.proxy.protocol.handshake.ConnectContextManager;
import com.socks.proxy.protocol.handshake.message.PublicKeyMessage;
import com.socks.proxy.protocol.handshake.message.SenTargetAddressMessage;
import com.socks.proxy.protocol.handshake.message.SendUserMessage;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.RSAUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

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
@Getter
@Setter
public class LocalProxyMessageHandler extends AbstractProxyMessageHandler{

    public LocalProxyMessageHandler(ConnectContextManager manager){
        this(new RSAUtil(), manager);
    }


    public LocalProxyMessageHandler(RSAUtil rsaUtil, ConnectContextManager manager){
        this(rsaUtil, new DefaultProxyCommandCodes(), manager);
    }


    public LocalProxyMessageHandler(RSAUtil rsaUtil, ProxyCodes codes, ConnectContextManager manager){
        super(rsaUtil, codes, manager);
    }


    @Override
    public void handlerShakeEvent(ProxyConnect local){
        ProxyContext context = manager.getContext(local);
        CountDownLatch count = context.getCount();
        if(count != null){
            try {
                count.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
                proxyContext.setRandom(s);
                AbstractCipher cipher = CipherProvider.getByName("aes-256-cfb", s);
                proxyContext.setCipher(cipher);
                connect.write(codes.encodeStr(JSON.toJSONString(new SendUserMessage("aes-256-cfb", "test", "test",
                        AESUtil.encryptByDefaultKey(rsaUtil.encrypt(s, message.getPublicKey()))))));
                break;
            case CONNECT_SUCCESS:
                // 这里来处理连接成功问题
                proxyContext.getCount().countDown();
                log.debug("now send message byte to target");
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
    public void handlerShakeEvent(ProxyConnect local, Map<String, Object> context){
        manager.putLocalConnect(local);
    }


    @Override
    public void handleLocalBinaryMessage(ProxyConnect local, byte[] binary){
        ProxyContext proxyContext = manager.getContext(local);
        if(proxyContext == null){
            // 这里说明远程连接已经关闭了，需要重新连接;直接断开本地连接
            local.close();
            return;
        }
        if(Objects.nonNull(proxyContext.getCount())){
            Optional.of(proxyContext).ifPresent(context->context.dstEncodeWrite(binary));
        } else {
            Optional.of(proxyContext).ifPresent(context->context.dstWrite(binary));
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
            Optional.of(proxyContext).ifPresent(context->context.localDecodeWrite(binary));
        } else {
            Optional.of(proxyContext).ifPresent(context->context.localWrite(binary));
        }

    }


    @Override
    public void handleDstConnect(ProxyConnect local, ProxyConnect dst, TargetServer target){
        manager.putTargetConnect(local, dst);
        manager.getContext(local).setServer(target);
    }
}
