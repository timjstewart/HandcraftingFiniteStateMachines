package com.timjstewart;

import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

/**
 * The BrewingInterrupted state is entered from the Brewing state when the user removes the CoffeePot from the
 * PotWarmer.
 */
public class BrewingInterruptedState extends CoffeeMaker.AbstractState {

    /**
     * @return BrewingInterrupted
     */
    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.BrewingInterrupted;
    }

    /**
     * responds to PotSensor state changes
     *
     * @param components an object that contains all of the Actuators and sensors of the CoffeeMaker
     * @param newState   the new state of the PotSensor
     *
     * @return the state that the FSM should transition to or the current state if no transition should occur
     */
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