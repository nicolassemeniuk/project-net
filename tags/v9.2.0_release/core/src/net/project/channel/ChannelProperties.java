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

import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;

/**
 * Provides properties for a channel including whether it
 * is on or off and how it is drawn.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
class ChannelProperties implements Serializable {

    //
    // Static Members
    //

    /** The default properties. */
    private static final ChannelProperties DEFAULT = new ChannelProperties(State.OPEN, FrameType.INLINE, FrameSize.MEDIUM);

    /** Prefix for storing channel property values. */
    static final String CHANNEL_PROPERTY_CONTEXT = "net.project.channel.";

    /** Name of the property for storing state. */
    private static final String PROPERTY_CHANNEL_STATE = "state";

    /** Name of the property for storing frame type. */
    private static final String PROPERTY_FRAME_TYPE = "frameType";

    /** Name of the property for storing frame size. */
    private static final String PROPERTY_FRAME_SIZE = "frameSize";

    /**
     * Returns default channel properties, currently Open and drawn without scrolling.
     * @return the default properties
     */
    static ChannelProperties makeDefault() {
        return DEFAULT;
    }

    /**
     * Loads the channel properties for the channel with the specified name.
     * When no value for each property is found, the default value as defined by {@link #DEFAULT}
     * is used.
     * @param name the name of the channel for which to load properties
     * @param personalSettingsManager the settings manager to use to load
     * @throws NullPointerException if name or personalSettingsManager is null
     */
    static ChannelProperties load(final String name, final PersonProperty personalSettingsManager) {
        return load(name, personalSettingsManager, DEFAULT.getState());
    }

    /**
     * Loads the channel properties for the channel with the specified name indicating the default state.
     * <p>
     * This allows individual channels to override the default state when no user-customized properties have been store.
     * Other default values are determined by {@link #DEFAULT}.
     * </p>
     * @param name the name of the channel for which to load properties
     * @param personalSettingsManager the settings manager to use to load
     * @param defaultState the default state to use when no state is loaded; overrides the default state
     * @throws NullPointerException if any of name, personalSettingsManager or defaultState are null
     */
    static ChannelProperties load(final String name, final PersonProperty personalSettingsManager, State defaultState) {

        if (name == null || personalSettingsManager == null || defaultState == null) {
            throw new NullPointerException("name, personalSettingsManager and defaultState are required");
        }

        final String context = CHANNEL_PROPERTY_CONTEXT + name;

        State state;
        String[] values = personalSettingsManager.get(context, PROPERTY_CHANNEL_STATE);
        if (values.length > 0) {
            state = State.forID(values[0]);
        } else {
            state = defaultState;
        }

        FrameType frameType;
        values = personalSettingsManager.get(context, PROPERTY_FRAME_TYPE);
        if (values.length > 0) {
            frameType = FrameType.forID(values[0]);
        } else {
            frameType = DEFAULT.getFrameType();
        }

        FrameSize frameSize;
        values = personalSettingsManager.get(context, PROPERTY_FRAME_SIZE);
        if (values.length > 0) {
            frameSize = FrameSize.forID(values[0]);
        } else {
            frameSize = DEFAULT.getFrameSize();
        }

        return new ChannelProperties(state, frameType, frameSize);
    }

    /**
     * Updates the state of a channel
     * @param personalSettingsManager the settings manager for storing
     * @param channelName the name of the channel
     * @param state the state of the channel
     * @throws PersistenceException if there is a problem storing
     */
    public static void updateState(PersonProperty personalSettingsManager, String channelName, State state)
            throws PersistenceException {
        updateProperty(personalSettingsManager, channelName, PROPERTY_CHANNEL_STATE, state.getID());
    }

    /**
     * Updates a property by only persisting the value if it has changed.
     * @param personalSettingsManager
     * @param name the name of the channel to update
     * @param property the property name
     * @param value the property value
     * @throws net.project.persistence.PersistenceException if there is a problem storing
     */
    private static void updateProperty(PersonProperty personalSettingsManager, String name, String property, String value)
            throws PersistenceException {

        String context = CHANNEL_PROPERTY_CONTEXT + name;

        // a get will access the property cache and not the database.
        // therefore get and compare to avoid a possible remove and put which
        // will result in a database hit
        String[] values = personalSettingsManager.get(context, property);
        if ((values.length == 0) || (!values[0].equals(value))) {
            // the stored state does not exist or does not equals the new state
            replaceProperty(personalSettingsManager, context, property, value);
        }
    }

    /**
     * Replaces the property value.
     * @param personalSettingsManager the settings manager to use
     * @param context the context
     * @param property the property name
     * @param value the value to replace with
     * @throws PersistenceException if there is a problem storing
     */
    private static void replaceProperty(PersonProperty personalSettingsManager, String context, String property, String value)
            throws PersistenceException {
        personalSettingsManager.removeAllValues(context, property);
        personalSettingsManager.put(context, property, value);
    }

    //
    // Instance Members
    //

    private State state;
    private FrameType frameType;
    private FrameSize frameSize;

    /**
     * Creates ChannelProperties.
     * @param state the current state
     * @param frameType the current frame type
     * @param frameSize the current frame size
     */
    ChannelProperties(State state, FrameType frameType, FrameSize frameSize) {
        this.state = state;
        this.frameType = frameType;
        this.frameSize = frameSize;
    }

    State getState() {
        return this.state;
    }

    FrameType getFrameType() {
        return this.frameType;
    }

    FrameSize getFrameSize() {
        return this.frameSize;
    }

    /**
     * Stores the channel properties for the channel with
     * the specified name.
     * @param name the name of the channel with which to associate the properties
     * @param personalSettingsManager the settings manager to use to store
     * @throws PersistenceException if there is a problem storing
     */
    void store(String name, PersonProperty personalSettingsManager) throws PersistenceException {
        // Now persist all the settings

        // State
        updateState(personalSettingsManager, name, getState());

        // FrameType
        updateProperty(personalSettingsManager, name, PROPERTY_FRAME_TYPE, getFrameType().getID());

        // FrameSize
        updateProperty(personalSettingsManager, name, PROPERTY_FRAME_SIZE, getFrameSize().getID());

    }

}
