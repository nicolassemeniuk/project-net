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

package net.project.resource;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.hibernate.model.PnAssignmentWork;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides a means for modifying <code>AssignmentWorkLogEntry</code>s.
 * 
 * @author Tim Morrow
 * @since Version 7.7.1
 */
public class AssignmentWorkLogDAO {
	
	private static Logger log = Logger.getLogger(AssignmentWorkLogDAO.class);

	/** SQL statement for storing a work log entry. */
	private static final String STORE_SQL = "{call SCHEDULE.STORE_ASSIGNMENT_WORK(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	
	/**
	 * Stores a collection of entries in a batch.
	 * 
	 * @param entries
	 *            the entries, where each element must be an <code>AssignmentWorkLogEntry</code>
	 * @throws PersistenceException
	 *             if there is a problem storing
	 */
	public void store(Collection entries) throws PersistenceException {
		DBBean db = new DBBean();
		try {
			db.setAutoCommit(false);

			store(entries, db);

			db.commit();

		} catch (Exception sqle) {
			try {
				db.rollback();
			} catch (SQLException e) {
				// Let earlier exception propogate
			}
			throw new PersistenceException(sqle);
		} finally {
			db.release();
		}

	}

	/**
	 * Stores a collection of entries in a batch.
	 * 
	 * @param entries
	 *            the entries, where each element must be an <code>AssignmentWorkLogEntry</code>
	 * @param db
	 *            the DBBean in which to perform the transaction
	 * @throws SQLException
	 *             if there is a problem storing
	 */
	public Integer[] store(Collection entries, DBBean db) {
		Integer[] assignmentWorkId = new Integer[entries.size()];
		try {
			int storedWorkIdIndex = 0;			
			for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
				AssignmentWorkLogEntry entry = (AssignmentWorkLogEntry) iterator.next();
				PnAssignmentWork assignmentWork = new PnAssignmentWork();
				assignmentWork.setPersonId(Integer.valueOf(entry.getAssigneeID()));
				assignmentWork.setObjectId(Integer.valueOf(entry.getObjectID()));
				assignmentWork.setWorkStart(new Timestamp(entry.getDatesWorked().getRangeStart().getTime()));
				assignmentWork.setWorkEnd(new Timestamp(entry.getDatesWorked().getRangeEnd().getTime()));
				assignmentWork.setWork(entry.getWork().getAmount());
				assignmentWork.setWorkUnits(entry.getWork().getUnits().getUniqueID());
				assignmentWork.setWorkRemaining(entry.getRemainingWork().getAmount());
				assignmentWork.setWorkRemainingUnits(entry.getRemainingWork().getUnits().getUniqueID());
				assignmentWork.setScheduledWork(entry.getScheduledWork().getAmount());
				assignmentWork.setScheduledWorkUnits(entry.getScheduledWork().getUnits().getUniqueID());
				assignmentWork.setPercentComplete(entry.getPercentComplete());
				assignmentWork.setLogDate(entry.getLogDate());
				assignmentWork.setModifiedBy(Integer.valueOf(entry.getModifiedByID()));
				assignmentWork.setComments(entry.getComment());
				assignmentWorkId[storedWorkIdIndex++] = ServiceFactory.getInstance().getScheduleService().storeAssignmentWork(assignmentWork);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return assignmentWorkId;
	}
    
    /**
     * Stores a single entry
     * @param a <code>AssignmentWorkLogEntry</code>
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem storing
     */
    public void store(AssignmentWorkLogEntry entry, DBBean db) throws SQLException {
        db.prepareCall(STORE_SQL);
        entry.setStoredProcParams(db.cstmt);
        db.executeCallable();
    }
    
    public void store(String personID,String id,String comment,DBBean db){
		PnAssignmentWork assignmentWork = new PnAssignmentWork();
		assignmentWork.setPersonId(Integer.valueOf(personID));
		assignmentWork.setObjectId(Integer.valueOf(id));
		assignmentWork.setComments(comment);
		ServiceFactory.getInstance().getScheduleService().storeAssignmentWork(assignmentWork);
    }

}