/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.socks.proxy.protocol.event;

import com.socks.proxy.protocol.lifecycle.Lifecycle;
import lombok.Getter;

import java.util.EventObject;

@Getter
public final class LifecycleEvent extends EventObject{

    /**
     * The event data associated with this event.
     */
    private final Object data;

    /**
     * The event type this instance represents.
     */
    private final String type;


    public LifecycleEvent(Lifecycle lifecycle, Object data, String type){
        super(lifecycle);
        this.data = data;
        this.type = type;
    }


    /**
     * @return the Lifecycle on which this event occurred.
     */
    public Lifecycle getLifecycle(){
        return (Lifecycle) getSource();
    }
}
