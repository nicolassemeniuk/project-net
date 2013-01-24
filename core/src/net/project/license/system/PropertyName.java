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
package net.project.license.system;

import java.util.Iterator;

import org.jdom.Element;

/**
 * Provides an emumeration of available property names.
 * The property names are simply constant values that identify a property.
 * Also provides mechanisms to convert to and from an XML format.
 */
public class PropertyName {
    //
    // Static members
    //

    /**
     * Creates a PropertyName object from the specified XML Element.
     * @param element the element that represents the property name
     * @return the PropertyName object, or null if the element is not a PropertyName element
     */
    public static PropertyName create(Element element) {
        PropertyName propertyName = null;

        if (element.getName().equals("PropertyName")) {
            propertyName = new PropertyName();
            propertyName.populate(element);
        }

        return propertyName;
    }


    //
    // Instance members
    //

    private String name = null;

    /**
     * Creates an empty property name.
     */
    private PropertyName() {
        // Do nothing
    }

    private PropertyName(String name) {
        setName(name);
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return getName();
    }

    /**
     * PropertyNames are equal if their names by {@link #getName} are equal.
     */
    public boolean equals(Object obj) {

        if ((this == obj) ||
                (obj instanceof PropertyName &&
                 obj != null &&
                 ((PropertyName) obj).getName().equals(getName()))
            ) {

            return true;
        }

        return false;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Populates this propertyName object from the specified element.
     * @param element the propertyName element
     */
    private void populate(Element element) {
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Name")) {
                setName(childElement.getTextTrim());
            }
        }

    }


    /**
     * Returns this PropertyName as an XML Element.
     * @return the element for this property name
     */
    public Element getXMLElement() {
        Element rootElement = new Element("PropertyName");
        rootElement.addContent(new Element("Name").addContent(getName()));
        return rootElement;

    }

    /**
     * Provides an XML structure of this PropertyName.
     * This structure may be used for presentation purposes.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("PropertyName");
            doc.addElement("Name", getName());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    //
    // Enumerated values
    //

    public static final PropertyName LICENSE_IS_ENABLED = new PropertyName("prm.global.license.isenabled");
    public static final PropertyName LICENSE_COST_BASE = new PropertyName("prm.global.license.cost.base");
    public static final PropertyName LICENSE_COST_MAINTENANCE = new PropertyName("prm.global.license.cost.maintenance");
    public static final PropertyName LICENSE_COST_TRIAL = new PropertyName("prm.global.license.cost.trial");
    public static final PropertyName LICENSE_MODEL_TRIAL_PERIOD = new PropertyName("prm.global.license.model.trial.period");
    public static final PropertyName LICENSE_PRODUCT_INSTALLATION_ID = new PropertyName("prm.global.license.productinstallationid");
    
    //public static final PropertyName LICENSE_MAINTENANCE_BILLING_ID = new PropertyName("prm.global.license.maintenancebillingid");
    //public static final PropertyName LICENSE_USAGE_BILLING_ID = new PropertyName("prm.global.license.usagebillingid");

    public static final PropertyName LICENSE_USAGE_BILLING_PERIOD_TYPEA = new PropertyName("prm.global.license.usage.billingperiod.typea");
    public static final PropertyName LICENSE_MAINTENANCE_BILLING_PERIOD_TYPEA = new PropertyName("prm.global.license.maintenance.billingperiod.typea");
    public static final PropertyName LICENSE_MAINTENANCE_BILLING_PERIOD_TYPEB = new PropertyName("prm.global.license.maintenance.billingperiod.typeb");
    public static final PropertyName LICENSE_MAINTENANCE_BILLING_PERIOD_TYPEC = new PropertyName("prm.global.license.maintenance.billingperiod.typec");

    public static final PropertyName LICENSE_BILLING_INVOICE_TERM = new PropertyName("prm.global.billing.invoice.term");
    public static final PropertyName LICENSE_BILLING_INVOICE_EMAIL = new PropertyName("prm.global.billing.invoice.email");
    public static final PropertyName LICENSE_BILLING_INVOICE_CUSTOMERINFO = new PropertyName("prm.global.billing.invoice.customerinfo");

    public static final PropertyName CREDIT_CARD_SERVICE_CHARGE = new PropertyName("prm.global.license.creditcardservicecharge.property.name");
    public static final PropertyName CREDIT_CARD_SERVICE_CHARGE_TYPE = new PropertyName("prm.global.license.creditcardservicechargetype.property.name");
    public static final PropertyName CREDIT_CARD_SERVICE_CHARGE_CURRENCY = new PropertyName("prm.global.license.creditcardservicechargecurrency.property.name");

    public static final PropertyName LICENSE_KEY_ENABLED = new PropertyName("prm.global.license.licensetype.licensekey.enabled");
    public static final PropertyName CREDIT_CARD_ENABLED = new PropertyName("prm.global.license.licensetype.creditcard.enabled");
    public static final PropertyName TRIAL_LICENSE_ENABLED = new PropertyName("prm.global.license.licensetype.trial.enabled");
    public static final PropertyName COST_CENTER_ENABLED = new PropertyName("prm.global.license.licensetype.costcenter.enabled");
    public static final PropertyName SYSTEM_DEFAULT_COST_CENTER_ENABLED = new PropertyName("prm.global.license.licensetype.systemdefaultcostcenter.enabled");
    public static final PropertyName DEFAULT_LICENSE_KEY_ENABLED = new PropertyName("prm.global.license.licensetype.defaultlicensekey.enabled");
}
