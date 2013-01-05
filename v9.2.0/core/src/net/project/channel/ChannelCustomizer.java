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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.persistence.PersistenceException;
import net.project.resource.IPersonPropertyScope;
import net.project.resource.PersonProperty;

/**
 * Provides a mechanism to customize channel properties.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class ChannelCustomizer {

    //
    // Static Members
    //

    /**
     * Updates the state of the channel by storing the setting.
     * <p>
     * Note: No channel is updated in memory; the channel must be reloaded to see changes.
     * This is a departure from earlier behavior.  This is necessary to avoid
     * problems with using the browser "back" button; in-memory channels are often
     * different than what is passed here.
     * </p>
     * @param channelName the name of the channel to update
     * @param state the new state
     * @param personalSettingsManager the session-level sessions manager that updates the settings
     * @param scope
     * @throws PersistenceException if there is a problem storing
     * @throws IllegalArgumentException if no channel can be found for the specified name
     */
    public static void updateState(String channelName, State state, PersonProperty personalSettingsManager,
            IPersonPropertyScope scope) throws PersistenceException {

        personalSettingsManager.setScope(scope);
        ChannelProperties.updateState(personalSettingsManager, channelName, state);
    }

    //
    // Instance Members
    //

    /**
     * The channelHelpers.
     */
    private final Collection channelHelpers = new ArrayList();

    /**
     * The current scope.
     */
    private IPersonPropertyScope scope;


    /**
     * Initializes this manager with the channelHelpers.
     * <p>
     * The names and titles of the channels are specified as request parameters
     * <code>name</code> and <code>title</code>.
     * The properties are loaded from the personal settings for the scope determined by the
     * request parameters.  The changes are stored in the personal settings.
     * Channels must be reloaded to see changes.
     * </p>
     * @param request the request from which to get the personal settings manager
     * @param personalSettings the personal settings manager for persisting properties
     * @throws IllegalStateException if the channel names and titles received are incomplete
     */
    public void init(HttpServletRequest request, PersonProperty personalSettings) {

        this.scope = ScopeType.makeScopeFromRequest(request);

        // Change the scope of the settings to that determined from the request
        // The scope is passed in the URL so that use of the browser back button
        // will not affect the scope
        personalSettings.setScope(this.scope);

        // Get all the channels to customize
        String[] channelNames = request.getParameterValues("name");
        String[] channelTitles = request.getParameterValues("title");

        if (channelNames.length != channelTitles.length) {
            throw new IllegalStateException("Incomplete channel information received");
        }

        channelHelpers.clear();
        for (int i = 0; i < channelNames.length; i++) {
            this.channelHelpers.add(new ChannelHelper(channelNames[i], channelTitles[i], personalSettings));
        }

    }

    /**
     * Returns an unmodifiable collection of channel helpers.
     * @return a collection where each element is a <code>ChannelHelper</code>.
     */
    public Collection getChannelHelpers() {
        return Collections.unmodifiableCollection(this.channelHelpers);
    }

    /**
     * Process the values submitted in the request.
     * @param request the request containing attributes submitted
     * @throws PersistenceException if there is a problem storing
     */
    public void process(HttpServletRequest request) throws PersistenceException {

        int count = 0;
        for (Iterator it = channelHelpers.iterator(); it.hasNext(); count++) {
            ChannelHelper nextHelper = (ChannelHelper) it.next();

            String isDisplayed = request.getParameter("channelSeq_" + count);
            if (isDisplayed == null) {
                // Channel is not displayed
                // All other submitted values are ignored
                nextHelper.setState(State.CLOSED);

            } else {
                // Channel is displayed
                // Reset to Open only if closed; if currently Open or Minimized
                // We don't change it
                if (nextHelper.getState().equals(State.CLOSED)) {
                    nextHelper.setState(State.OPEN);
                }

                String frameTypeID = request.getParameter("frameTypeID_" + count);
                if (frameTypeID == null) {
                    throw new IllegalStateException("missing request attribute 'frameTypeID_" + count + "'");
                }
                FrameType frameType = FrameType.forID(frameTypeID);
                nextHelper.setFrameType(frameType);

                if (frameType.equals(FrameType.SCROLL)) {
                    // We also need a frame size
                    String frameSizeID = request.getParameter("frameSizeID_" + count);
                    if (frameSizeID == null) {
                        throw new IllegalStateException("missing request attribute 'frameTypeID_" + count + "'");
                    }
                    nextHelper.setFrameSize(FrameSize.forID(frameSizeID));
                }
            }

            // Now store the settings
            nextHelper.store();

        }

    }

    /**
     * Returns the scope's request parameters.
     * @return the request parameters
     */
    public String formatScopeRequestParameters() {
        return this.scope.formatRequestParameters();
    }


}
