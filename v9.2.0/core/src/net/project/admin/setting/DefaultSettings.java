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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.project.base.PnetRuntimeException;

/**
 * Provides access to system default settings that are deployed with the application.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
final class DefaultSettings implements Serializable {

    /** The path to the default settings file, relative to the classloader's root. */
    private static final String PROPERTIES_FILE_PATH = "/config/etc/defaultsettings.properties";

    /**
     * The VM-scoped default settings.
     */
    private static DefaultSettings SETTINGS = null;

    /**
     * Returns the VM-scoped system default settings, initializing them if necessary by reading
     * the properties file.
     * @return the settings
     * @throws PnetRuntimeException if there is a problem reading the settings
     */
    static synchronized DefaultSettings getInstance() {

        if (SETTINGS == null) {
            try {
                SETTINGS = new DefaultSettings();
            } catch (IOException e) {
                throw new PnetRuntimeException("Unable to load default system settings: " + e, e);
            }
        }

        return SETTINGS;
    }

    //
    // Instance memebers
    //

    /**
     * The default settings map. Each key is a <code>String</code> name, each value is a <code>Setting</code>.
     */
    private final Map settings = new HashMap();

    /**
     * Settings ordered in the way they are loaded from the properties file.
     */
    private final List orderedSettings = new ArrayList();

    /**
     * Creates new default settings from the properties file.
     * @throws IOException if there is a problem reading
     */
    private DefaultSettings() throws IOException {

        Properties props = new Properties();
        props.load(getClass().getResourceAsStream(PROPERTIES_FILE_PATH));

        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();

            // Names without "." are actual property values
            if (name.indexOf(".") < 0) {
                DefaultSetting setting = new DefaultSetting(name, props);
                this.settings.put(name, setting);
                this.orderedSettings.add(setting);
            }
        }

        // Sort the settings by sequence number
        Collections.sort(this.orderedSettings, new SequenceComparator());
    }

    /**
     * Returns the setting value for the specified setting name.
     * @param name the setting whose value to get
     * @return the setting or null if there is none with that name
     */
    DefaultSetting getSetting(String name) {
        return (DefaultSetting) this.settings.get(name);
    }

    /**
     * Provides an iterator over the default settings
     * in the order in which they were read from the configuration file.
     * @return an iterator where each element is a <code>DefaultSetting</code>.
     */
    Iterator iterator() {
        return this.orderedSettings.iterator();
    }

    /**
     * Provides a comparator of <code>DefaultSetting</code> objects on their sequence numbers.
     */
    private static class SequenceComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            DefaultSetting setting1 = (DefaultSetting) o1;
            DefaultSetting setting2 = (DefaultSetting) o2;

            int setting1Sequence = setting1.getSequence();
            int setting2Sequence = setting2.getSequence();
            return (setting1Sequence < setting2Sequence ? -1 : (setting1Sequence == setting2Sequence ? 0 : 1));
        }

    }
}