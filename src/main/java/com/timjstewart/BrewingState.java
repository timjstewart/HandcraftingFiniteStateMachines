package com.timjstewart;

import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

class BrewingState extends CoffeeMaker.AbstractState {

    @Override
    public CoffeeMaker.State getState() {
        return CoffeeMaker.State.Brewing;
    }

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