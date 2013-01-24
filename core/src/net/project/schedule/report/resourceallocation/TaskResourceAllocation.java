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

 package net.project.schedule.report.resourceallocation;

import java.math.BigDecimal;
import java.util.Date;

import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;
import net.project.xml.XMLUtils;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * This class contains data for the assignment of a resource to a task on a
 * certain day.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
class TaskResourceAllocation {
    /** The beginning of the reporting period for this task resource allocation. */
    private Date reportingPeriodStart;
    /** The day on which the resource will start this work on the task. */
    private Date allocationStartDate;
    /** The day on which the resource will complete this allocation of work. */
    private Date allocationFinishDate;
    /** The task that the resource was assigned to. */
    private String taskID;
    /** The name of the task that the resource was assigned to. */
    private String taskName;
    /** The duration of the task that the resource was assigned to. */
    private TimeQuantity taskDuration;
    /** The date that the task the resource was assigned to begins. */
    private Date startDate;
    /** The date that the task the resource was assigned to ends. */
    private Date finishDate;
    /** The name of the resource which was assigned to a task. */
    private String resourceName;
    /** The primary key identifier for the resource which was assigned to a task. */
    private String resourceID;
    /** The percent of the resource's day which was allocated to this task. */
    private int percentAllocated;
    /**
     * The total amount of work allocated for the resource and task in the given
     * time period.
     */
    private TimeQuantity totalAllocatedWork;
    /** The total amount of time required to complete the work for this task. */
    private TimeQuantity totalTaskWork;

    /**
     * Get the start of the reporting period that this task resource allocation
     * appears in.
     *
     * @return a <code>Date</code> value indicating the start of the reporting
     * period for this task resource allocation.
     */
    public Date getReportingPeriodStart() {
        return reportingPeriodStart;
    }

    /**
     * Set the start of the reporting period that this task resource allocation
     * appears in.
     *
     * @param reportingPeriodStart a <code>Date</code> value indicating the
     * start of the reporting period for this task resource allocation.
     */
    public void setReportingPeriodStart(Date reportingPeriodStart) {
        this.reportingPeriodStart = reportingPeriodStart;
    }

    /**
     * Get the day on which the resource will start this unit of work.
     *
     * @return a <code>Date</code> value describing when the resource was
     * assigned to the start this work on the task.
     */
    public Date getAllocationStartDate() {
        return allocationStartDate;
    }

    /**
     * Set the date that the resource was assigned to start working on this unit
     * of work on the task.
     *
     * @param allocationStartDate a <code>Date</code> value indicating when the
     * resource will start working on this unit of work for the task.
     */
    public void setAllocationStartDate(Date allocationStartDate) {
        this.allocationStartDate = allocationStartDate;
    }

    /**
     * Get the date on which the user will complete working on this unit of work
     * on the current task.
     *
     * @return a <code>Date</code> on which the user will finish working on the
     * current unit of work on the current task.
     */
    public Date getAllocationFinishDate() {
        return allocationFinishDate;
    }

    /**
     * Set the date on which the user will complete working on this unit of work
     * on the current task.
     *
     * @param allocationFinishDate a <code>Date</code> which indicates when the
     * user will be done with this unit of work.
     */
    public void setAllocationFinishDate(Date allocationFinishDate) {
        this.allocationFinishDate = allocationFinishDate;
    }

    /**
     * Get the task ID of a task that the resource was assigned to work on on the
     * day indicated by allocation date.
     *
     * @return a <code>String</code> value indicating the task id of the task
     * that the resource is going to be working on.
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * Set the ID of the task that the resource was assigned to.
     *
     * @param taskID a <code>String</code> value indicating a task that the
     * resource was assigned to on the allocation date.
     */
    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    /**
     * Get the name of a task that the resource was assigned to on the allocation
     * date.
     *
     * @return a <code>String</code> value containing the name of the task that
     * the resource was allocated to on the allocation date.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Set the name of a task that the resource was assigned to on the allocation
     * date.
     *
     * @param taskName a <code>String</code> value containing the name of a task
     * that the resource was assigned to work on the allocation date.
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Get the amount of time for which the resource was assigned to a task
     * identified by this object.
     *
     * @return a <code>TimeQuantity</code> representing the duration of the
     * task.
     */
    public TimeQuantity getTaskDuration() {
        return taskDuration;
    }

    /**
     * Set the duration of the task identified by this resource allocation.
     *
     * @param taskDuration a {@link net.project.util.TimeQuantity} identifying
     * the duration of the task identified by this object.
     */
    public void setTaskDuration(TimeQuantity taskDuration) {
        this.taskDuration = taskDuration;
    }

    /**
     * Get the start date of the task identified by this object.
     *
     * @return a <code>Date</code> object which identifies the start date of the
     * task identified by this object as being assigned to a person on a certain
     * day.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the start date of the task that this resource allocation is assigning
     * a person to work on.
     *
     * @param startDate a <code>Date</code> value which identifies the date on
     * which the task starts.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Get the finish date of the attached task.
     *
     * @return a <code>Date</code> value identifying when the attached task
     * is scheduled to be completed.
     */
    public Date getFinishDate() {
        return finishDate;
    }

    /**
     * Set the date on which the scheduled task is to be completed.
     *
     * @param finishDate a <code>Date</code> value specifying when the task is
     * scheduled to be completed.
     */
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    /**
     * Get the display name of a resource that is scheduled to do work on a task
     * also identified by this object.
     *
     * @return a <code>String</code> value which contains the display name of a
     * resource which will be working on a task.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Set the display name of a resource that is scheduled to do work on a task
     * also identified on by this object.
     *
     * @param resourceName a <code>String</code> value which contains the display
     * name of a resource working on the task identified by the {@link #setTaskID}
     * method.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Get the percent of a resource's work day that is allocated to a certain
     * task {@link #setTaskID} on the {@link #allocationStartDate}.
     *
     * @return a <code>int</code> value containing the percentage of the
     * resource's work day allocated to the task.
     */
    public int getPercentAllocated() {
        return percentAllocated;
    }

    /**
     * Set the percent of a resource's work day is allocated to a certain task
     * on the {@link #allocationStartDate}.
     *
     * @param percentAllocated an <code>int</code> value indicating the amount
     * of a resource's work day is allocated to the task identified by this
     * object on the {@link #allocationStartDate}.
     */
    public void setPercentAllocated(int percentAllocated) {
        this.percentAllocated = percentAllocated;
    }

    /**
     * Get an XML Document object which contains all the information encapsulated
     * in this data object.
     *
     * @return a <code>XMLDocument</code> object which contains all the
     * information encapsulated in this object.
     * @throws XMLDocumentException if an error occurs while assembling the XML
     * Document.
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException {
        XMLDocument doc = new XMLDocument();

        doc.startElement("TaskResourceAllocation");
        doc.addElement("AllocationDate", XMLUtils.formatISODateTime(allocationStartDate));
        doc.addElement("TaskID", taskID);
        doc.addElement("TaskName", taskName);
        doc.addElement("TaskDuration", taskDuration.toString());
        doc.addElement("StartDate", XMLUtils.formatISODateTime(startDate));
        doc.addElement("FinishDate", XMLUtils.formatISODateTime(finishDate));
        doc.addElement("ResourceName", resourceName);
        doc.addElement("PercentAllocated", String.valueOf(percentAllocated));
        doc.addElement("AllocatedHours", String.valueOf(getAllocatedHours()));
        doc.endElement();

        return doc;
    }

    /**
     * Get the resource assigned to this task.
     *
     * @return a <code>String</code> value containing the primary key of the
     * resource assigned to this task.
     */
    public String getResourceID() {
        return resourceID;
    }

    /**
     * Set the primary key for the resource assigned to this task.
     *
     * @param resourceID a <code>String</code> value containing the primary key
     * of the resource assigned to the task this object identifies.
     */
    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public void setAllocatedWork(TimeQuantity allocatedWork) {
        totalAllocatedWork = allocatedWork;
    }

    /**
     * Get the number of hours that this resource allocation is allocating to
     * the task.  This is based on the WorkingTime calendars defined for the
     * current schedule.
     *
     * @return a {@link java.math.BigDecimal} which contains the number of hours
     * which have been allocated in this resource allocation.
     */
    public BigDecimal getAllocatedHours() {
        if (totalAllocatedWork.getUnits() != TimeQuantityUnit.HOUR) {
            totalAllocatedWork = totalAllocatedWork.convertTo(TimeQuantityUnit.HOUR, 2);
        }

        return totalAllocatedWork.getAmount();
    }

    /**
     * Get the total amount of time allocated to work on the task identified by
     * {@link #getTaskID}.
     *
     * @see #getAllocatedHours
     * @return a <code>TimeQuantity</code> object indicating the total amount of
     * time allocated by the <code>TimeResourceAllocation</code> to work on the
     * task.
     */
    public TimeQuantity getAllocatedWork() {
        return new TimeQuantity(getAllocatedHours(), TimeQuantityUnit.HOUR);
    }

    /**
     * Set the total number of hours required to complete this task.
     *
     * @param timeQuantity a {@link net.project.util.TimeQuantity} value
     * indicating the total amount of time required to completely finish this
     * task.
     */
    public void setTotalTaskWork(TimeQuantity timeQuantity) {
        this.totalTaskWork = timeQuantity;
    }

    /**
     * Get the total amount of time required to complete the task referenced by
     * {@link #getTaskID} in this object.
     *
     * @return a {@link net.project.util.TimeQuantity} object which indicates
     * the amount of time required to complete this task.
     */
    public TimeQuantity getTotalTaskWork() {
        return totalTaskWork;
    }

}
