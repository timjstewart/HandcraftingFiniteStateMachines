package com.timjstewart;

import com.timjstewart.actuator.Boiler;
import com.timjstewart.actuator.PotWarmer;
import com.timjstewart.sensor.BrewButtonSensor;
import com.timjstewart.sensor.PotSensor;
import com.timjstewart.sensor.WaterLevelSensor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Coffee Maker FSM.  It has references to all of its sensors (so that it can query their respective states) and to
 * all of its actuators so that it can control them.
 */

class CoffeeMaker implements BrewButtonSensor.Listener, PotSensor.Listener, WaterLevelSensor.Listener {

    /**
     * a class that encapsulates all of the Actuators and Sensors the CoffeeMaker interacts with
     * <p/>
     * Rather than expose the CoffeeMaker's components (e.g Boiler, PotWarmer) from the CoffeeMaker and take the risk
     * that some other class might decide to command the boiler to do something, the components are wrapped up in a
     * class that is only passed to classes derived from AbstractState that were created by the CoffeeMaker.
     */
    static class Components {

        /**
         * Actuators
         */

        private final Boiler boiler;
        private final PotWarmer potWarmer;

        /**
         * Sensors
         */

        private final WaterLevelSensor waterLevelSensor;
        private final PotSensor potSensor;
        private final BrewButtonSensor brewButton;

        /**
         * creates a Components object
         */
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
     * the base class of all CoffeeMaker states
     * <p/>
     * It never changes state and is useful for deriving state classes that only care about a subset of event sources.
     */
    abstract static class AbstractState {

        /**
         * @return the CoffeeMaker.State enum value that corresponds to a derived class
         */
        public abstract State getState();

        /**
         * called whenever the Brew button state changes
         */
        AbstractState onBrewButtonStateChanged(final Components components, final BrewButtonSensor.State newState) {
            return this;
        }

        /**
         * called whenever PotSensor state changes
         */
        AbstractState onPotStateChanged(final Components components, final PotSensor.State newState) {
            return this;
        }

        /**
         * called whenever the WaterLevelSensor state changes
         */
        AbstractState onWaterLevelStateChanged(final Components components, final WaterLevelSensor.State newState) {
            return this;
        }
    }

    /**
     * represents the possible states that the Coffee Pot can be in.
     * <p/>
     * The state can be interrogated by clients and unit tests (for State Verification).
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

        private final AbstractState state;

        State(AbstractState abstractState) {
            this.state = abstractState;
        }

        AbstractState get() {
            return state;
        }
    }

    // The state of the coffee maker
    private AbstractState state;

    // The components that the coffee maker interacts with
    private final Components components;

    /**
     * Creates a CoffeeMaker object
     *
     * @param boiler           the Boiler used to boil the water
     * @param potWarmer        the PotWarmer used to keep freshly brewed coffee warm
     * @param waterLevelSensor the WaterLevelSensor that detects whether or not there is water in the Boiler
     * @param potSensor        the PotSensor that detects if there is a CoffeePot on the WarmerPlate and, if there is,
     *                         whether or not it's empty.
     * @param brewButton       the Brew button that the use presses to initiate a brew cycle.
     */
    public CoffeeMaker(
            final Boiler boiler,
            final PotWarmer potWarmer,
            final WaterLevelSensor waterLevelSensor,
            final PotSensor potSensor,
            final BrewButtonSensor brewButton
    ) {
        checkNotNull(boiler, "boiler cannot be null");
        checkNotNull(potWarmer, "potWarmer cannot be null");
        checkNotNull(waterLevelSensor, "waterLevelSensor cannot be null");
        checkNotNull(potSensor, "potSensor cannot be null");
        checkNotNull(brewButton, "brewButton cannot be null");

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
     * Sensor State Change Handlers
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
