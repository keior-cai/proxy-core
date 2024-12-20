package com.socks.proxy.stream;

import com.socks.proxy.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaChaEngine;

/**
 * Chacha-20
 */
public class Chacha20Cipher extends LocalStreamCipher{
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Chacha20Cipher(String password){
        super(password);
    }


    @Override
    public StreamCipher getNewCipherInstance(){
        return new ChaChaEngine();
    }


    @Override
    public int getVILength(){
        return 8;
    }


    @Override
    public int getKeyLength(){
        return 32;
    }
}
