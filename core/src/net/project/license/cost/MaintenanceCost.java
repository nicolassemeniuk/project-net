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
package net.project.license.cost;

import java.util.Iterator;

import net.project.base.quantity.Percentage;
import net.project.base.quantity.Quantity;
import net.project.base.quantity.UnitOfMeasure;

import org.jdom.Element;

/**
 * The Maintenance Cost associated with the license.
 * This may used when generating a maintenance bill.
 * It has a percentage that is generally the percentage of the total base
 * cost of the license
 */
public class MaintenanceCost extends LicenseCost implements IPercentageCost {

    private Percentage percentage = null;

    /**
     * Creates an empty MaintenanceCost.
     */
    MaintenanceCost() {
        super();
    }

    /**
     * Creates a new MaintenanceCost with the specified percentage value.
     * @param percentage the percentage value of this maintenance cost
     */
    public MaintenanceCost(Percentage percentage) {
        setPercentage(percentage);
    }

    protected void setPercentage(Percentage percentage) {
        this.percentage = percentage;
    }

    public Percentage getPercentage() {
        return this.percentage;
    }

    /**
     * Returns the XML format of this license model suitable for storage.
     * @return the storage XML or empty string if there was a problem
     * constructing the XML
     */
    public Element getXMLElement() {
        String elementName = LicenseCostTypes.getAll().getLicenseCostType(LicenseCostTypeID.MAINTENANCE).getXMLElementName();

        Element rootElement = new Element(elementName);
        rootElement.addContent(new Element("Percentage").addContent(getPercentage().getXMLElement()));
        return rootElement;
    }

    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     */
    public void populate(Element element) {
        
        // Iterate over each child element of this BaseCost element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Percentage")) {
                setPercentage(Percentage.create((Element) childElement.getChildren().get(0)));
            
            }
        }

    }

    /**
     * Returns the license cost type id for this license cost.
     * @return {@link LicenseCostTypeID#MAINTENANCE}
     */
    public LicenseCostTypeID getLicenseCostTypeID() {
        return LicenseCostTypeID.MAINTENANCE;
    }
    
    /**
     * Provides an XML structure of this MaintenanceCost.
     * This structure may be used for presentation purposes.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement(LicenseCostTypes.getAll().getLicenseCostType(LicenseCostTypeID.MAINTENANCE).getXMLElementName());
            doc.startElement("Percentage");
            if (getPercentage() != null) {
                doc.addElement(getPercentage().getXMLDocument());
            }
            doc.endElement();
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }


    /**
     * Calculates the total charge of this license cost for the specified license.
     * For a <code>MaintenanceCost</code> the charge is the license's maximum
     * usage cost and the base cost unit price multiplied by the maintenance percentage.
     * @param license the license for whom this license cost total cost is
     * being calculated
     * @return the license charge for this license cost
     */
    public LicenseCharge calculateLicenseCharge(net.project.license.License license) {
        LicenseCharge charge = new LicenseCharge();
        charge.setQuantity(new Quantity(new Integer(license.getMaximumUsageCount()), UnitOfMeasure.EA));
        // This charge's unit price based on the base cost's unit price
        // multiplied by this maintenance cost's percentage
        charge.setUnitPrice(findBaseCost(license).getUnitPrice().multiply(getPercentage()));
        return charge;
    }


    /**
     * Locates the <code>BaseCost</code> license cost.
     */
    private BaseCost findBaseCost(net.project.license.License license) {
        
        BaseCost baseCost = null;

        for (Iterator it = license.getCostCollection().iterator(); it.hasNext(); ) {
            ILicenseCost nextCost = (ILicenseCost) it.next();

            if (nextCost.getLicenseCostTypeID().equals(LicenseCostTypeID.BASE)) {
                baseCost = (BaseCost) nextCost;
            }

        }
    
        return baseCost;
    }


}
