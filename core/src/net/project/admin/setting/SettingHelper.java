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

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a helper for presenting and editing settings.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class SettingHelper {

    private final DefaultSetting defaultSetting;
    private final String currentValue;
    private boolean isDefault;
    private String customValue;

    /**
     * Creates a setting helper based on the specified default setting and the
     * optional loaded setting.
     * @param defaultSetting the default setting
     * @param loadedSetting the custom setting value loaded from the database;
     * when null, no custom value is displayed
     * @param currentValue the current value in-memory value of the setting used
     * to indicate when the stored value differs from runtime (indicating a server restart required)
     */
    SettingHelper(DefaultSetting defaultSetting, Setting loadedSetting, String currentValue) {
        this.defaultSetting = defaultSetting;
        this.currentValue = currentValue;

        this.isDefault = (loadedSetting == null);

        if (loadedSetting != null) {
            this.customValue = loadedSetting.getValue();
        } else {
            this.customValue = null;
        }

    }

    public String getName() {
        return this.defaultSetting.getName();
    }

    public String getDescription() {
        return this.defaultSetting.getDescription();
    }

    public String getDefaultValue() {
        return this.defaultSetting.getValue();
    }

    public boolean isFlagWarning() {
        return this.defaultSetting.isFlagWarning();
    }

    boolean isDefault() {
        return this.isDefault;
    }

    public String getDefaultRadioChecked() {
        return (isDefault() ? "checked" : "");
    }

    public String getCustomRadioChecked() {
        return (isDefault() ? "" : "checked");
    }

    /**
     * Returns the <code>disabled<code> attribute when the custom
     * input box must be disabled.
     * @return <code>disabled</code> or the empty string
     */
    public String getCustomInputDisabled() {
        return (isDefault() ? "disabled" : "");
    }

    private Collection getExampleValues() {
        return this.defaultSetting.getExampleValues();
    }

    /**
     * Returns the example values as a comma separate list.
     * @return the example values or the empty string if there are none
     */
    public String formatExampleValuesCommaSeparated() {
        StringBuffer values = new StringBuffer();
        for (Iterator iterator = getExampleValues().iterator(); iterator.hasNext();) {
            String nextValue = (String) iterator.next();
            if (values.length() > 0) {
                values.append(", ");
            }
            values.append(nextValue);
        }
        return values.toString();
    }

    public String getCustomValue() {
        return this.customValue;
    }

    /**
     * Returns the current runtime, in memory value.
     * @return the current runtime value
     */
    public String getCurrentRuntimeValue() {
        return this.currentValue;
    }

    /**
     * Indicates whether this setting's value differs from the current, in-memory runtime value.
     * <p>
     * This helps inform the user of pending changes.
     * </p>
     * @return true if this setting's value (custom or default depending on the selection) is
     * different from what is being used; false otherwise
     */
    public boolean isDifferentFromRuntime() {
        return ((this.currentValue == null && getResolvedValue() != null) ||
                (this.currentValue != null && !this.currentValue.equals(getResolvedValue())));
    }

    /**
     * Returns the default or custom value, depending on whether the default is selected.
     * @return the default value if default option selected; otherwise the custom value
     */
    private String getResolvedValue() {
        return (isDefault() ? getDefaultValue() : getCustomValue());
    }

    void setDefaultSelection() {
        this.isDefault = true;
        this.customValue = null;
    }

    void setCustomSelection(String customValue) {
        this.isDefault = false;
        this.customValue = customValue;
    }

}