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

 package net.project.process;

import java.io.Serializable;

import net.project.code.TableCodeDomain;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;

/**
 * This Bean wraps Phase.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class PhaseBean extends Phase implements Serializable {
    /**
     * Construct an empty PhaseBean.
     */
    public PhaseBean() {
    }

    /**
     * Get an HTML option list of status options with the tasks current status
     * selected.
     *
     * @return a <code>String</code> containing an HTML option list of status
     * values.
     */
    public String getStatusOptionList() {
        TableCodeDomain domain = new TableCodeDomain();
        domain.setTableName("pn_phase");
        domain.setColumnName("status_id");
        domain.load();

        return domain.getOptionList(m_status_id);
    }

    /**
     * Get the Start Date as a string.
     *
     * @return a <code>String</code> containing the start date formatted properly
     * for the current user's locale.
     */
    public String getStartDateString() {
        User user = SessionManager.getUser();
        DateFormat formatter = user.getDateFormatter();

        return formatter.formatDate(getStart());
    }

    /**
     * Get the End Date as a string.
     *
     * @return String a <code>String</code> containing the end date of this
     * phase properly formatted for the current user's locale.
     */
    public String getEndDateString() {
        User user = SessionManager.getUser();
        DateFormat formatter = user.getDateFormatter();

        return formatter.formatDate(getEnd());
    }
}



