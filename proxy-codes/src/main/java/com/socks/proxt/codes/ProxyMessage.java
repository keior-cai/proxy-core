package com.socks.proxt.codes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: chuangjie
 * @date: 2023/6/4
 **/
@Getter
@Setter
@AllArgsConstructor
public abstract class ProxyMessage{

    private int command;

}
