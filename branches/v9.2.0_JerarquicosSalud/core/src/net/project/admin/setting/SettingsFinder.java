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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import net.project.base.finder.Finder;
import net.project.persistence.PersistenceException;

/**
 * Provides a finder for loading system setting values.
 * @author Tim Morrow
 */
class SettingsFinder extends Finder {

    /** The SQL statement used to load the system settings. */
    private static final String SQL = "select name, value from pn_system_setting";

    protected String getBaseSQLStatement() {
        return SQL;
    }

    protected Object createObjectForResultSetRow(ResultSet databaseResults) throws SQLException {

        Setting setting = new Setting(
                databaseResults.getString("name"),
                databaseResults.getString("value"));

        return setting;
    }

    /**
     * Returns a collection of all system settings that have
     * a database value.
     * @return a collection where each element is a <code>Setting</code>.
     * @throws PersistenceException if there is a problem loading
     */
    public Collection findAll() throws PersistenceException {
        return loadFromDB();
    }

}