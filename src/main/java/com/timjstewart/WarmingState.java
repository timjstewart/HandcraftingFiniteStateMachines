package com.timjstewart;

import com.timjstewart.sensor.PotSensor;

/**
 * The Warming state is entered from the Brewing state when all of the water from the Boiler has been transferred to the
 * CoffeePot.
 */
public class WarmingState extends CoffeeMaker.AbstractState {

    /**
     * @return Warming
     */
    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.Warming;
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
            case Empty:
                components.getPotWarmer().turnOff();
                return new WarmingInterruptedState();
        }
        return this;
    }

}
