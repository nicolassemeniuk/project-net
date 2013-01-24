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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.process;

import java.io.Serializable;

import net.project.code.TableCodeDomain;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;

/**
 * Bean that extends the functionality of a gate object with presentation features.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class GateBean extends Gate implements Serializable {
    /**
     * Constructs an empty GateBean.
     */
    public GateBean() {
    }

    /**
     * Get an HTML option list of status options with the tasks current status
     * selected.
     *
     * @return String     HTML option list
     */
    public String getStatusOptionList() {
        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_gate");
        domain.setColumnName("status_id");

        domain.load();

        return domain.getOptionList(statusID);
    }

    /**
     * Get the current date of the gate in String format.
     *
     * @return String HTML option list
     */
    public String getDateString() {
        User user = SessionManager.getUser();
        DateFormat formatter = user.getDateFormatter();

        return formatter.formatDate(gateDate);
    }
}