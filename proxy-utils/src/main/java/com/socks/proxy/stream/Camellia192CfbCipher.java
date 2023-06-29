package com.socks.proxy.stream;

import com.socks.proxy.cipher.LocalStreamCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

/**
 * Camellia-192
 */
public class Camellia192CfbCipher extends LocalStreamCipher{
    /**
     * localStreamCipher
     *
     * @param password password
     */
    public Camellia192CfbCipher(String password){
        super(password);
    }


    @Override
    public StreamCipher getNewCipherInstance(){
        return new CFBBlockCipher(new CamelliaEngine(), getVILength() * 8);
    }


    @Override
    public int getVILength(){
        return 16;
    }


    @Override
    public int getKeyLength(){
        return 24;
    }
}
