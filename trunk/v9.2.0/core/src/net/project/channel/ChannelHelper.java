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

import net.project.gui.html.HTMLOptionList;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;

/**
 * Provides a helper for editing channel properties.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ChannelHelper {

    /** The channel's internal name. */
    private final String name;

    /** The read only channel displayName. */
    private final String displayName;

    /** Indicates whether the channel is displayed or not. */
    private State state;
    private FrameType frameType;
    private FrameSize frameSize;

    /**
     * The settings provider.
     */
    private final PersonProperty settings;

    /**
     * Creates a new helper to edit the specified channel's properties.
     * <p>
     * Note: The settings must already be set to the correct scope (that is, the scope in which the
     * channel's properties can be found).
     * </p>
     * @param name the name of the channel; used to look up the channel properties from
     * the settings
     * @param title the title of the channel
     * @param settings the settings manager used to get the channel properties
     */
    ChannelHelper(String name, String title, PersonProperty settings) {
        this.name = name;
        this.displayName = title;

        ChannelProperties properties = ChannelProperties.load(name, settings);
        this.state = properties.getState();
        this.frameType = properties.getFrameType();
        this.frameSize = properties.getFrameSize();

        this.settings = settings;
    }

    /**
     * Returns the internal name of the channel.
     * @return the name of the channel
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the display name of the channel.
     * @return the channel display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Specifies the state of the channel.
     * @param state the state
     */
    void setState(State state) {
        this.state = state;
    }

    /**
     * Returns the current state.
     * @return the current state
     */
    State getState() {
        return this.state;
    }

    /**
     * Indicates whether the channel is displayed based on it state.
     * @return true if the channel is not closed; false if the channel is closed
     */
    private boolean isDisplayed() {
        return !this.state.equals(State.CLOSED);
    }

    /**
     * Returns the attribute "checked" or empty string
     * depending on whether the channel is currently being displayed.
     * @return <code>checked</code> or empty string
     */
    public String getDisplayedCheckedAttribute() {
        return (isDisplayed() ? "checked" : "");
    }

    /**
     * Specifies the type of frame to draw the channel in.
     * @param frameType the frame type
     */
    void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }

    /**
     * Returns the attribute "checked" or empty string
     * depending on whether the specified frame type ID is
     * the selected frame type.
     * @param frameType the frame type for which to get the checked attribute
     * @return <code>checked</code> if the channel's selected frame
     * type matches the specified frame type
     */
    public String getFrameTypeCheckedAttribute(FrameType frameType) {
        return (this.frameType.equals(frameType) ? "checked" : "");
    }

    /**
     * Specifies the size of frame to draw the channel in.
     * @param frameSize the frame size
     */
    void setFrameSize(FrameSize frameSize) {
        this.frameSize = frameSize;
    }

    /**
     * Indicates whether the frame size property is enabled.
     * <p>
     * It is enabled only if frame type is SCROLL.
     * </p>
     * @return true if the frame size property is enabled; false otherwise
     */
    public boolean isFrameSizeEnabled() {
        return (this.frameType.equals(FrameType.SCROLL));
    }

    /**
     * Returns HTML Options for selecting the frame size property.
     * <p>
     * The currently selected frame size is selected.
     * </p>
     * @return the HTML options
     */
    public String getFrameSizeOptions() {
        return HTMLOptionList.makeHtmlOptionList(FrameSize.getAll(), frameSize);
    }

    /**
     * Updates the modified channel properties in the current settings.
     * @throws PersistenceException if there is a problem storing
     */
    public void store() throws PersistenceException {

        ChannelProperties channelProperties = new ChannelProperties(this.state, this.frameType, this.frameSize);
        channelProperties.store(this.name, this.settings);
    }

}
