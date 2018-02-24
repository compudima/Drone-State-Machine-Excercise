/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blue.catchy.drone.model;

import org.springframework.statemachine.annotation.WithStateMachine;

/**
 *
 * @author Dmitry Botvinnik
 */
@WithStateMachine
public class Drone {
    
    public static enum States {
        GROUNDED, TAKEOFF, PRE_PLANNED, STAY, RETURN_TO_BASE
    }

    public static enum Events {
        SEND_LAUNCH_COMMAND, EXECUTE_PRE_PLANNED_ROUTE, HOLD_POINT, SEND_RETURN_TO_BASE_COMMAND, AUTOMATIC_RETURN_TO_BASE, LOST_CONNECTION, APPROACHED_OBSTRUCTION, LAND
    }
    
}
