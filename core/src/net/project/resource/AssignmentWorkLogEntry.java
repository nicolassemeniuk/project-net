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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.database.DatabaseUtils;
import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;
import net.project.util.DateRange;
import net.project.util.StringUtils;
import net.project.util.TimeQuantity;
import net.project.xml.document.XMLDocument;

/**
 * Represents some work a user has reported as being completed for an
 * assignment.
 * 
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class AssignmentWorkLogEntry implements IXMLPersistence {
	private String assignmentWorkID;

	private String objectID;

	private String objectName;

	private String assigneeID;

	private String assigneeName;

	private DateRange datesWorked;

	private TimeQuantity hoursWorked;

	private TimeQuantity remainingWork;

	private TimeQuantity totalAssignmentWork;

	private TimeQuantity totalRemainingWork;

	private TimeQuantity scheduledWork;

	private BigDecimal percentComplete;

	private Date logDate;

	private String modifiedBy;

	private String comment;

	private String modifiedByID;
	
	private String spaceName;
	
	private int sequenceNo;
	
	private String classId;
	
	private String objectType;

	public String getAssignmentWorkID() {
		return this.assignmentWorkID;
	}

	public void setAssignmentWorkID(String assignmentWorkID) {
		this.assignmentWorkID = assignmentWorkID;
	}

	public String getObjectID() {
		return this.objectID;
	}

	public void setObjectID(String objectID) {
		this.objectID = objectID;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getAssigneeID() {
		return this.assigneeID;
	}

	public void setAssigneeID(String assigneeID) {
		this.assigneeID = assigneeID;
	}

	public String getAssigneeName() {
		return this.assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public DateRange getDatesWorked() {
		return this.datesWorked;
	}

	public void setDatesWorked(DateRange datesWorked) {
		this.datesWorked = datesWorked;
	}

	public TimeQuantity getWork() {
		return this.hoursWorked;
	}

	public void setHoursWorked(TimeQuantity hoursWorked) {
		this.hoursWorked = hoursWorked;
	}

	public TimeQuantity getRemainingWork() {
		return this.remainingWork;
	}

	public void setRemainingWork(TimeQuantity remainingWork) {
		this.remainingWork = remainingWork;
	}

	public BigDecimal getPercentComplete() {
		return percentComplete;
	}

	/**
	 * Sets the percent complete where <code>1.00</code> equals 100%.
	 * 
	 * @param percentComplete the percent complete
	 */
	public void setPercentComplete(BigDecimal percentComplete) {
		this.percentComplete = percentComplete;
	}

	public Date getLogDate() {
		return this.logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public String getModifiedByID() {
		return this.modifiedByID;
	}

	public void setModifiedByID(String modifiedByID) {
		this.modifiedByID = modifiedByID;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public TimeQuantity getScheduledWork() {
		return this.scheduledWork;
	}

	public void setScheduledWork(TimeQuantity scheduledWork) {
		this.scheduledWork = scheduledWork;
	}

	void setStoredProcParams(PreparedStatement stmt) throws SQLException {
		int index = 0;
		DatabaseUtils.setInteger(stmt, ++index, this.assigneeID);
		DatabaseUtils.setInteger(stmt, ++index, this.objectID);
		DatabaseUtils.setTimestamp(stmt, ++index, this.datesWorked.getRangeStart());
		DatabaseUtils.setTimestamp(stmt, ++index, this.datesWorked.getRangeEnd());
		stmt.setDouble(++index, this.hoursWorked.getAmount().doubleValue());
		stmt.setInt(++index, this.hoursWorked.getUnits().getUniqueID());
		stmt.setDouble(++index, this.remainingWork.getAmount().doubleValue());
		stmt.setInt(++index, this.remainingWork.getUnits().getUniqueID());
		stmt.setDouble(++index, this.scheduledWork.getAmount().doubleValue());
		stmt.setInt(++index, this.scheduledWork.getUnits().getUniqueID());
		DatabaseUtils.setBigDecimal(stmt, ++index, this.percentComplete);
		stmt.setBoolean(++index, false);
		DatabaseUtils.setInteger(stmt, ++index, this.modifiedByID);
		stmt.setString(++index, this.comment);
	}

	public String getXML() {
		return getXMLDocument().getXMLString();
	}

	public String getXMLBody() {
		return getXMLDocument().getXMLBodyString();
	}

	XMLDocument getXMLDocument() {
		XMLDocument xml = new XMLDocument();
		xml.startElement("AssignmentWorkLogEntry");
		xml.addElement("SpaceName", this.spaceName);
		xml.addElement("ObjectID", this.objectID);
		xml.addElement("ContextUrl", getContextUrl(this.objectType));
		if(this.objectType.equals(ObjectType.FORM_DATA) && StringUtils.isNotEmpty(this.classId)){
			xml.addElement("ObjectName", new AssignmentStoreDataFactory().getFormAbbreviationByClassId(Integer.parseInt(this.classId))
					+ this.sequenceNo + ": " +this.objectName );
		} else {
			xml.addElement("ObjectName", this.objectName);			
		}
	
		xml.addElement("PersonID", this.assigneeID);
		if (this.datesWorked != null) {
			xml.addElement("StartDate", this.datesWorked.getRangeStart());
			xml.addElement("EndDate", this.datesWorked.getRangeEnd());
			xml.addElement("Comment", this.comment);
		}
		if (this.hoursWorked != null) {
			xml.addElement("HoursWorkedFormatted", this.hoursWorked.toShortString(0, 2));
		}
		if (this.totalAssignmentWork != null) {
			xml.addElement("TotalWorkFormatted", this.totalAssignmentWork.toShortString(0, 2));
		}
		if (this.totalRemainingWork != null) {
			xml.addElement("TotalRemainingWorkFormatted", this.totalRemainingWork.toShortString(0, 2));
		}

		xml.addElement("PercentComplete", this.percentComplete);
		xml.addElement("LogDate", this.logDate);
		xml.addElement("ModifiedByID", this.modifiedByID);
		xml.addElement("ModifiedBy", this.modifiedBy);
		xml.addElement("AssigneeName", this.assigneeName);
		xml.endElement();
		return xml;
	}

	public TimeQuantity getTotalAssignmentWork() {
		return this.totalAssignmentWork;
	}

	public void setTotalAssignmentWork(TimeQuantity totalAssignmentWork) {
		this.totalAssignmentWork = totalAssignmentWork;
	}

	public TimeQuantity getTotalRemainingWork() {
		return this.totalRemainingWork;
	}

	public void setTotalRemainingWork(TimeQuantity totalRemainingWork) {
		this.totalRemainingWork = totalRemainingWork;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}

	/**
	 * @return the classId
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * @param classId the classId to set
	 */
	public void setClassId(String classId) {
		this.classId = classId;
	}
	
	/**
	 * @return the sequenceNo
	 */
	public int getSequenceNo() {
		return sequenceNo;
	}

	/**
	 * @param sequenceNo the sequenceNo to set
	 */
	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	/**
	 * @return the contextUrl
	 */
	public String getContextUrl(String objectType) {
		String contextUrl = URLFactory.makeURL(getObjectID(),objectType, true);
		if(objectType.equals(ObjectType.FORM_DATA)){
			contextUrl = contextUrl + "&spaceID="+SessionManager.getUser().getCurrentSpace().getID()+ "&redirectedFromSpace=true";
		}
		return contextUrl;
	}
	
	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

}
