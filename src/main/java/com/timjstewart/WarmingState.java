package com.timjstewart;

import com.timjstewart.sensor.PotSensor;

public class WarmingState extends CoffeeMaker.AbstractState {

    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.Warming;
    }

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
