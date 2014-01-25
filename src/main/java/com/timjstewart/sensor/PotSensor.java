package com.timjstewart.sensor;

import java.util.HashSet;
import java.util.Set;

public class PotSensor {

    public enum State {
        Gone,
        Empty,
        NonEmpty
    }

    private PotSensor.State state;

    public interface Listener {
        void onPotStatusChanged(PotSensor.State status);
    }

    private Set<Listener> listeners = new HashSet<>();

    public PotSensor.State getState() {
        return state;
    }

    public void detect(PotSensor.State newState) {
        for (Listener listener : listeners) {
            listener.onPotStatusChanged(this.state = newState);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
