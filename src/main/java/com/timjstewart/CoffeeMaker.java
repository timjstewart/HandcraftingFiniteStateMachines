package com.timjstewart;

import com.timjstewart.actuator.Boiler;
import com.timjstewart.actuator.PotWarmer;
import com.timjstewart.sensor.BrewButtonSensor;
import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

/**
 * The Coffee Maker FSM.  It has references to all of its sensors (so that it can query their respective states) and to
 * all of its actuators so that it can control them.
 */

class CoffeeMaker implements BrewButtonSensor.Listener, PotSensor.Listener, WaterLevelSensor.Listener {

    /**
     * This class exists for one reason: encapsulation.
     * <p/>
     * Rather than expose the CoffeeMaker's components (e.g boiler) from the CoffeeMaker and take the risk that some other
     * class might decide to command the boiler to do something, the components are wrapped up in a class that is only
     * passed to classes derived from AbstractState that the CoffeeMaker creates.
     */
    class Components {

        // Actuators

        private final Boiler boiler;
        private final PotWarmer potWarmer;

        // Sensors

        private final WaterLevelSensor waterLevelSensor;
        private final PotSensor potSensor;
        private final BrewButtonSensor brewButton;

        Components(final Boiler boiler,
                   final PotWarmer potWarmer,
                   final WaterLevelSensor waterLevelSensor,
                   final PotSensor potSensor,
                   final BrewButtonSensor brewButton
        ) {
            this.boiler = boiler;
            this.potWarmer = potWarmer;

            this.waterLevelSensor = waterLevelSensor;
            this.potSensor = potSensor;
            this.brewButton = brewButton;
        }

        public Boiler getBoiler() {
            return boiler;
        }

        public PotWarmer getPotWarmer() {
            return potWarmer;
        }

        public WaterLevelSensor getWaterLevelSensor() {
            return waterLevelSensor;
        }

        public PotSensor getPotSensor() {
            return potSensor;
        }

        public BrewButtonSensor getBrewButton() {
            return brewButton;
        }
    }

    /**
     * This is the base class of all CoffeeMaker states.
     * <p/>
     * It never changes state and is useful for deriving state classes that only care about a subset of event sources.
     */
    abstract static class AbstractState {

        /**
         * @return the CoffeeMaker.State enum value that corresponds to a derived class.
         */
        public abstract State getState();

        /**
         * called whenever the brew button state changes
         */
        AbstractState onBrewButtonStateChanged(final Components components, final BrewButtonSensor.State newState) {
            return this;
        }

        /**
         * called whenever pot state changes
         */
        AbstractState onPotStateChanged(final Components components, final PotSensor.State newState) {
            return this;
        }

        /**
         * called whenever the water level state changes
         */
        AbstractState onWaterLevelStateChanged(final Components components, final WaterLevelSensor.State newState) {
            return this;
        }
    }

    /**
     * represents the possible states that the Coffee Pot can be in.
     * <p/>
     * That state can be interrogated by clients and unit tests.
     * <p/>
     * Each State enum value owns an instance of AbstractState.  This is purely an optimization to reduce the amount of
     * garbage that is generated as the state machine is transitioned through.
     * <p/>
     * For small number of states and event sources, the AbstractState hierarchy can be encoded as methods on the enum
     * and not separate classes.
     */
    enum State {

        Initial(new InitialState()),

        Brewing(new BrewingState()),

        Warming(new WarmingState()),

        BrewingInterrupted(new BrewingInterruptedState()),

        WarmingInterrupted(new WarmingInterruptedState());

        private final AbstractState impl;

        State(AbstractState abstractState) {
            this.impl = abstractState;
        }

        AbstractState get() {
            return impl;
        }
    }

    // The state of the coffee maker
    private AbstractState state;

    // The components that the coffee maker interacts with
    private final Components components;

    /**
     * Creates a CoffeeMaker object
     *
     * @param boiler           the boiler used to boil the water
     * @param potWarmer        the pot warmer used to keep freshly brewed coffee warm
     * @param waterLevelSensor the water level sensor that detects whether or not there is water in the boiler
     * @param potSensor        the pot sensor that detects if there is a pot on the warmer plate and, if there is,
     *                         whether or not it's empty.
     * @param brewButton       the brew button that the use presses to initiate a brew/warm cycle.
     */
    public CoffeeMaker(
            final Boiler boiler,
            final PotWarmer potWarmer,
            final WaterLevelSensor waterLevelSensor,
            final PotSensor potSensor,
            final BrewButtonSensor brewButton
    ) {
        components = new Components(boiler, potWarmer, waterLevelSensor, potSensor, brewButton);

        // listen for state changes
        components.getWaterLevelSensor().addListener(this);
        components.getPotSensor().addListener(this);
        components.getBrewButton().addListener(this);

        // immediately go to the initial state
        state = new InitialState();
    }

    /**
     * @return the state of the coffee maker
     */
    public State getState() {
        return state.getState();
    }

    /**
     * State Change Handlers - pass all actuator state changes onto the current state enum value so that it can decide
     * whether or not to transition to another state.
     */

    @Override
    public void onBrewButtonStatusChanged(BrewButtonSensor.State newState) {
        state = state.onBrewButtonStateChanged(components, newState);

        // automatically reset the Brew button
        if (newState == BrewButtonSensor.State.Pressed) {
            components.getBrewButton().detect(BrewButtonSensor.State.NotPressed);
        }
    }

    @Override
    public void onPotStatusChanged(PotSensor.State newState) {
        state = state.onPotStateChanged(components, newState);
    }

    @Override
    public void onWaterLevelChanged(WaterLevelSensor.State newState) {
        state = state.onWaterLevelStateChanged(components, newState);
    }
}
