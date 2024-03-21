package com.socks.proxy.protocol.lifecycle;

import com.socks.proxy.protocol.exception.LifecycleException;
import com.socks.proxy.protocol.listener.LifecycleListener;

import java.util.List;

/**
 * <pre>
 *            start()
 *  -----------------------------
 *  |                           |
 *  | init()                    |
 * NEW -»-- INITIALIZING        |
 * | |           |              |     ------------------«-----------------------
 * | |           |auto          |     |                                        |
 * | |          \|/    start() \|/   \|/     auto          auto         stop() |
 * | |      INITIALIZED --»-- STARTING_PREP --»- STARTING --»- STARTED --»---  |
 * | |         |                                                            |  |
 * | |destroy()|                                                            |  |
 * | --»-----«--    ------------------------«--------------------------------  ^
 * |     |          |                                                          |
 * |     |         \|/          auto                 auto              start() |
 * |     |     STOPPING_PREP ----»---- STOPPING ------»----- STOPPED -----»-----
 * |    \|/                               ^                     |  ^
 * |     |               stop()           |                     |  |
 * |     |       --------------------------                     |  |
 * |     |       |                                              |  |
 * |     |       |    destroy()                       destroy() |  |
 * |     |    FAILED ----»------ DESTROYING ---«-----------------  |
 * |     |                        ^     |                          |
 * |     |     destroy()          |     |auto                      |
 * |     --------»-----------------    \|/                         |
 * |                                 DESTROYED                     |
 * |                                                               |
 * |                            stop()                             |
 * ----»-----------------------------»------------------------------
 * Any state can transition to FAILED.
 * Calling start() while a component is in states STARTING_PREP, STARTING or
 * STARTED has no effect.
 * Calling start() while a component is in state NEW will cause init() to be
 * called immediately after the start() method is entered.
 * Calling stop() while a component is in states STOPPING_PREP, STOPPING or
 * STOPPED has no effect.
 * Calling stop() while a component is in state NEW transitions the component
 * to STOPPED. This is typically encountered when a component fails to start and
 * does not start all its sub-components. When the component is stopped, it will
 * try to stop all sub-components - even those it didn't start.
 * </pre>
 */
public interface Lifecycle{

    /**
     * The LifecycleEvent type for the "component before init" event.
     */
    String BEFORE_INIT_EVENT = "before_init";

    /**
     * The LifecycleEvent type for the "component after init" event.
     */
    String AFTER_INIT_EVENT = "after_init";

    /**
     * The LifecycleEvent type for the "component start" event.
     */
    String START_EVENT = "start";

    /**
     * The LifecycleEvent type for the "component before start" event.
     */
    String BEFORE_START_EVENT = "before_start";

    /**
     * The LifecycleEvent type for the "component after start" event.
     */
    String AFTER_START_EVENT = "after_start";

    /**
     * The LifecycleEvent type for the "component stop" event.
     */
    String STOP_EVENT = "stop";

    /**
     * The LifecycleEvent type for the "component before stop" event.
     */
    String BEFORE_STOP_EVENT = "before_stop";

    /**
     * The LifecycleEvent type for the "component after stop" event.
     */
    String AFTER_STOP_EVENT = "after_stop";

    /**
     * The LifecycleEvent type for the "component after destroy" event.
     */
    String AFTER_DESTROY_EVENT = "after_destroy";

    /**
     * The LifecycleEvent type for the "component before destroy" event.
     */
    String BEFORE_DESTROY_EVENT = "before_destroy";


    void init() throws LifecycleException;


    void start() throws LifecycleException;


    void stop() throws LifecycleException;


    void destroy() throws LifecycleException;


    /**
     * Add a LifecycleEvent listener to this component.
     *
     * @param listener The listener to add
     */
    void addLifecycleListener(LifecycleListener listener);


    /**
     * Get the life cycle listeners associated with this life cycle.
     *
     * @return An array containing the life cycle listeners associated with this life cycle. If this component has no
     *         listeners registered, a zero-length array is returned.
     */
    List<LifecycleListener> findLifecycleListeners();


    /**
     * Remove a LifecycleEvent listener from this component.
     *
     * @param listener The listener to remove
     */
    void removeLifecycleListener(LifecycleListener listener);
}

