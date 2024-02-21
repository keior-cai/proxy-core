package com.socks.proxy.protocol.tracker;

import com.socks.proxy.protocol.connect.ProxyConnect;

public interface Tracker{

    ProxyConnect local();


    ProxyConnect remote();
}
