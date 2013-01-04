/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.channel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides an enumeration of Channel States.
 *
 * @since Version 7.6.3
 * @author Tim Morrow
 */
public class State implements Serializable {

    /**
     * All the states.
     * Each key is a <code>String</code> internal ID and each
     * element is a <code>State</code>.
     */
    private static final Map allStates = new HashMap();

    /** OPEN channel state. */
    public static final State OPEN = new State("0", "open");

    /** Minimized channel state. */
    public static final State MINIMIZED = new State("1", "minimized");

    /** Closed channel state. */
    public static final State CLOSED = new State("2", "closed");

    /**
     * Returns the State for the specified ID.
     * @param stateID the ID of the state to get
     * @return the state
     * @throws IllegalArgumentException if no state is found for that ID
     */
    public static State forID(String stateID) {
        State state = (State) State.allStates.get(stateID);
        if (state == null) {
            throw new IllegalArgumentException("No state found for ID " + stateID);
        }
        return state;
    }

    /** The ID of the state. */
    private final String stateID;

    /** The internal name of the state. */
    private final String internalName;

    /**
     * Creates a new State with the specified ID and internal name.
     * @param stateID the unique ID of the state
     * @param internalName the internal name of the state
     */
    private State(String stateID, String internalName) {
        this.stateID = stateID;
        this.internalName = internalName;
        State.allStates.put(stateID, this);
    }

    /**
     * Returns the internal ID of the state.
     * @return the internal ID
     */
    public String getID() {
        return this.stateID;
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof State)) {
            return false;
        }

        final State state = (State) o;

        if (!stateID.equals(state.stateID)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return stateID.hashCode();
    }

    /**
     * Returns the string representation of this sate
     * suitable for debugging.
     * @return the string representation
     */
    public String toString() {
        return this.stateID + " " + this.internalName;
    }

}
