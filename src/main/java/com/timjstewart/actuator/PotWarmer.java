package com.timjstewart.actuator;

/**
 * PotWarmer implementations should implement this interface.
 */
public interface PotWarmer {

    /**
     * turn the PotWarmer on
     *
     * If the PotWarmer is already on, the PotWarmer should remain on
     */
    void turnOn();

    /**
     * turn the PotWarmer on
     *
     * If the PotWarmer is already off, the PotWarmer should remain off
     */
    void turnOff();

}
