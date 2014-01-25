package com.timjstewart;

import com.timjstewart.sensor.PotSensor;

/**
 * The WarmingInterrupted state is entered from the Warming state when the user removes the CoffeePot from the
 * PotWarmer.
 */
public class WarmingInterruptedState extends CoffeeMaker.AbstractState {

    /**
     * @return WarmingInterrupted
     */
    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.WarmingInterrupted;
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
            case NonEmpty:
                components.getPotWarmer().turnOn();
                return new WarmingState();

            case Empty:
                return new InitialState();
        }

        return this;
    }
}