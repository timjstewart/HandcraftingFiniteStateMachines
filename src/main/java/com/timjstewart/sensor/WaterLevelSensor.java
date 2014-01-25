package com.timjstewart.sensor;

import java.util.HashSet;
import java.util.Set;

public class WaterLevelSensor {

    public enum State {
        Empty,
        NotEmpty
    }

    private WaterLevelSensor.State waterLevel;

    public interface Listener {
        void onWaterLevelChanged(WaterLevelSensor.State waterLevel);
    }

    private Set<Listener> listeners = new HashSet<>();

    public WaterLevelSensor.State getWaterLevel() {
        return waterLevel;
    }

    public void detect(WaterLevelSensor.State waterLevel) {
        for (Listener listener : listeners) {
            listener.onWaterLevelChanged(this.waterLevel = waterLevel);
        }
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
}
