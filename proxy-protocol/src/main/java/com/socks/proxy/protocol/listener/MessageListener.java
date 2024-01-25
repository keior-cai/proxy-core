package com.socks.proxy.protocol.listener;

import com.socks.proxy.cipher.AbstractCipher;
import com.socks.proxy.protocol.connect.ProxyConnect;
import com.socks.proxy.protocol.codes.ProxyCodes;
import com.socks.proxy.protocol.codes.ProxyMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: chuangjie
 * @date: 2023/11/29
 **/
@Slf4j
public class MessageListener implements ProxyListener{

    private final ProxyCodes<? super ProxyMessage> codes;


    private AbstractCipher cipher;


    public MessageListener(ProxyCodes<? super ProxyMessage> codes){
        this.codes = codes;
    }


    @Override
    public void onMessage(ProxyConnect connect, ProxyConnect local, String message){
//        ProxyConnect localConnect = (ProxyConnect) connect;
//        ProxyMessage decode = codes.decode(message);
//        if(log.isDebugEnabled()){
//            log.debug("decode message = {}", decode);
//        }
//        ServerProxyCommand command = ServerProxyCommand.of(decode.getCommand());
//        if(Objects.isNull(command)){
//            log.error("not exists command");
//            return;
//        }
//        switch(command) {
//            case SEND_PUBLIC_KEY:
//                PublicKeyMessage publicKeyMessage = (PublicKeyMessage) decode;
//                String publicKey = publicKeyMessage.getPublicKey();
//                cipher = localConnect.writeHandshake(publicKey);
//                break;
//            case ACK_USER_MESSAGE:
//                localConnect.writeTarget();
//                break;
//            case CONNECT_SUCCESS:
//                localConnect.connectSuccess();
//                break;
//        }
    }


    @Override
    public void onMessage(ProxyConnect connect, ProxyConnect local, byte[] binary){
        byte[] bytes = cipher.decodeBytes(binary);
        if(log.isDebugEnabled()){
            log.debug("write to system = {}", bytes);
        }
        local.write(bytes);
    }


    @Override
    public void onClose(ProxyConnect connect, ProxyConnect local){
        local.close();
    }
}
