package com.timjstewart.actuator;

/**
 * Boiler implementations should implement this interface.
 */
public interface Boiler {

    /**
     * turn the Boiler on
     *
     * If the Boiler is already on, the Boiler should remain on
     */
    void turnOn();

    /**
     * turn the Boiler off
     *
     * If the Boiler is already off, the Boiler should remain off
     */
    void turnOff();

}
