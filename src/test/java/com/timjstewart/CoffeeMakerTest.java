package com.timjstewart;

import com.timjstewart.actuator.Boiler;
import com.timjstewart.actuator.PotWarmer;
import com.timjstewart.sensor.BrewButtonSensor;
import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

class BoilerSpy implements Boiler {

    boolean on = false;

    @Override
    public void turnOn() {
        on = true;
    }

    @Override
    public void turnOff() {
        on = false;
    }

    public boolean isOn() {
        return on;
    }
}


class PotWarmerSpy implements PotWarmer {

    boolean on = false;

    @Override
    public void turnOn() {
        on = true;
    }

    @Override
    public void turnOff() {
        on = false;
    }

    public boolean isOn() {
        return on;
    }
}

public class CoffeeMakerTest {

    private CoffeeMaker coffeeMaker;

    private BoilerSpy boilerSpy;
    private PotWarmerSpy potWarmerSpy;

    private PotSensor potSensor;
    private WaterLevelSensor waterLevelSensor;
    private BrewButtonSensor brewButtonSensor;

    @Before
    public void setUp() {
        boilerSpy = new BoilerSpy();
        potWarmerSpy = new PotWarmerSpy();

        potSensor = new PotSensor();
        waterLevelSensor = new WaterLevelSensor();
        brewButtonSensor = new BrewButtonSensor();

        coffeeMaker = new CoffeeMaker(boilerSpy, potWarmerSpy, waterLevelSensor, potSensor, brewButtonSensor);
    }

    @Test
    public void wontBrewWithoutWater() {
        potSensor.detect(PotSensor.State.Empty);
        brewButtonSensor.detect(BrewButtonSensor.State.Pressed);
        assertFalse(boilerSpy.isOn());
    }

    @Test
    public void wontBrewWithoutPot() {
        waterLevelSensor.detect(WaterLevelSensor.State.NotEmpty);
        brewButtonSensor.detect(BrewButtonSensor.State.Pressed);
        assertFalse(boilerSpy.isOn());
    }

    @Test
    public void willBrewWithWaterAndPot() {
        potSensor.detect(PotSensor.State.Empty);
        waterLevelSensor.detect(WaterLevelSensor.State.NotEmpty);
        brewButtonSensor.detect(BrewButtonSensor.State.Pressed);
        assertTrue(boilerSpy.isOn());
    }

    @Test
    public void warmerComesOnAfterBoilerEmpties() {
        potSensor.detect(PotSensor.State.Empty);
        waterLevelSensor.detect(WaterLevelSensor.State.NotEmpty);
        brewButtonSensor.detect(BrewButtonSensor.State.Pressed);
        waterLevelSensor.detect(WaterLevelSensor.State.Empty);

        assertFalse(boilerSpy.isOn());
        assertTrue(potWarmerSpy.isOn());
    }

    @Test
    public void warmerTurnsOffIfWarmingIsInterrupted() {
        potSensor.detect(PotSensor.State.Empty);
        waterLevelSensor.detect(WaterLevelSensor.State.NotEmpty);
        brewButtonSensor.detect(BrewButtonSensor.State.Pressed);
        waterLevelSensor.detect(WaterLevelSensor.State.Empty);

        potSensor.detect(PotSensor.State.Gone);

        assertEquals(CoffeeMaker.State.WarmingInterrupted, coffeeMaker.getState());

        assertFalse(boilerSpy.isOn());
        assertFalse(potWarmerSpy.isOn());
    }
}
