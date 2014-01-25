package com.timjstewart;

/**
 * Theory of Finite State Machines:
 * <p/>
 * In a problem modeled as a Finite State Machine there are different kinds of stateful entities. I'll call them sensors
 * and actuators.
 * <p/>
 * Sensors:
 * <p/>
 * A sensor receives some precept from outside of the system and this precept may or may not be handled by the system.
 * Examples of sensors are pressure sensors, heat sensors, distance sensors, moisture detectors, etc.  Once the precept
 * has been handled and the FSM has adjusted its state in response, the FSM can go back to waiting for the next
 * precept.
 * <p/>
 * Actuators:
 * <p/>
 * An actuator allows the system to interact with things outside the system.  Examples of actuators include servos,
 * doors, valves, pumps, etc.
 * <p/>
 * The goal of the Finite State Machine is to keep the actuators doing the right thing in response to the sensors
 * inputs.
 * <p/>
 * State Space:
 * <p/>
 * A Finite State Machine's state space is largely governed by the rule of product.  If you have two entities in an FSM
 * and they each can be in one of two states, then the FSM can be in four total states.  For systems with lots of
 * entities with lots of states an FSM's state space can mushroom.
 * <p/>
 * State Space Reduction:
 * <p/>
 * There are two primary ways of reducing the state space to a more manageable size.  The first is removing illegal
 * states.  The second is part of the theory I'm trying to prove; focusing on actuator state.
 * <p/>
 * Removing Illegal States:
 * <p/>
 * If you have four entities in your FSM and each of them has two states that's 2^4 (16) possible states.  If you decide
 * that entity A can never be in state S1 when entity B is in state S3, then one of the four states for entities A and B
 * has been eliminated and now components A and B can only be ie total states reducing the total number of states from
 * 16 to 12 (3 x 2 x 2).
 * <p/>
 * Focusing on Actuator State:
 * <p/>
 * I'm going to pretend that the only states that really matter at the outset are the actuator states.  In Robert C.
 * Martin's Mark IV Coffee Maker example, this causes a significant reduction to the state space.
 * <p/>
 * For example, we have the following actuators:  com.timjstewart.Boiler and Plate Warmer.  We are ignoring, for the time being, all of
 * the sensors: Warmer Plate Sensor, Water Level Sensor, and the Brew Button.
 * <p/>
 * Then we take the two actuator states:
 * <p/>
 * com.timjstewart.Boiler { On, Off }
 * <p/>
 * Plate Warmer { On, Off }
 * <p/>
 * and look for any illegal states.  Only one of those states is illegal.  Let's say that we have found that the freshly
 * brewed coffee in the pot does not start getting cold until several minutes after the brew cycle has completed.  We
 * can conserve power consumption by then stating that if the com.timjstewart.Boiler is on (and therefore brewing coffee) that the Plate
 * Warmer is unnecessary and can only draw unnecessary power to the unit.  That leaves us with the following legal
 * states:
 * <p/>
 * com.timjstewart.Boiler: On, Plate Warmer: Off
 * <p/>
 * com.timjstewart.Boiler: Off, Plate Warmer: On
 * <p/>
 * com.timjstewart.Boiler: Off, Plate Warmer: Off
 * <p/>
 * Now, it's time to name those states.  One thing you'll quickly find is that some combinations of states can represent
 * multiple system states.  For example, there are two system states where both the com.timjstewart.Boiler and the Plate Warmer will be
 * off.  The first is when the unit is first plugged in.  The second is when, during a brew cycle, the user removes the
 * coffee pot to sneak a cup.  In that case, the boiler should turn off so that no water is sprayed on the grounds, and
 * the plate warmer should turn off because there is nothing on the plate to warm.  In this case, list all possible
 * state names for each combination of actuator states.  We make sure to create multiple system states for one
 * configuration of actuator states because the way the system responds to events in those multiple states is probably
 * different.  For example, replacing an empty pot when the com.timjstewart.Boiler and the Plate Warmer are off does something different
 * if the unit had just been turned on, compared to if the Coffee Maker had been brewing coffee.
 * <p/>
 * State Names:
 * <p/>
 * com.timjstewart.Boiler: On, Plate Warmer: Off - Brewing
 * <p/>
 * com.timjstewart.Boiler: Off, Plate Warmer: On - Warming
 * <p/>
 * com.timjstewart.Boiler: Off, Plate Warmer: Off - Off, Brewing Interrupted
 * <p/>
 * <p/>
 * Events:
 * <p/>
 * Having identified the system level states, we now focus on the events that can cause the system to transition from
 * one state to another.  Some of the events are user-generated (e.g. pressing the Brew button) while others are
 * triggered by actuators (directly or indirectly, e.g. the Water Level Sensor detecting an empty com.timjstewart.Boiler).
 * <p/>
 * Brew Button - Pressed
 * <p/>
 * Water Level Sensor - Detects Empty com.timjstewart.Boiler
 * <p/>
 * Water Level Sensor - Detects Non-Empty com.timjstewart.Boiler
 * <p/>
 * Plate Sensor - Detects No Pot
 * <p/>
 * Plate Sensor - Detects Empty Pot
 * <p/>
 * Plate Sensor - Detects Non-Empty Pot
 * <p/>
 * These become methods on the State Classes we'll define.  You'll note that every method returns a com.timjstewart.CoffeePotState. This
 * implies that an implementation of com.timjstewart.CoffeePotState can decide which state to transition to when an event is received.
 */

/**
 * The code is starting to tell me that I may want to change the interface for the state machine from using one method
 * per event to using an com.timjstewart.Event Enum and then one method.  That way I can more explicitly group similar
 * responses to events.
 */

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
