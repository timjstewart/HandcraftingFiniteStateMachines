package com.timjstewart.sensor;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Mark IV CoffeePot has a Brew button that initiates a brew cycle.  Objects of this class are told about Brew
 * button state changes and report those changes to interested Listeners
 */
public class BrewButtonSensor {

    /**
     * all of the states that a Brew button can be in
     */
    public enum State {
        Pressed,
        NotPressed
    }

    // the current state of the Brew button
    private BrewButtonSensor.State brewButtonState;

    /**
     * classes interested in Brew button events should implement this interface and then call addListener on a
     * BrewButton object.
     */
    public interface Listener {
        void onBrewButtonStatusChanged(BrewButtonSensor.State status);
    }

    // the set of Listener objects to notify when the Brew button state changes.
    private Set<Listener> listeners = new HashSet<>();

    /**
     * @return the current state of the Brew button
     */
    public BrewButtonSensor.State getBrewButtonState() {
        return brewButtonState;
    }

    /**
     * causes the Brew button to transition to the specified state and notify all Listeners that the state of the Brew
     * button has changed.
     *
     * @param newState the new state of the Brew button
     */
    public void detect(BrewButtonSensor.State newState) {
        brewButtonState = checkNotNull(newState, "newState cannot be null");

        for (Listener listener : listeners) {
            listener.onBrewButtonStatusChanged(brewButtonState);
        }
    }

    /**
     * adds the specified Listener to the set of Listeners that will be notified if/when the state of the Brew button
     * changes.
     *
     * @param listener the Listener object interested in state changes
     */
    public void addListener(Listener listener) {
        checkNotNull(listener, "listener cannot be null");

        listeners.add(listener);
    }

}
