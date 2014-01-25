package com.timjstewart;

import com.timjstewart.sensor.BrewButtonSensor;
import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

/**
 * The Initial state is the default state of the CoffeeMaker.  It is entered from other states when the brew cycle has
 * completed.
 */
class InitialState extends CoffeeMaker.AbstractState {

    /**
     * @return Initial
     */
    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.Initial;
    }

    /**
     * responds to BrewButton state changes
     *
     * @param components an object that contains all of the Actuators and sensors of the CoffeeMaker
     * @param newState   the new state of the PotSensor
     *
     * @return the state that the FSM should transition to or the current state if no transition should occur
     */
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

