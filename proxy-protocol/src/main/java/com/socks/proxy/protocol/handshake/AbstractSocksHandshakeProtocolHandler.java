package com.socks.proxy.protocol.handshake;

import com.socks.proxy.protocol.enums.Protocol;
import com.socks.proxy.protocol.exception.UnKnowProtocolException;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * socks 握手包协议处理器实现
 *
 * @author: chuangjie
 * @date: 2023/5/30
 **/
@Slf4j
public abstract class AbstractSocksHandshakeProtocolHandler implements HandshakeProtocolHandler{

    @Override
    public Protocol handler(InputStream is) throws IOException{
        int read = is.read();
        if(read != version()){
            log.error("version = {}", read);
            throw new UnKnowProtocolException();
        }
        int method = is.read();
        if(method == -1){
            throw new EOFException();
        }
        int available = is.available();
        byte[] content = new byte[available];
        int len = is.read(content);
        if(len <= 0){
            throw new EOFException();
        }
        boolean validate = validate((byte) (method & 0xff), content);
        if(!validate){
            throw new UnKnowProtocolException();
        }
        return protocol();
    }


    /**
     * socks版本之外的信息是否正确
     *
     * @param b 命令，或者方式
     * @param bytes 握手协议内容
     * @return true-socks握手协议版本正确
     */
    protected abstract boolean validate(byte b, byte[] bytes);


    /**
     * 版本号
     *
     * @return 握手协议版本
     */
    protected abstract byte version();


    /**
     * 握手协议
     *
     * @return {@link Protocol}
     */
    protected abstract Protocol protocol();
}
