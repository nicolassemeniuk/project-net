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

 package net.project.admin.setting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.PnetRuntimeException;
import net.project.persistence.PersistenceException;

/**
 * Provides global system settings, loaded from the database with
 * default values provided by the deployment.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class SystemSettings {

    //
    // Static Members
    //

    /** The single instance of the settings. */
    private static SystemSettings SINGLETON;

    /**
     * Returns the singleton instance of loaded system settings.
     * @return the system settings
     * @throws net.project.base.PnetRuntimeException if there is a problem loading the settings
     */
    public static synchronized SystemSettings getInstance() {
        if (SINGLETON == null) {
            try {
                SINGLETON = new SystemSettings();
            } catch (PersistenceException e) {
                throw new PnetRuntimeException("Error loading system settings: " + e, e);
            }
        }
        return SINGLETON;
    }

    //
    // Instance Members
    //

    /**
     * The runtime settings.
     * Each key is a <code>String</code> name, each value a <code>String</code> setting.
     * value.
     */
    private final Map settings = new HashMap();

    /**
     * Creates and loads the system settings.
     * @throws PersistenceException if there is a problem loading
     */
    private SystemSettings() throws PersistenceException {

        // Fetch the default settings
        DefaultSettings defaultSettings = DefaultSettings.getInstance();

        // Load all the custom settings from the database
        Map loadedSettings = new HashMap();

        for (Iterator iterator = new SettingsFinder().findAll().iterator(); iterator.hasNext();) {
            Setting setting = (Setting) iterator.next();
            loadedSettings.put(setting.getName(), setting);
        }

        // Now build the map containing all the default setting names as keys
        // With the value equal to the loaded value or the default value
        for (Iterator iterator = defaultSettings.iterator(); iterator.hasNext();) {
            Setting defaultSetting = (Setting) iterator.next();

            String value;
            if (loadedSettings.containsKey(defaultSetting.getName())) {
                value = ((Setting) loadedSettings.get(defaultSetting.getName())).getValue();
            } else {
                value = defaultSetting.getValue();
            }

            settings.put(defaultSetting.getName(), value);

        }

    }

    /**
     * Returns the runtime value, either loaded from the database or
     * the default value.
     * @param name the name of the setting to get
     * @return the value
     * @throws IllegalArgumentException if the setting does not exist; that means it was
     * not defined in the default settings
     */
    public String getValue(String name) {
        String value = (String) this.settings.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Unknown system setting with name " + name + ".  Check the default settings.");
        }
        return value;
    }

}