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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.persistence.IXMLPersistence;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Data object that stores information about resource assignement to tasks.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ResourceAssignment implements IXMLPersistence {
    /** The name of the resource to which task work has been assigned. */
    private String resourceName;
    /** The person id that corresponds to this resource. */
    private String resourceID;
    /** The the percentage of this resource's workday that has been assigned. */
    private int percentAssigned = 0;
    /** The total hours of task work assigned to this resource. */
    private int hoursAssigned = 0;
    /** The date on which work was assigned. */
    private Date assignmentDate;
    /** The tasks involved with this assignment. */
    private List tasks = new ArrayList();

    /**
     * Default Constructor.
     *
     */
    public ResourceAssignment() {
    }

    /**
	 * This constructor is used to create a class instance with following data:
	 * <ul>
	 * <li> Assignment Date </li>
	 * <li> Work Percent Assigned </li>
	 * <li> Resource Identification </li>
	 * </ul>
	 * 
	 * @param assignmentDate
	 *            Date where assignment was done.
	 * @param percentAssigned
	 *            Percent resource' work time that has been assigned to do this work.
	 * @param resourceID
	 *            Resource Identification.
	 */
    public ResourceAssignment(Date assignmentDate, int percentAssigned, String resourceID) {
        this.assignmentDate = assignmentDate;
        this.percentAssigned = percentAssigned;
        this.resourceID = resourceID;
    }

    /**
     * Get the name of the resource to which work has been assigned.
     *
     * @return a <code>String</code> containing the display name of the resource
     * to which this work was assigned.
     */
    public String getResourceName() {
        return this.resourceName;
    }

    /**
     * Set the display of the resource to which this work was assigned.
     *
     * @param resourceName a <code>String</code> value containing the display
     * name of a resource.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Get the percentage of the resource's work time that has been assigned to
     * do this work.
     *
     * @return a <code>int</code> value containing the percentage this resource
     * has been assigned to this work.
     */
    public int getPercentAssigned() {
        return this.percentAssigned;
    }

    /**
     * Set the percentage of the resource's work time that has been assigned to
     * do this work.
     *
     * @param percentAssigned a <code>int</code> value containing the percentage
     * of this resource's work day that has been assigned to do the work mentioned
     * in this object.
     */
    public void setPercentAssigned(int percentAssigned) {
        this.percentAssigned = percentAssigned;
    }

    /**
     * Get the number of hours this resource has been assigned to do this work.
     * This value is based on a standard 8 hour day (for now) until we support
     * more advanced resource scheduling.
     *
     * @return a <code>int</code> value indicating the number of house that have
     * been assigned.
     */
    public int getHoursAssigned() {
        return this.hoursAssigned;
    }

    /**
     * Set the number of hours that have been assigned on the given day for this
     * resource to work.
     *
     * @param hoursAssigned a <code>int</code> value indicating the number of
     * hours this resource would have to work on the given day to complete the
     * tasks assigned to him or her.
     */
    public void setHoursAssigned(int hoursAssigned) {
        this.hoursAssigned = hoursAssigned;
    }

    /**
     * Get the day of the work that this resource assignment refers to.  That is,
     * what date is this work assigned on.
     *
     * @return a <code>Date</code> value indicating the day on which work is to
     * be performed.
     */
    public Date getAssignmentDate() {
        return this.assignmentDate;
    }

    /**
     * Set the date on which the proposed work should be performed.
     *
     * @param assignmentDate a <code>Date</code> value on which the work should
     * be performed.
     */
    public void setAssignmentDate(Date assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    /**
     * Get the primary key that corresponds to the resource name indicated in
     * this <code>ResourceAssignment</code> object.  This value currently corresponds to a
     * person id, but it is named resource id to accomodate future changes where
     * a resource can be a person or a piece of equipment.
     *
     * @return an <code>String</code> value indicating the primary key of the
     * resource identified in the resource name field.
     */
    public String getResourceID() {
        return this.resourceID;
    }

    /**
     * Set the primary key that corresponds to the resource name indicated in
     * this <code>ResourceAssignment</code> object.  This value currently
     * corresponds to a person id, but it is named resource id to accommodate
     * future changes where a resource can be a person or a piece of equipment.
     *
     * @param resourceID an <code>String</code> value indicating the primary key of
     * the resource identified in the resource name field.
     */
    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    /**
     * Get XML that describes the data in this object.
     *
     * @return a <code>XMLDocument</code> which describes the data stored within.
     * @throws net.project.xml.document.XMLDocumentException if any error occurs while constructing the
     * XML document, such as ending an element twice.
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException {
        XMLDocument doc = new XMLDocument();

        doc.startElement("OverallocatedResource");
        doc.addElement("ResourceID", String.valueOf(this.resourceID));
        doc.addElement("ResourceName", getResourceName());
        doc.addElement("AssignmentDate", XMLUtils.formatISODateTime(this.assignmentDate));
        doc.addElement("PercentAssigned", String.valueOf(this.percentAssigned));
        doc.addElement("TaskNames", getTaskNames());
        doc.endElement();

        return doc;
    }

    /**
     * Get the tasks that the resource is assigned to on this date.
     *
     * @return a <code>List</code> object containing the tasks to which the
     * resource is assigned.
     */
    public List getTasks() {
        return this.tasks;
    }

    /**
     * Get a comma-delimited string of the tasks names assigned to this resource
     * assignment.
     *
     * @return a <code>String</code> containing a list of the the task names
     * to which this resource is assigned on this day.
     */
    public String getTaskNames() {
        return Conversion.toCommaSeparatedString(this.tasks);
    }

    /**
     * Add a task that this resource is assigned to on the given date.
     *
     * @param taskName a <code>String</code> value containing the name of the
     * task that the resource is assinged to.
     */
    public void addTask(String taskName) {
        tasks.add(taskName);
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXMLBody
     * @see IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<ResourceAllocation>");
        xml.append("  <ResourceID>").append(this.resourceID).append("</ResourceID>");
        xml.append("  <AssignmentDate>").append(this.assignmentDate).append("</AssignmentDate>");
        xml.append("  <PercentAssigned>").append(this.percentAssigned).append("</PercentAssigned>");
        xml.append("</ResourceAllocation>");

        return xml.toString();
    }


}
