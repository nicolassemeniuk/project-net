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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * @author Tim Morrow
 */
public class SettingsEditor {

    /**
     * The settings helpers for editing.
     */
    private final Collection settings = new ArrayList();

    /**
     * Creates a new SettingsEditor containing all the loaded settings
     * ready for editing.
     * @throws PersistenceException if there is a problem loading
     */
    public SettingsEditor() throws PersistenceException {
        init();
    }

    /**
     * Initializes the editor by loading the settings and the default settings
     * @throws PersistenceException if there is a problem loading
     */
    private void init() throws PersistenceException {

        this.settings.clear();

        // First, load all the settings from the database
        Map loadedSettings = new HashMap();

        for (Iterator iterator = new SettingsFinder().findAll().iterator(); iterator.hasNext();) {
            Setting setting = (Setting) iterator.next();
            loadedSettings.put(setting.getName(), setting);
        }

        // Get the current in-memory settings
        // We use this to determine which need flagged as changed
        SystemSettings runtimeSettings = SystemSettings.getInstance();

        // Get the default settings
        DefaultSettings defaultSettings = DefaultSettings.getInstance();

        // Now build the map containing all the default setting names as keys
        // With the value equal to the loaded value or the default value
        for (Iterator iterator = defaultSettings.iterator(); iterator.hasNext();) {
            DefaultSetting defaultSetting = (DefaultSetting) iterator.next();

            // Add a new helper constructed from the default setting
            // and any loaded setting
            this.settings.add(new SettingHelper(defaultSetting, (Setting) loadedSettings.get(defaultSetting.getName()), runtimeSettings.getValue(defaultSetting.getName())));

        }

    }

    /**
     * Returns an unmodifiable collection of the settings.
     * @return a collection where each element is a <code>SettingHelper</code>
     */
    public Collection getSettings() {
        return Collections.unmodifiableCollection(this.settings);
    }

    /**
     * Indicates whether any setting is different from the in-memory version.
     * <p>
     * This would imply a restart is required for changes to take effect.
     * </p>
     * @return true if a setting has a different value than the in memory setting;
     * false if all settings are identical.
     */
    public boolean isAnySettingDifferent() {
        boolean isAnySettingDifferent = false;

        for (Iterator iterator = getSettings().iterator(); iterator.hasNext() && !isAnySettingDifferent;) {
            SettingHelper settingHelper = (SettingHelper) iterator.next();
            if (settingHelper.isDifferentFromRuntime()) {
                isAnySettingDifferent = true;
            }
        }

        return isAnySettingDifferent;
    }

    /**
     * Processes submitted data, updates the settings helpers and
     * stores the modified values.
     * @param request the request containing the parameters for updating
     * @throws PersistenceException if there is a problem storing
     */
    public void process(HttpServletRequest request)
            throws PersistenceException {

        for (Iterator iterator = settings.iterator(); iterator.hasNext();) {
            SettingHelper settingHelper = (SettingHelper) iterator.next();

            String radioOptionName = "setting_" + settingHelper.getName() + "_select";
            String radioValue = request.getParameter(radioOptionName);

            String customValueName = "setting_" + settingHelper.getName() + "_customValue";
            String customValue = request.getParameter(customValueName);

            // Validate selections
            if (radioValue == null) {
                throw new IllegalStateException("Missing radio selection for setting " + settingHelper.getName());

            } else {
                if (radioValue.equals("default")) {
                    settingHelper.setDefaultSelection();
                } else {
                	if (customValue.trim().length() != 0) {
                		settingHelper.setCustomSelection(customValue);                		
                	}
                }
            }

        }

        // Now store the settings
        store();

    }

    /**
     * Stores all non-default settings by first deleting all settings then
     * inserting non-default settings only.
     * @throws PersistenceException if there is a problem storing
     */
    private void store() throws PersistenceException {
        final String insertQuery = "insert into pn_system_setting (name, value) values (?, ?)";

        DBBean db = new DBBean();

        try {
            db.setAutoCommit(false);

            // First delete all the existing settings
            deleteCustomValues(db);

            // Now insert all non-default rows
            boolean isWork = false;
            db.prepareStatement(insertQuery);

            for (Iterator iterator = settings.iterator(); iterator.hasNext();) {
                SettingHelper settingHelper = (SettingHelper) iterator.next();

                if (!settingHelper.isDefault()) {
                    isWork = true;
                    int index = 0;
                    db.pstmt.setString(++index, settingHelper.getName());
                    db.pstmt.setString(++index, settingHelper.getCustomValue());
                    db.pstmt.addBatch();
                }

            }

            if (isWork) {
                db.executePreparedBatch();
            }

            db.commit();

        } catch (SQLException e) {
            try {
                db.rollback();
            } catch (SQLException e1) {
                // Let original exception propogate
            }
            throw new PersistenceException("Error modifying settings: " + e, e);

        } finally {
            db.release();
        }

    }

    /**
     * Resets all settings back to their default values.
     * <p>
     * Equivalent to choosing the "default" radio option for all settings.
     * </p>
     * @throws PersistenceException if there is a problem deleting the values
     */
    public void resetDefault() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            deleteCustomValues(db);

        } catch (SQLException sqle) {
            throw new PersistenceException("Error deleting custom settings: " + sqle, sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Deletes all custom values.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem executing the query
     */
    private void deleteCustomValues(DBBean db) throws SQLException {
        db.executeQuery("delete from pn_system_setting");
    }

}