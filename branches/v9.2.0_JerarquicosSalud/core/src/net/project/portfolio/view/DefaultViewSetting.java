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

 /*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

/**
 * Provides a default view setting.
 * There may be different default views for a given view context.
 * For example, Default view on Portfolio, Default view on Personal Space Dashboard.
 * Each of these is called a scenario.
 * This setting specifies the default view ID for a particular scenario.
 * A view ID may be null, meaning there is no default view for the scenario
 * defined by this setting.
 *
 * @since Version 7.4
 * @author Tim Morrow
 */
public class DefaultViewSetting {

    private final DefaultScenario scenario;

    private String viewID;

    /**
     * Creates a new setting for the specified scenario and with the specified
     * default view ID.
     *
     * @param scenario the scenario to which this default view applies
     * @param viewID the ID of the default view for the specified scenario
     */
    DefaultViewSetting(DefaultScenario scenario, String viewID) {
        this.scenario = scenario;
        this.viewID = viewID;
    }

    /**
     * Returns the scenario to which the default view ID applies.
     *
     * @return the scenario
     */
    public DefaultScenario getScenario() {
        return this.scenario;
    }

    /**
     * Indicates whether this setting actually has a default view ID value.
     *
     * @return true if there is a default view for the current scenario;
     * false otherwise
     */
    public boolean hasValue() {
        return (getDefaultID() != null);
    }

    public void setDefaultID(String viewID) {
        this.viewID = viewID;
    }

    /**
     * Returns the ID of the default view for the current scenario.
     *
     * @return the default view ID
     */
    public String getDefaultID() {
        return this.viewID;
    }

    public void clearDefaultID() {
        this.viewID = null;
    }

    /**
     * Indicates whether the specified view ID matches the default view ID in
     * this setting.
     *
     * @param viewID the ID of the view to check
     * @return true if the specified viewID matches the viewID in this setting;
     * false otherwise.  If this setting has no default value or the specified
     * view ID is null, false is returned.
     */
    public boolean isDefault(String viewID) {
        return (hasValue() && (viewID != null) && getDefaultID().equals(viewID));
    }
}

