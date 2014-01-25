package com.timjstewart.sensor;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Mark IV CoffeePot has a PotSensor that detects whether or not a CoffeePot is on the PotWarmer (and if so, whether
 * or not the CoffeePot is empty).  Objects of this class are told when the PotSensor device's state has changed.
 */
public class PotSensor {

    /**
     * all of the states that a PotSensor can be in
     */
    public enum State {
        Gone,
        Empty,
        NonEmpty
    }

    // the current state of the PotSensor
    private PotSensor.State state;

    /**
     * classes interested in PotSensor events should implement this interface and then call addListener on a PotSensor
     * object.
     */
    public interface Listener {
        void onPotStatusChanged(PotSensor.State status);
    }

    // the set of Listener objects to notify when the PotSensor state changes.
    private Set<Listener> listeners = new HashSet<>();

    /**
     * @return the current state of the PotSensor
     */
    public PotSensor.State getState() {
        return state;
    }

    /**
     * causes the PotSensor to transition to the specified state and notify all Listeners that the state of the
     * PotSensor has changed.
     *
     * @param newState the new state of the PotSensor
     */
    public void detect(PotSensor.State newState) {
        state = checkNotNull(newState, "newState cannot be null");

        for (Listener listener : listeners) {
            listener.onPotStatusChanged(state);
        }
    }

    /**
     * adds the specified Listener to the set of Listeners that will be notified if/when the state of the PotSensor
     * changes.
     *
     * @param listener the Listener object interested in state changes
     */
    public void addListener(Listener listener) {
        checkNotNull(listener, "listener cannot be null");

        listeners.add(listener);
    }

}
