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
package net.project.billing.invoice;

import net.project.license.system.MasterProperties;
import net.project.license.system.PropertyName;
import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;

/**
 * Provides facilities for invoice creation notifications.
 */
public class InvoiceNotification extends ImmediateNotification {

    public static final String INVOICE_NOTIFICATION_STYLE_SHEET = "/admin/invoice/xsl/invoice-notification.xsl";

    /**
     * Creates a new LicenseNotification.
     */
    public InvoiceNotification() {
        super();
    }

    private String getInvoiceEmail() throws NotificationException {
        try {
            return MasterProperties.getInstance().get(PropertyName.LICENSE_BILLING_INVOICE_EMAIL).getValue();
        } catch (net.project.persistence.PersistenceException pe) {
            throw new NotificationException("InvoiceNotification.java : Could not get the InvoiceEmail Address from master properties." + pe.getMessage());
        } catch (net.project.license.system.MasterPropertiesNotFoundException mpfe) {
            throw new NotificationException("InvoiceNotification.java : Could not get the InvoiceEmail Address from master properties." + mpfe.getMessage());
        } catch (net.project.license.LicenseException le) {
            throw new NotificationException("InvoiceNotification.java : Could not get the InvoiceEmail Address from master properties." + le.getMessage());
        }
    }


    /**
     * Notifies Project.net of a invoice creation.
     * @param invoice the invoice for which this notification is being sent.
     */
    public void notifyProjectNet(Invoice invoice)
        throws NotificationException {

        setDeliveryTypeID(ImmediateNotification.EMAIL_DELIVERABLE);
        setXSLStylesheetPath(INVOICE_NOTIFICATION_STYLE_SHEET);
        setDeliveryAddress(this.getInvoiceEmail());
        setCustomizationUserID(SessionManager.getUser().getID());

        try {
            this.attach(invoice.getEmailAttachment());

            StringBuffer xml = new StringBuffer();
            xml.append(IXMLPersistence.XML_VERSION);
            xml.append("<InvoiceNotification>");
            xml.append(invoice.getXMLBody());
            xml.append("</InvoiceNotification>");
            setNotificationXML(xml.toString());

            this.post();
        } catch (java.io.IOException ioe) {
            throw new NotificationException("InvoiceNotification.java :	Could not retrieve the attachment:IOException thrown." + ioe.getMessage());
        } catch (NotificationException ne) {
            // We don't want to fail the assigning of reponsibility just for notification failure.
            throw new NotificationException("InvoiceNotification.java : notifyProjectNet operation failed : Notification exception thrown " + ne.getMessage());
        }

    }


}
