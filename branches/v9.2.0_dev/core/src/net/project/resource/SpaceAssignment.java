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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.project.hibernate.model.PnAssignment;

/**
 * Provides an assignment to a space which is created when a person is invited
 * to a space.
 * <p>
 * With a <code>SpaceAssignment</code> the <code>spaceID</code> is set to
 * the person's personal space.  The <code>objectID</code> is set to the ID of the
 * space to which the person is invited.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class SpaceAssignment extends Assignment {
    
    /**
     * Creates an empty MeetingAssignment.
     */
    public SpaceAssignment() {
        super();
    }
    
    /**
     * Populates this assignment with schedule entry specific items.
     * Currently, there are none.
     * @param result the result set from which to get the data
     * @throws java.sql.SQLException if there is a problem reading the result set
     */
    protected void populateAssignment(ResultSet result) throws SQLException {
        // Do nothing
    }

//    @Override
//    protected void populateAssignment(PnAssignment pnAssignment, String timeZoneId) {
//        // Do nothing
//        
//    }

}
