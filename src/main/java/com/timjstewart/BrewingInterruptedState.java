package com.timjstewart;

import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

public class BrewingInterruptedState extends CoffeeMaker.AbstractState {

    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.BrewingInterrupted;
    }

    @Override
    public CoffeeMaker.AbstractState onPotStateChanged(
            final CoffeeMaker.Components components,
            final PotSensor.State newState) {

        switch (newState) {
            case Empty:
                if (components.getWaterLevelSensor().getWaterLevel() == WaterLevelSensor.State.NotEmpty) {
                    components.getBoiler().turnOn();
                    return CoffeeMaker.State.Brewing.get();
                } else {
                    return CoffeeMaker.State.Initial.get();
                }

            case NonEmpty:
                if (components.getWaterLevelSensor().getWaterLevel() == WaterLevelSensor.State.NotEmpty) {
                    components.getBoiler().turnOn();
                    return CoffeeMaker.State.Brewing.get();
                } else {
                    components.getPotWarmer().turnOn();
                    return CoffeeMaker.State.Warming.get();
                }
        }

        return this;
    }
}