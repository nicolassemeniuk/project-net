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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import net.project.util.Validator;

/**
 * Provides a default setting; maintains a description value.
 */
class DefaultSetting extends Setting implements Serializable {

    /** The suffix for specifying the sequence number of the property. */
    private static final String SEQUENCE_SUFFIX = "seq";

    /** The suffix for the property that provides the description. */
    private static final String DESCRIPTION_SUFFIX = "description";

    /** The suffix for the properties that provide example value. */
    private static final String EXAMPLE_SUFFIX = "example";

    /** The suffix indicating we should warn the user about changing this setting. */
    private static final String WARNING_SUFFIX = "isFlagWarning";

    private final int sequence;
    private final String description;
    private final boolean isFlagWarning;
    private final Collection exampleValues = new ArrayList();

    /**
     * Creates a DefaultSetting by reading the property value,
     * examples and description for the property with the specified name.
     * @param name the name of the default property to create
     * @param props the Properties containing the other relevant values for the property name
     */
    DefaultSetting(String name, Properties props) {
        super(name, props.getProperty(name));

        String sequenceValue = props.getProperty(name + "." + SEQUENCE_SUFFIX);
        if (!Validator.isBlankOrNull(sequenceValue)) {
            this.sequence = Integer.valueOf(sequenceValue).intValue();
        } else {
            this.sequence = Integer.MAX_VALUE;
        }

        this.description = props.getProperty(name + "." + DESCRIPTION_SUFFIX);

        // Grab the example values for this property (zero or more)
        // We stop when an example is not found for the next sequence number
        boolean isDone = false;
        for (int exampleSequence = 1; !isDone; exampleSequence++) {
            String exampleName = name + "." + EXAMPLE_SUFFIX + "." + exampleSequence;
            String exampleValue = props.getProperty(exampleName);

            if (exampleValue != null) {
                exampleValues.add(exampleValue);
            } else {
                isDone = true;
            }
        }

        // Indicates a warning should be displayed cautioning the
        // changing of the property
        String flagWarningValue = props.getProperty(name + "." + WARNING_SUFFIX);
        if (!Validator.isBlankOrNull(flagWarningValue)) {
            this.isFlagWarning = Boolean.valueOf(flagWarningValue).booleanValue();
        } else {
            this.isFlagWarning = false;
        }
    }

    /**
     * Returns the description if this default setting.
     * <p>
     * This may be multi-line value.
     * </p>
     * @return the description
     */
    String getDescription() {
        return this.description;
    }

    /**
     * Indicates whether a warning should be presented when modifying
     * this setting.
     * <p>
     * This is typically required if changing the setting can disrupt the ability
     * to log in and modify the settings.
     * </p>
     * @return true if a warning should be flagged; false otherwise
     */
    boolean isFlagWarning() {
        return this.isFlagWarning;
    }

    /**
     * Returns the example value for this setting
     * @return an unmodifiable colleciton where each element is a <code>String</code>
     */
    Collection getExampleValues() {
        return Collections.unmodifiableCollection(exampleValues);
    }

    /**
     * Returns the sequence number of this setting.
     * <p>
     * This is used for ordering the setting.
     * </p>
     * @return the sequence number
     */
    public int getSequence() {
        return this.sequence;
    }

}