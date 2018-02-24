package blue.catchy.drone.statemachine;


//import blue.catchy.drone.config.StateMachineApplicationEventListener;
import blue.catchy.drone.model.Drone;
import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import blue.catchy.drone.model.Drone.States;
import blue.catchy.drone.model.Drone.Events;
import org.springframework.statemachine.StateContext;

@SpringBootApplication
public class DroneStateMachine implements CommandLineRunner {
    
    @Autowired
    private StateMachine<States, Events> stateMachine;
    
    public static void main(String[] args) {
        SpringApplication.run(DroneStateMachine.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("Executing a successful run");
        stateMachine.sendEvent(Events.SEND_LAUNCH_COMMAND);
        stateMachine.sendEvent(Events.EXECUTE_PRE_PLANNED_ROUTE);
        stateMachine.sendEvent(Events.HOLD_POINT);
        stateMachine.sendEvent(Events.SEND_RETURN_TO_BASE_COMMAND);
        stateMachine.sendEvent(Events.LAND);
        System.out.println("Executing a run that goes out of range");
        stateMachine.sendEvent(Events.SEND_LAUNCH_COMMAND);
        stateMachine.sendEvent(Events.EXECUTE_PRE_PLANNED_ROUTE);
        stateMachine.sendEvent(Events.LOST_CONNECTION);
        stateMachine.sendEvent(Events.AUTOMATIC_RETURN_TO_BASE);
        stateMachine.sendEvent(Events.LAND);
        System.out.println("Executing a run with an obstruction encounter");
        stateMachine.sendEvent(Events.SEND_LAUNCH_COMMAND);
        stateMachine.sendEvent(Events.EXECUTE_PRE_PLANNED_ROUTE);
        stateMachine.sendEvent(Events.APPROACHED_OBSTRUCTION);
        stateMachine.sendEvent(Events.HOLD_POINT);
        stateMachine.sendEvent(Events.SEND_RETURN_TO_BASE_COMMAND);
        stateMachine.sendEvent(Events.LAND);
    }

    @Configuration
    @EnableStateMachine
    public class StateMachineConfig
            extends EnumStateMachineConfigurerAdapter<Drone.States, Drone.Events> {

        @Override
        public void configure(StateMachineConfigurationConfigurer<Drone.States, Drone.Events> config)
                throws Exception {
            config
                .withConfiguration()
                    .autoStartup(true)
                    .listener(listener());
        }

        @Override
        public void configure(StateMachineStateConfigurer<Drone.States, Drone.Events> states)
                throws Exception {
            states
            .withStates()
            .initial(Drone.States.GROUNDED)
            .states(EnumSet.allOf(Drone.States.class));
        }

        @Override
        public void configure(StateMachineTransitionConfigurer<Drone.States, Drone.Events> transitions)
                throws Exception {
        transitions
            .withExternal()
            .event(Drone.Events.SEND_LAUNCH_COMMAND)
            .source(Drone.States.GROUNDED).target(Drone.States.TAKEOFF)
        .and()
            .withExternal()
            .event(Drone.Events.EXECUTE_PRE_PLANNED_ROUTE)
            .source(Drone.States.TAKEOFF).target(Drone.States.PRE_PLANNED)
        .and()
            .withExternal()
            .event(Drone.Events.HOLD_POINT)
            .source(Drone.States.PRE_PLANNED).target(Drone.States.STAY)
        .and()
            .withExternal()
            .event(Drone.Events.SEND_RETURN_TO_BASE_COMMAND)
            .source(Drone.States.STAY).target(Drone.States.RETURN_TO_BASE)
        .and()
            .withExternal()
            .event(Drone.Events.APPROACHED_OBSTRUCTION)
            .source(Drone.States.PRE_PLANNED).target(Drone.States.STAY)
        .and()
            .withExternal()
            .event(Drone.Events.LOST_CONNECTION)
            .source(Drone.States.PRE_PLANNED).target(Drone.States.RETURN_TO_BASE)
        .and()
            .withExternal()
            .event(Drone.Events.EXECUTE_PRE_PLANNED_ROUTE)
            .source(Drone.States.TAKEOFF).target(Drone.States.PRE_PLANNED)
        .and()
            .withExternal()
            .event(Drone.Events.AUTOMATIC_RETURN_TO_BASE)
            .source(Drone.States.STAY).target(Drone.States.RETURN_TO_BASE)
        .and()
            .withExternal()
            .event(Drone.Events.LAND)
            .source(Drone.States.RETURN_TO_BASE).target(Drone.States.GROUNDED);
        }

        @Bean
        public StateMachineListener<Drone.States, Drone.Events> listener() {
            return new StateMachineListenerAdapter<Drone.States, Drone.Events>() {
                @Override
                public void stateChanged(State<Drone.States, Drone.Events> from, State<Drone.States, Drone.Events> to) {
                    System.out.println("State change to " + to.getId());
                }
                
                @Override
                public void stateContext(StateContext stateContext) {
                    if(stateContext.getEvent() != null) {
                        if(stateContext.getStage().equals(stateContext.getStage().TRANSITION)) {
                            System.out.println("Event triggered: " + stateContext.getEvent().toString());
                        }
                    }
                }
                
                @Override
                public void stateMachineError(StateMachine s, Exception e) {
                    System.out.println("State machine experienced an error: " + e.getMessage());
                }
            };
        }
        
        //@Bean
        //public StateMachineApplicationEventListener contextListener() {
        //    return new StateMachineApplicationEventListener();
        //}
    }
}