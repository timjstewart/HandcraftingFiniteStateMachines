package com.timjstewart.sensor;

import java.util.HashSet;
import java.util.Set;

public class BrewButtonSensor {

    public enum State {
        Pressed,
        NotPressed
    }

    private BrewButtonSensor.State brewButtonStatus;

    public interface Listener {
        void onBrewButtonStatusChanged(BrewButtonSensor.State status);
    }

    private Set<Listener> listeners = new HashSet<>();

    public BrewButtonSensor.State getBrewButtonStatus() {
        return brewButtonStatus;
    }

    public void detect(BrewButtonSensor.State state) {
        for (Listener listener : listeners) {
            listener.onBrewButtonStatusChanged(this.brewButtonStatus = state);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
