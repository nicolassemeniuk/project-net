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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.license.model;

import java.util.Date;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.xml.XMLUtils;

import org.jdom.Element;

/**
 * The TimeLimit license model provides an end date prior to which a license is
 * valid.  After the end date, the license it is not valid.
 */
public class TimeLimit extends LicenseModel implements java.io.Serializable {

    /** The time limit start date. */
    private Date startDate = null;

    /** The time limit end date. */
    private Date endDate = null;

    /**
     * Creates an empty time limit.
     */
    TimeLimit() {
        super();
    }

    /**
     * Creates a TimeLimit with the specified endDate.
     * @param endDate the date after which a license should expire
     */
    public TimeLimit(Date endDate) {
        setStartDate(new Date());
        setEndDate(endDate);
    }


    /**
     * Creates a TimeLimit with an end date calculated by adding 
     * <code>dayDuration</code> to the current date.
     * @param dayDuration the duration in days
     */
    public TimeLimit(int dayDuration) {
        setStartDate(new Date());
        setEndDate(addDays(getStartDate(), dayDuration));    
    }


    /**
     * Sets this time limit's start date.
     * @param startDate the start date
     * @see #getStartDate
     */
    private void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns this time limit's start date.
     * @return the start date
     * @see #setStartDate
     */
    private Date getStartDate() {
        return this.startDate;
    }

    /**
     * Sets this time limit's end date.
     * @param endDate the ending date
     * @see #getEndDate
     */
    private void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns the end date of this time limit.
     * @return the end date
     * @see #setEndDate
     */
    private Date getEndDate() {
        return this.endDate;
    }

    /**
     * Returns this time limit's duration in days.
     * @return the duration in days
     */
    private int getDayDuration() {
        final long dayMillis = (1000 * 60 * 60 * 24);
        return (int) ((getEndDate().getTime() - getStartDate().getTime()) / dayMillis);
    }

    /**
     * Returns this time limit's duration in days from today.
     * @return the duration in days
     */
    private int getDaysRemaining() {
        final long dayMillis = (1000 * 60 * 60 * 24);
        return (int) ((getEndDate().getTime() - System.currentTimeMillis()) / dayMillis);
    }

    /**
     * Adds the specified number of days to the specified date.
     * @param startDate the date to which to add the days
     * @param dayDuration the number of days to add
     * @return the resulting date
     */
    private Date addDays(Date startDate, int dayDuration) {
        final long dayMillis = (1000 * 60 * 60 * 24);
        return new Date(startDate.getTime() + (dayMillis * dayDuration));
    }

    
    /**
     * Returns the XML format of this license model suitable for storage.
     * @return the storage XML or empty string if there was a problem
     * constructing the XML
     */
    public Element getXMLElement() {
        String elementName = LicenseModelTypes.getAll().getLicenseModelType(getLicenseModelTypeID()).getXMLElementName();
        
        Element rootElement = new Element(elementName);
        rootElement.addContent(new Element("StartDate").addContent(XMLUtils.formatISODateTime(getStartDate())));
        rootElement.addContent(new Element("EndDate").addContent(XMLUtils.formatISODateTime(getEndDate())));
        rootElement.addContent(new Element("DayDuration").addContent(String.valueOf(getDayDuration())));
        return rootElement;
    }

    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     */
    protected void populate(Element element) {
        
        // Iterate over each child element if this TimeLimit element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("StartDate")) {
                // Start Date contains a date
                setStartDate(XMLUtils.parseDateFromXML(childElement.getTextTrim()));
            
            } else if (childElement.getName().equals("EndDate")) {
                // End Date contains a date
                setEndDate(XMLUtils.parseDateFromXML(childElement.getTextTrim()));

            } else if (childElement.getName().equals("DayDuration")) {
                // calculated automatically, no need to read

            }
        }

    }

    /**
     * Indicates if this license model's constraints have already been met.
     * @return a check status value of true if the current date is before
     * the start date or after the end date
     */
    public net.project.license.CheckStatus checkConstraintMet() {
        net.project.license.CheckStatus status = null;
        Date currentDate = new Date();

        if (currentDate.getTime() < getStartDate().getTime() || currentDate.getTime() > getEndDate().getTime()) {
            status = new net.project.license.CheckStatus(true, PropertyProvider.get("prm.license.model.timelimit.outsidedaterange.message"));
        } else {
            status = new net.project.license.CheckStatus(false);
        }

        return status;
    }

    /**
     * Indicates if this license model's constraints have already been exceeded.
     * @return a check status value of false always; exceeded constraints
     * won't prohibit a license from being used (all though we do prevent this
     * scenario through {@link #checkConstraintMet})
     */
    public net.project.license.CheckStatus checkConstraintExceeded() {
        net.project.license.CheckStatus status = null;
        Date currentDate = new Date();

        if (currentDate.getTime() > getEndDate().getTime()) {
            status = new net.project.license.CheckStatus(true, PropertyProvider.get("prm.license.model.timelimit.expired.message"));
        } else {
            status = new net.project.license.CheckStatus(false);
        }

        return status;
    }

    /**
     * Returns the license model type id for this license model.
     * @return the license model type id
     */
    public LicenseModelTypeID getLicenseModelTypeID() {
        return LicenseModelTypeID.TIME_LIMIT;
    }
    
    /**
     * Checks that this time limit has not been met
     * @throws LicenseModelAcquisitionException the time limit has been met
     */
    public void acquisitionEvent() throws LicenseModelAcquisitionException {
        net.project.license.CheckStatus status = checkConstraintMet();
        if (status.booleanValue()) {
            throw new LicenseModelAcquisitionException(status.getMessage());
        }
    }

    /**
     * Performs no action.
     * @throws LicenseModelRelinquishException never
     */
    public void relinquishEvent() throws LicenseModelRelinquishException {
        // Do nothing
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement(LicenseModelTypes.getAll().getLicenseModelType(getLicenseModelTypeID()).getXMLElementName());
            doc.addElement("StartDate", getStartDate());
            doc.addElement("EndDate", getEndDate());
            doc.addElement("DayDuration", new Integer(getDayDuration()));
	    doc.addElement("DaysRemaining", new Integer(getDaysRemaining()));
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

}
