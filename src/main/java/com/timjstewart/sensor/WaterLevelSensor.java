package com.timjstewart.sensor;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Mark IV CoffeePot has a WaterLevelSensor can detect whether or not the Boiler has water in it.  Objects of this
 * class are told about Water Level state changes and report those changes to interested Listeners
 */
public class WaterLevelSensor {

    /**
     * all of the states that a WaterLevelSensor can be in
     */
    public enum State {
        Empty,
        NotEmpty
    }

    // the current state of the WaterLevelSensor
    private WaterLevelSensor.State waterLevel;

    /**
     * classes interested in WaterLevelSensor events should implement this interface and then call addListener on a
     * WaterLevelSensor object.
     */
    public interface Listener {
        void onWaterLevelChanged(WaterLevelSensor.State waterLevel);
    }

    // the set of Listener objects to notify when the WaterLevelSensor state changes.
    private Set<Listener> listeners = new HashSet<>();

    /**
     * @return the current state of the WaterLevelSensor
     */
    public WaterLevelSensor.State getWaterLevel() {
        return waterLevel;
    }

    /**
     * causes the WaterLevelSensor to transition to the specified state and notify all Listeners that the state of the
     * WaterLevelSensor has changed.
     *
     * @param waterLevel the new water level of the WaterLevelSensor
     */
    public void detect(WaterLevelSensor.State waterLevel) {
        this.waterLevel = checkNotNull(waterLevel, "waterLevel cannot be null");

        for (Listener listener : listeners) {
            listener.onWaterLevelChanged(waterLevel);
        }
    }

    /**
     * adds the specified Listener to the set of Listeners that will be notified if/when the state of the
     * WaterLevelSensor changes.
     *
     * @param listener the Listener object interested in state changes
     */
    public void addListener(Listener listener) {
        checkNotNull(listener, "listener cannot be null");

        listeners.add(listener);
    }
}
