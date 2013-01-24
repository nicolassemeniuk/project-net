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
|    $Revision: 19815 $
|        $Date: 2009-08-21 09:47:33 -0300 (vie, 21 ago 2009) $
|      $Author: ritesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.resource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.project.database.DBBean;
import net.project.hibernate.model.PnAssignment;
import net.project.persistence.PersistenceException;

/**
 * Provides an assignment to a meeting, created when an attendee is added to
 * a meeting.
 *
 * @author Tim Morrow
 * @since Version 7.6.2
 */
public class MeetingAssignment extends Assignment {

	/** Date on which assignment is to start. */
    private Date startTime = null;
    /** Date on which assignment completes. */
    private Date endTime = null;
    
    /**
     * Creates an empty MeetingAssignment.
     */
    public MeetingAssignment() {
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

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	 public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            super.storeAssignment(db, startTime, endTime, startTime, endTime, null, null, null, null);
            db.commit();
        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) { }
            throw new PersistenceException("Unable to store meeting assignment.", sqle);
        } finally {
            db.release();
        }
    }
	
	
//    @Override
//    protected void populateAssignment(PnAssignment pnAssignment, String timeZoneId) {
//        // Do nothing
//        
//    }

}
