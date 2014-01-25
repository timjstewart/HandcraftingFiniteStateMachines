package com.timjstewart;

import com.timjstewart.sensor.BrewButtonSensor;
import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

class InitialState extends CoffeeMaker.AbstractState {

    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.Initial;
    }

    @Override
    public CoffeeMaker.AbstractState onBrewButtonStateChanged(
            final CoffeeMaker.Components components,
            final BrewButtonSensor.State newState) {

        switch (newState) {
            case Pressed:
                if (components.getWaterLevelSensor().getWaterLevel() == WaterLevelSensor.State.NotEmpty &&
                        components.getPotSensor().getState() == PotSensor.State.Empty) {
                    components.getBoiler().turnOn();
                    return new BrewingState();
                }
                break;
        }

        return this;
    }
}

