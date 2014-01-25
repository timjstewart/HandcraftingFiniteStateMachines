package com.timjstewart;

import com.timjstewart.actuator.Boiler;
import com.timjstewart.actuator.PotWarmer;
import com.timjstewart.sensor.BrewButtonSensor;
import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

class Main {
    public static void main(String[] args) {

        // Sensors

        PotSensor potSensor = new PotSensor();
        BrewButtonSensor brewButtonSensor = new BrewButtonSensor();
        WaterLevelSensor waterLevelSensor = new WaterLevelSensor();

        // Actuators

        Boiler boiler = new DebugBoiler();
        PotWarmer potWarmer = new DebugPotWarmer();

        // The FSM

        new CoffeeMaker(boiler, potWarmer, waterLevelSensor, potSensor, brewButtonSensor);

        waterLevelSensor.detect(WaterLevelSensor.State.NotEmpty);
        potSensor.detect(PotSensor.State.Empty);
        brewButtonSensor.detect(BrewButtonSensor.State.Pressed);

        potSensor.detect(PotSensor.State.Gone);

        potSensor.detect(PotSensor.State.Empty);

        waterLevelSensor.detect(WaterLevelSensor.State.Empty);

        potSensor.detect(PotSensor.State.Gone);

        potSensor.detect(PotSensor.State.NonEmpty);

        potSensor.detect(PotSensor.State.Gone);

        potSensor.detect(PotSensor.State.Empty);
    }
}

/**
 * Actuators that don't actuate; they just print out what they're told to do.
 */

class DebugPotWarmer implements PotWarmer {
    @Override
    public void turnOn() {
        System.out.println("POT WARMER: Turned on");
    }

    @Override
    public void turnOff() {
        System.out.println("POT WARMER: Turned off");
    }
}

class DebugBoiler implements Boiler {
    @Override
    public void turnOn() {
        System.out.println("BOILER: Turned On");
    }

    @Override
    public void turnOff() {
        System.out.println("BOILER: Turned Off");
    }
}
