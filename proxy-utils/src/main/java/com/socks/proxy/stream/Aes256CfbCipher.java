package com.socks.proxy.stream;

import com.socks.proxy.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

/**
 * aes-256
 */
public class Aes256CfbCipher extends LocalStreamCipher{
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Aes256CfbCipher(String password){
        super("aes-256-cfb", password);
    }


    @Override
    public StreamCipher getNewCipherInstance(){
        return new CFBBlockCipher(new AESEngine(), getVILength() * 8);
    }


    @Override
    public int getVILength(){
        return 16;
    }


    @Override
    public int getKeyLength(){
        return 32;
    }
}
