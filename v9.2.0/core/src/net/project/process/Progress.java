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

import java.util.Date;

/**
 * Represents the start date, end date and percent complete of a process element (phase)
 */
public class Progress {

    /** The start date of the process item */
    private Date startDate;

    /** The finish date of the item */
    private Date finishDate;

    /** The percent complete of the item */
    private String percentComplete;

    public Progress (Date start, Date finish, String percentComplete) {
        setStartDate(start);
        setFinishDate(finish);
        setPercentComplete(percentComplete);
    }

    public Progress() {

    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public String getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(String percentComplete) {
        this.percentComplete = percentComplete;
    }

    public void clear() {
        setStartDate(null);
        setFinishDate(null);
        setPercentComplete(null);
    }

}
