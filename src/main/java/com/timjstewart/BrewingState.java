package com.timjstewart;

import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

/**
 * The BrewingInterrupted state is entered from the Initial state when there is water in the Boiler, a CoffeePot on the
 * PotWarmer and the user presses the Brew button.
 * <p/>
 * The BrewingInterrupted state is also entered from the BrewingInterrupted state when the user replaces the CoffeePot
 * on the Pot Warmer and there is water left in the Boiler.
 */
class BrewingState extends CoffeeMaker.AbstractState {

    /**
     * @return Brewing
     */
    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.Brewing;
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
            case Gone:
                components.getBoiler().turnOff();
                return CoffeeMaker.State.BrewingInterrupted.get();
        }

        return this;
    }

    /**
     * responds to WaterLevel state changes
     *
     * @param components an object that contains all of the Actuators and sensors of the CoffeeMaker
     * @param newState   the new state of the PotSensor
     *
     * @return the state that the FSM should transition to or the current state if no transition should occur
     */
    @Override
    public CoffeeMaker.AbstractState onWaterLevelStateChanged(
            final CoffeeMaker.Components components,
            final WaterLevelSensor.State newState) {

        switch (newState) {
            case Empty:
                components.getBoiler().turnOff();
                components.getPotWarmer().turnOn();
                return CoffeeMaker.State.Warming.get();
        }

        return this;
    }

}