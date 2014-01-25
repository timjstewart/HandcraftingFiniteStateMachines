package com.timjstewart;

import com.timjstewart.sensor.PotSensor;

public class WarmingInterruptedState extends CoffeeMaker.AbstractState {
    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.WarmingInterrupted;
    }

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