package com.socks.proxy.protocol.handshake.handler;

import com.alibaba.fastjson2.JSON;
import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.cipher.CipherProvider;
import com.socks.proxy.protocol.DefaultCipher;
import com.socks.proxy.protocol.LocalConnect;
import com.socks.proxy.protocol.LocalMiddleProxy;
import com.socks.proxy.protocol.codes.ProxyMessage;
import com.socks.proxy.protocol.handshake.LocalHandshakeMessageHandler;
import com.socks.proxy.protocol.handshake.config.ConnectUserInfo;
import com.socks.proxy.protocol.handshake.message.SendUserMessage;
import com.socks.proxy.protocol.handshake.message.PublicKeyMessage;
import com.socks.proxy.util.AESUtil;
import com.socks.proxy.util.RSAUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * <p>接受ss-server发送RSA公钥处理器</p>
 * <p>
 * 接收到ss-server发送过来的公钥 生成随机密码，密码长度参考{@link com.socks.proxy.protocol.handshake.config.ConnectUserInfo#getPasswordLen()}
 * 加密过程：
 * <li>使用服务端公钥，对随机密码进行加密；</li>
 * <li>使用AES对称加密，使用默认的KEY对使用公钥加密后的密码再次加密</li>
 * </p>
 *
 * @author: chuangjie
 * @date: 2023/5/21
 **/
@Slf4j
@AllArgsConstructor
public class SendRandomPasswordMessageHandler implements LocalHandshakeMessageHandler{

    private final ConnectUserInfo userInfo;


    @Override
    public void handle(LocalConnect local, ProxyMessage message, LocalMiddleProxy remote){
        PublicKeyMessage publicKeyMessage = (PublicKeyMessage) message;
        String publicKey = publicKeyMessage.getPublicKey();

        String random = RandomStringUtils.randomNumeric(userInfo.getPasswordLen());
        log.debug("password = {} public key = {}", random, publicKey);
        String decrypt = null;
        try {
            decrypt = RSAUtil.encrypt(random, publicKey);
        } catch (Exception e) {
            //            throw new ProxyException("RSA加密失败");
        }
        String aes = AESUtil.encryptByDefaultKey(decrypt);
        ProxyMessage proxyMessage = new SendUserMessage(userInfo.getMethod(), userInfo.getUsername(),
                userInfo.getPassword(), aes);
        AbstractCipher cipher = CipherProvider.getByName(userInfo.getMethod(), random);
        local.setCipher(new DefaultCipher(cipher));
        remote.write(JSON.toJSONString(proxyMessage));
    }
}