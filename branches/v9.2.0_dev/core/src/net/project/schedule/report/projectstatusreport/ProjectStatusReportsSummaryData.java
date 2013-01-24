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
 |    $RCSfile$NAME.java,v $
 |   $Revision: 15404 $
 |       $Date: 2006-08-28 20:20:09 +0530 (Mon, 28 Aug 2006) $
 |     $Author: deepak $
 |
 |
 +--------------------------------------------------------------------------------------*/
package net.project.schedule.report.projectstatusreport;

import java.util.Date;

import net.project.code.ColorCode;
import net.project.code.ImprovementCode;

/**
 * Class to store all of the data that will appear in the summary section of
 * various Project Status Summary Report.
 * 
 * @author K B Deepak
 * @since Version 1.0
 */
public class ProjectStatusReportsSummaryData {
    /**
     * Project Last Updated in the schedule.
     */
    private Date projectLastUpdated = null;

    /**
     * Project Business Area in the schedule.
     */
    private String projectBusinessArea = null;

    /**
     * Projcet Name in the schedule.
     */
    private String projectName = null;

    /**
     * Project Number in the schedule.
     */
    private String projectNumber = null;

    /**
     * Project Description in the schedule.
     */
    private String projectDescription = null;

    /**
     * Project Start Date in the schedule.
     */
    private Date projectStartDate = null;

    /**
     * Project Finish Date in the schedule.
     */
    private Date projectFinishDate = null;

    /**
     * Project Overall Completion percentage in the schedule.
     */
    private String projectOverallComplete = null;

    /**
     * Project Overall Stage in the schedule.
     */
    private String projectOverallStage = null;

    /**
     * The color code of this project.
     */
    private ColorCode colorCode = null;

    /**
     * The HTTP URL to the image file representing the current color.
     */
    private String colorImgURL = null;

    /**
     * Indicates this project's overall improvement.
     */
    private ImprovementCode improvementCode = null;

    /**
     * The color of the financial status.
     */
    private ColorCode financialStatusColorCode = null;

    /**
     * The improvment of the financial status.
     */
    private ImprovementCode financialStatusImprovementCode = null;

    /**
     * Color of schedule status.
     */
    private ColorCode scheduleStatusColorCode = null;

    /**
     * Improvement of schedule status.
     */
    private ImprovementCode scheduleStatusImprovementCode = null;

    /**
     * Color of resource status.
     */
    private ColorCode resourceStatusColorCode = null;

    /**
     * Improvement of resource status.
     */
    private ImprovementCode resourceStatusImprovementCode = null;

    /**
     * Project Overall Status in the schedule.
     */
    private String projectOverallStatus = null;

    /**
     * Project Project Comments in the schedule.
     */
    private String projectStatusComments = null;

    /**
     * Get the Project last updated date.
     * 
     * @return an <code>date</code> value that indicates the last updated date.
     * @see #setProjectLastUpdated
     */
    public Date getProjectLastUpdated() {
        return projectLastUpdated;
    }

    /**
     * Set the Project last updated date.
     * 
     * @param projectLastUpdated the new <code>Date</code> value that will represent the
     *            project last updated date.
     * @see #getProjectLastUpdated
     */
    public void setProjectLastUpdated(Date projectLastUpdated) {
        this.projectLastUpdated = projectLastUpdated;
    }

    /**
     * Get the Project business area.
     * 
     * @return an <code>String</code> value that indicates the business area.
     * @see #setProjectBusinessArea
     */
    public String getProjectBusinessArea() {
        return projectBusinessArea;
    }

    /**
     * Set the Project Business area.
     * 
     * @param projectBusinessArea the new <code>String</code> value that will represent the
     *            project business area.
     * @see #getProjectBusinessArea
     */
    public void setProjectBusinessArea(String projectBusinessArea) {
        this.projectBusinessArea = projectBusinessArea;
    }

    /**
     * Get the Project name.
     * 
     * @return an <code>String</code> value that indicates the project name.
     * @see #setProjectName
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Set the Project name.
     * 
     * @param projectName the new <code>String</code> value that will represent the
     *            project name.
     * @see #getProjectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Get the Project number.
     * 
     * @return an <code>String</code> value that indicates the project number.
     * @see #setProjectNumber
     */
    public String getProjectNumber() {
        return projectNumber;
    }

    /**
     * Set the Project number.
     * 
     * @param projectNumber the new <code>String</code> value that will represent the
     *            project number.
     * @see #getProjectNumber
     */
    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    /**
     * Get the Project description.
     * 
     * @return an <code>String</code> value that indicates the project description.
     * @see #setProjectDescription
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     * Set the Project description.
     * 
     * @param projectDescription the new <code>String</code> value that will represent the
     *            project description.
     * @see #getProjectDescription
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * Get the Project start date.
     * 
     * @return an <code>Date</code> value that indicates the project start date.
     * @see #setProjectStartDate
     */
    public Date getProjectStartDate() {
        return projectStartDate;
    }

    /**
     * Set the Project start date.
     * 
     * @param projectStartDate the new <code>Date</code> value that will represent the
     *            project start date.
     * @see #getProjectStartDate
     */
    public void setProjectStartDate(Date projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    /**
     * Get the Project finish date.
     * 
     * @return an <code>Date</code> value that indicates the project finish date.
     * @see #setProjectFinishDate
     */
    public Date getProjectFinishDate() {
        return projectFinishDate;
    }

    /**
     * Set the Project Finish Date.
     * 
     * @param projectFinishDate the new <code>String</code> value that will represent the
     *            project finish date.
     * @see #getProjectFinishDate
     */
    public void setProjectFinishDate(Date projectFinishDate) {
        this.projectFinishDate = projectFinishDate;
    }

    /**
     * Get the Project overall complete.
     * 
     * @return an <code>String</code> value that indicates the project overall complete.
     * @see #setProjectOverallComplete
     */
    public String getProjectOverallComplete() {
        return projectOverallComplete;
    }

    /**
     * Set the Project overall complete.
     * 
     * @param projectOverallComplete the new <code>String</code> value that will represent the
     *            project overall complete.
     * @see #getProjectOverallComplete
     */
    public void setProjectOverallComplete(String projectOverallComplete) {
        this.projectOverallComplete = projectOverallComplete;
    }

    /**
     * Get the Project overall stage.
     * 
     * @return an <code>String</code> value that indicates the project overall stage.
     * @see #setProjectOverallStage
     */
    public String getProjectOverallStage() {
        return projectOverallStage;
    }

    /**
     * Set the Project overall stage.
     * 
     * @param projectOverallStage the new <code>String</code> value that will represent the
     *            project overall stage.
     * @see #getProjectOverallStage
     */
    public void setProjectOverallStage(String projectOverallStage) {
        this.projectOverallStage = projectOverallStage;
    }

    /**
     * Sets the color code of this project.
     * 
     * @param colorCode
     *            the color code
     * @see #getColorCode
     */
    public void setColorCode(ColorCode colorCode) {
        if (colorCode == ColorCode.EMPTY) {
            this.colorCode = null;
        } else {
            this.colorCode = colorCode;
        }
    }

    /**
     * Returns this project's color code.
     * 
     * @return the color code or null if none has been set
     * @see #setColorCode
     */
    public ColorCode getColorCode() {
        return this.colorCode;
    }

    /**
     * Sets the HTML image URL of the image file that represents this project
     * space's color.
     * 
     * @param colorImgURL
     *            the HTML image URL
     * @see #getColorImgURL
     */
    public void setColorImgURL(String colorImgURL) {
        this.colorImgURL = colorImgURL;
    }

    /**
     * Returns the HTML image URL of the image file that represents this project
     * space's color.
     * 
     * @return the HTML image URL
     * @see #setColorImgURL
     */
    public String getColorImgURL() {
        return this.colorImgURL;
    }

    /**
     * Specifies the improvement code for this ProjectSpace.
     * 
     * @param improvementCode
     *            the improvement code
     * @see #getImprovementCode
     */
    public void setImprovementCode(ImprovementCode improvementCode) {
        if (improvementCode == ImprovementCode.EMPTY) {
            this.improvementCode = null;
        } else {
            this.improvementCode = improvementCode;
        }
    }

    /**
     * Returns the improvement code for this ProjectSpace.
     * 
     * @return the improvement code
     * @see #setImprovementCode
     */
    public ImprovementCode getImprovementCode() {
        return this.improvementCode;
    }

    /**
     * Specifies the color code for the financial status of this project.
     * 
     * @param financialStatusColorCode
     *            the color code
     * @see #getFinancialStatusColorCode
     */
    public void setFinancialStatusColorCode(ColorCode financialStatusColorCode) {
        if (financialStatusColorCode == ColorCode.EMPTY) {
            this.financialStatusColorCode = null;
        } else {
            this.financialStatusColorCode = financialStatusColorCode;
        }
    }

    /**
     * Returns the color code for the financial status of this project.
     * 
     * @return the color code
     * @see #setFinancialStatusColorCode
     */
    public ColorCode getFinancialStatusColorCode() {
        return this.financialStatusColorCode;
    }

    /**
     * Specifies the improvement code for the financial status of this project.
     * 
     * @param financialStatusImprovementCode
     *            the improvement code
     * @see #getFinancialStatusImprovementCode
     */
    public void setFinancialStatusImprovementCode(ImprovementCode financialStatusImprovementCode) {
        if (financialStatusImprovementCode == ImprovementCode.EMPTY) {
            this.financialStatusImprovementCode = null;
        } else {
            this.financialStatusImprovementCode = financialStatusImprovementCode;
        }
    }

    /**
     * Returns the improvement code for the financial status of this project.
     * 
     * @return the improvement code
     * @see #setFinancialStatusImprovementCode
     */
    public ImprovementCode getFinancialStatusImprovementCode() {
        return this.financialStatusImprovementCode;
    }

    /**
     * Specifies the color code for the schedule status.
     * 
     * @param scheduleStatusColorCode
     *            the color code
     * @see #getScheduleStatusColorCode
     */
    public void setScheduleStatusColorCode(ColorCode scheduleStatusColorCode) {
        if (scheduleStatusColorCode == ColorCode.EMPTY) {
            this.scheduleStatusColorCode = null;
        } else {
            this.scheduleStatusColorCode = scheduleStatusColorCode;
        }
    }

    /**
     * Returns the color code for the schedule status.
     * 
     * @return the color code
     * @see #setScheduleStatusColorCode
     */
    public ColorCode getScheduleStatusColorCode() {
        return this.scheduleStatusColorCode;
    }

    /**
     * Specifies the improvement code for the schedule status.
     * 
     * @param scheduleStatusImprovementCode
     *            the improvement code
     * @see #getScheduleStatusImprovementCode
     */
    public void setScheduleStatusImprovementCode(ImprovementCode scheduleStatusImprovementCode) {
        if (scheduleStatusImprovementCode == ImprovementCode.EMPTY) {
            this.scheduleStatusImprovementCode = null;
        } else {
            this.scheduleStatusImprovementCode = scheduleStatusImprovementCode;
        }
    }

    /**
     * Returns the improvement code for the schedule status.
     * 
     * @return the improvement code
     * @see #setScheduleStatusImprovementCode
     */
    public ImprovementCode getScheduleStatusImprovementCode() {
        return this.scheduleStatusImprovementCode;
    }

    /**
     * Specifies the color code for the resource status.
     * 
     * @param resourceStatusColorCode
     *            the color code
     * @see #getResourceStatusColorCode
     */
    public void setResourceStatusColorCode(ColorCode resourceStatusColorCode) {
        if (resourceStatusColorCode == ColorCode.EMPTY) {
            this.resourceStatusColorCode = null;
        } else {
            this.resourceStatusColorCode = resourceStatusColorCode;
        }
    }

    /**
     * Returns the color code for the resource status.
     * 
     * @return the color code
     * @see #setResourceStatusColorCode
     */
    public ColorCode getResourceStatusColorCode() {
        return this.resourceStatusColorCode;
    }

    /**
     * Specifies the improvement code for the resource status.
     * 
     * @param resourceStatusImprovementCode
     *            the improvement code
     * @see #getResourceStatusImprovementCode
     */
    public void setResourceStatusImprovementCode(ImprovementCode resourceStatusImprovementCode) {
        if (resourceStatusImprovementCode == ImprovementCode.EMPTY) {
            this.resourceStatusImprovementCode = null;
        } else {
            this.resourceStatusImprovementCode = resourceStatusImprovementCode;
        }
    }

    /**
     * Returns the improvement code for the resource status.
     * 
     * @return the improvement code
     * @see #setResourceStatusImprovementCode
     */
    public ImprovementCode getResourceStatusImprovementCode() {
        return this.resourceStatusImprovementCode;
    }

    /**
     * Get the Project overall status.
     * 
     * @return an <code>String</code> value that indicates the project overall status.
     * @see #setProjectOverallStatus
     */
    public String getProjectOverallStatus() {
        return projectOverallStatus;
    }

    /**
     * Set the Project overall status.
     * 
     * @param projectOverallStatus the new <code>String</code> value that will represent the
     *            project overall status.
     * @see #getProjectOverallStatus
     */
    public void setProjectOverallStatus(String projectOverallStatus) {
        this.projectOverallStatus = projectOverallStatus;
    }

    /**
     * Get the Project status comments.
     *
     * @return an <code>String</code> value that indicates the project status comments.
     * @see #setProjectStatusComments
     */
    public String getProjectStatusComments() {
        return projectStatusComments;
    }

    /**
     * Set the Project status comments.
     *
     * @param projectStatusComments the new <code>String</code> value that will represent the
     * project status comments.
     * @see #getProjectStatusComments
     */
    public void setProjectStatusComments(String projectStatusComments) {
        this.projectStatusComments = projectStatusComments;
    }
}
