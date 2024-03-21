package com.socks.proxy.protocol.lifecycle;

import com.socks.proxy.protocol.event.LifecycleEvent;
import com.socks.proxy.protocol.exception.LifecycleException;
import com.socks.proxy.protocol.listener.LifecycleListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: chuangjie
 * @date: 2024/3/13
 **/
@Slf4j
public abstract class LifecycleBean implements Lifecycle{

    private volatile LifecycleState state = LifecycleState.NEW;

    private final List<LifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<>();


    @Override
    public final synchronized void init() throws LifecycleException{
        if(!state.equals(LifecycleState.NEW)){
            throw new LifecycleException(Lifecycle.BEFORE_INIT_EVENT);
        }
        setStateInternal(LifecycleState.INITIALIZING, null);
        initInternal();
        setStateInternal(LifecycleState.INITIALIZED, null);
    }


    protected abstract void initInternal();


    protected synchronized void setStateInternal(LifecycleState state, Object data){
        String lifecycleEvent = state.getLifecycleEvent();
        this.state = state;
        if(lifecycleEvent != null){
            fireLifecycleEvent(lifecycleEvent, data);
        }
    }


    protected void fireLifecycleEvent(String type, Object data){
        LifecycleEvent event = new LifecycleEvent(this, data, type);
        for(LifecycleListener listener : lifecycleListeners) {
            listener.lifecycleEvent(event);
        }
    }


    @Override
    public final synchronized void start() throws LifecycleException{
        if(LifecycleState.STARTING_PREP.equals(state) || LifecycleState.STARTING.equals(state)
                || LifecycleState.STARTED.equals(state)){
            LifecycleException e = new LifecycleException("lifecycleBase.alreadyStarted");
            if(log.isDebugEnabled()){
                log.debug("lifecycleBase.alreadyStarted", e);
            } else if(log.isInfoEnabled()){
                log.info("lifecycleBase.alreadyStarted");
            }
            return;
        }
        if(state == LifecycleState.NEW){
            init();
        } else if(state == LifecycleState.FAILED){
            stop();
        } else if(!(state == LifecycleState.INITIALIZED)){
            throw new LifecycleException(Lifecycle.BEFORE_START_EVENT);
        }
        try {
            setStateInternal(LifecycleState.STARTING_PREP, null);
            startInternal();
            if(state.equals(LifecycleState.FAILED)){
                stop();
            } else if(!state.equals(LifecycleState.STARTING)){
                throw new LifecycleException(Lifecycle.AFTER_START_EVENT);
            } else {
                setStateInternal(LifecycleState.STARTED, null);
            }
        } catch (Throwable throwable) {
            throw new LifecycleException("lifecycleBase.startFail", throwable);
        }
    }


    protected abstract void startInternal();


    @Override
    public final synchronized void stop() throws LifecycleException{
        if(LifecycleState.STOPPING_PREP.equals(state) || LifecycleState.STOPPING.equals(state)
                || LifecycleState.STOPPED.equals(state)){
            Exception e = new LifecycleException("lifecycleBase.alreadyStopped");
            if(log.isDebugEnabled()){
                log.debug("lifecycleBase.alreadyStopped", e);
            } else if(log.isInfoEnabled()){
                log.info("lifecycleBase.alreadyStopped");
            }
            return;
        }

        if(state.equals(LifecycleState.NEW)){
            state = LifecycleState.STOPPED;
            return;
        }

        if(!state.equals(LifecycleState.STARTED) && !state.equals(LifecycleState.FAILED)){
            throw new LifecycleException(Lifecycle.BEFORE_STOP_EVENT);
        }
        try {
            if(state.equals(LifecycleState.FAILED)){
                // Don't transition to STOPPING_PREP as that would briefly mark the
                // component as available but do ensure the BEFORE_STOP_EVENT is
                // fired
                fireLifecycleEvent(BEFORE_STOP_EVENT, null);
            } else {
                setStateInternal(LifecycleState.STOPPING_PREP, null);
            }
            stopInternal();
            // Shouldn't be necessary but acts as a check that sub-classes are
            // doing what they are supposed to.
            if(!state.equals(LifecycleState.STOPPING) && !state.equals(LifecycleState.FAILED)){
                throw new LifecycleException(Lifecycle.AFTER_STOP_EVENT);
            }
            setStateInternal(LifecycleState.STOPPED, null);
        } catch (Throwable throwable) {
            throw new LifecycleException("lifecycleBase.stopFail");
        }

    }


    protected abstract void stopInternal();


    @Override
    public final synchronized void destroy() throws LifecycleException{
        if(LifecycleState.FAILED.equals(state)){
            try {
                // Triggers clean-up
                stop();
            } catch (LifecycleException e) {
                throw new LifecycleException("lifecycleBase.destroyStopFail", e);
            }
        }

        if(LifecycleState.DESTROYING.equals(state) || LifecycleState.DESTROYED.equals(state)){
            if(log.isDebugEnabled()){
                Exception e = new LifecycleException();
                log.debug("lifecycleBase.alreadyDestroyed", e);
            } else if(log.isInfoEnabled()){
                // Rather than have every component that might need to call
                // destroy() check for SingleUse, don't log an info message if
                // multiple calls are made to destroy()
                log.info("lifecycleBase.alreadyDestroyed");
            }

            return;
        }

        if(!state.equals(LifecycleState.STOPPED) && !state.equals(LifecycleState.FAILED) && !state.equals(
                LifecycleState.NEW) && !state.equals(LifecycleState.INITIALIZED)){
            throw new LifecycleException(Lifecycle.BEFORE_DESTROY_EVENT);
        }

        try {
            setStateInternal(LifecycleState.DESTROYING, null);
            destroyInternal();
            setStateInternal(LifecycleState.DESTROYED, null);
        } catch (Throwable t) {
            throw new LifecycleException("lifecycleBase.destroyFail", t);
        }
    }


    protected abstract void destroyInternal();


    @Override
    public void addLifecycleListener(LifecycleListener listener){
        lifecycleListeners.add(listener);
    }


    @Override
    public void removeLifecycleListener(LifecycleListener listener){
        lifecycleListeners.remove(listener);
    }


    @Override
    public List<LifecycleListener> findLifecycleListeners(){
        return Collections.unmodifiableList(lifecycleListeners);
    }
}
