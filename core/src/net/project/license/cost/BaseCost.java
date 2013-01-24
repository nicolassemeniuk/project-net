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

import net.project.base.money.Money;
import net.project.base.quantity.Quantity;
import net.project.base.quantity.UnitOfMeasure;
import net.project.xml.document.XMLDocument;

import org.jdom.Element;

/**
 * The Base Cost associated with the license.  This is typically based on the
 * license usage limit.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public class BaseCost extends LicenseCost implements IFixedPriceCost {

    /** This LicenseCost's unit price. */
    private Money unitPrice = null;

    /**
     * Creates an empty BaseCost.
     */
    BaseCost() {
        super();
    }

    /**
     * Creates a new BaseCost with the specified unit price.
     * @param unitPrice the unitPrice for the base cost of a license
     */
    public BaseCost(Money unitPrice) {
        setUnitPrice(unitPrice);
    }

    /**
     * Sets the unit price.
     * @param unitPrice the unit price of this license cost
     */
    protected void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Returns the unit price.
     * @return the unit price
     */
    public Money getUnitPrice() {
        return this.unitPrice;
    }

    /**
     * Returns the license cost type id for this license cost.
     * @return {@link LicenseCostTypeID#BASE} always.
     */
    public LicenseCostTypeID getLicenseCostTypeID() {
        return LicenseCostTypeID.BASE;
    }
    
    
    /**
     * Returns the XML format of this license model suitable for storage.
     * @return the storage XML or empty string if there was a problem
     * constructing the XML
     */
    public Element getXMLElement() {
        String elementName = LicenseCostTypes.getAll().getLicenseCostType(LicenseCostTypeID.BASE).getXMLElementName();

        Element rootElement = new Element(elementName);
        rootElement.addContent(new Element("UnitPrice").addContent(getUnitPrice().getXMLElement()));
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

            if (childElement.getName().equals("UnitPrice")) {
                // UnitPrice contains money
                setUnitPrice(Money.create((Element) childElement.getChildren().get(0)));
            
            }
        }

    }
    
    /**
     * Provides an XML structure of this BaseCost.  This structure may be used
     * for presentation purposes.
     *
     * @return an XML structure for this <code>BaseCost</code>.
     */
    public XMLDocument getXMLDocument() {
        XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement(LicenseCostTypes.getAll().getLicenseCostType(LicenseCostTypeID.BASE).getXMLElementName());
            doc.startElement("UnitPrice");
            doc.addElement(getUnitPrice().getXMLDocument());
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
     * For a <code>BaseCost</code> the charge is the license's max usage count
     * and the unit price.
     * @param license the license for whom this license cost total cost is
     * being calculated
     * @return the license charge for this license cost
     */
    public LicenseCharge calculateLicenseCharge(net.project.license.License license) {
        // Base Cost charge is simply the license's max usage count and the
        // base cost unit price
        LicenseCharge charge = new LicenseCharge();
        charge.setQuantity(new Quantity(new Integer(license.getMaximumUsageCount()), UnitOfMeasure.EA));
        charge.setUnitPrice(getUnitPrice());
        return charge;
    }

}
