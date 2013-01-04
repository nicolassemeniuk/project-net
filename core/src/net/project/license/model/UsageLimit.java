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

import java.util.Iterator;

import net.project.base.property.PropertyProvider;

import org.jdom.Element;

/**
 * A UsageLimit is valid while a license's usage has not exceeded the built-in
 * count.
 */
public class UsageLimit extends LicenseModel implements java.io.Serializable {

    /** The maximum count of usage. */
    private int maxUsageCount = 0;

    /** The current usage count. */
    private int currentUsageCount = 0;

    /**
     * Creates an empty UsageLimit.
     */
    UsageLimit() {
        super();
    }

    /**
     * Creates a new UsageLimit with the specified maximum count of usages.
     * @param maxUsageCount the maximum usage for this usage limit
     */
    public UsageLimit(int maxUsageCount) {
        setMaxUsageCount(maxUsageCount);
    }

    /**
     * Increments the current usage count by one.
     */
    private void incrementCurrentUsageCount() {
        incrementCurrentUsageCount(1);
    }

    /**
     * Increments the current usage count by the specified amount.
     * @param amount the amount to increment the count by
     */
    private void incrementCurrentUsageCount(int amount) {
        this.currentUsageCount += amount;
    }

    /**
     * Decrements the current usage count by one.
     */
    private void decrementCurrentUsageCount() {
        decrementCurrentUsageCount(1);
    }

    /**
     * Decrements the current usage count by the specified amount.
     * Note that if the amount exceeds the current usage count, the usage
     * count becomes zero.
     * @param amount the amount to decrement the count by
     */
    private void decrementCurrentUsageCount(int amount) {
        if (amount > this.currentUsageCount) {
            this.currentUsageCount = 0;
        } else {
            this.currentUsageCount -= amount;
        }
    }

    /**
     * Sets the max usage count.
     * @param count the max usage count for this usage limit
     */
    private void setMaxUsageCount(int count) {
        this.maxUsageCount = count;
    }

    /**
     * Returns the max usage count.
     * @return the max usage count for this usage limit
     */
    public int getMaxUsageCount() {
        return this.maxUsageCount;
    }

    /**
     * Sets the current usage count.
     * @param count the current usage count
     */
    private void setCurrentUsageCount(int count) {
        this.currentUsageCount = count;
    }

    /**
     * Returns the current usage count.
     * @return the current usage count for this usage limit
     */
    public int getCurrentUsageCount() {
        return this.currentUsageCount;
    }

    /**
     * Returns the xml Element for this LicenseModel.
     * This is used for persistence the UsageLimit as XML.  It cannot change
     * without changing the {@link #populate} method.  Additionally, changing
     * this may break compatibility with existing XML structures.
     * @return the element
     */
    public Element getXMLElement() {
        String elementName = LicenseModelTypes.getAll().getLicenseModelType(getLicenseModelTypeID()).getXMLElementName();
        
        Element rootElement = new Element(elementName);
        rootElement.addContent(new Element("MaxCount").addContent(String.valueOf(getMaxUsageCount())));
        rootElement.addContent(new Element("CurrentCount").addContent(String.valueOf(getCurrentUsageCount())));
        return rootElement;
    }

    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     */
    protected void populate(Element element) {
        
        // Iterate over each child element if this UsageLimit element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("MaxCount")) {
                // MaxCount contains a number
                setMaxUsageCount(new Integer(childElement.getTextTrim()).intValue());
            
            } else if (childElement.getName().equals("CurrentCount")) {
                // CurrentCount contains a number
                setCurrentUsageCount(new Integer(childElement.getTextTrim()).intValue());

            }
        }

    }

    /**
     * Indicates if this license model's constraints have already been met.
     * @return a check status value of true if this UsageLimit's current
     * usage count is equal to (or greater) than its max usage count; a
     * check status value of false if the current usage count if less than
     * the max usage count
     */
    public net.project.license.CheckStatus checkConstraintMet() {
        net.project.license.CheckStatus status = null;

        if (getCurrentUsageCount() >= getMaxUsageCount()) {
            status = new net.project.license.CheckStatus(true, PropertyProvider.get("prm.license.model.usagelimit.limitreached.message"));
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
        return new net.project.license.CheckStatus(false);
    }

    /**
     * Returns the license model type id for this license model.
     * @return the license model type id
     */
    public LicenseModelTypeID getLicenseModelTypeID() {
        return LicenseModelTypeID.USAGE_LIMIT;
    }    
    
    /**
     * Updates this usage count by one unit.
     * @throws LicenseModelAcquisitionException if the max usage limit has
     * already been met.
     */
    public void acquisitionEvent() throws LicenseModelAcquisitionException {
        net.project.license.CheckStatus status = checkConstraintMet();
        if (status.booleanValue()) {
            throw new LicenseModelAcquisitionException(status.getMessage());
        
        } else {
            incrementCurrentUsageCount();
        }
    
    }

    /**
     * Decrements this usage count by one unit.
     * @throws LicenseModelRelinquishException never throws this
     */
    public void relinquishEvent() throws LicenseModelRelinquishException {
        decrementCurrentUsageCount();
    }

    /**
     * Provides an XML structure of this UsageLimit.
     * This structure may be used for presentation purposes.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
           
            doc.startElement(LicenseModelTypes.getAll().getLicenseModelType(getLicenseModelTypeID()).getXMLElementName());
        	doc.addElement("MaxCount", new Integer(getMaxUsageCount()));
        	doc.addElement("CurrentCount", new Integer(getCurrentUsageCount()));
    	    doc.endElement();
            	
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }
}
