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
package net.project.notification;

import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**
 * Provides presentation facilities for the SubscriptionManager.
 */
public class SubscriptionManagerBean extends SubscriptionManager {

    /**
     * the object used to format the XML prior to presentations
     */
    private XMLFormatter xmlFormatter = new XMLFormatter();


    /**
     * Creates an empty SubscriptionManagerBean.
     */
    public SubscriptionManagerBean() {
        // Do nothing
    }

    /**
     * Gets the presentation of the all the current subscriptions for the user.
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting text
     * @return presetation of  all the current subscriptions for the user.
     */
    public String getSubscriptionsPresentation(String spaceID) {
        String presentation = null;

        try {
            if (spaceID != null && !spaceID.trim().equals("")) {
                presentation = xmlFormatter.getPresentation(getSubscriptionsForSpace(spaceID).getXML());
            } else {
                presentation = xmlFormatter.getPresentation(getSubscriptions().getXML());
            }
        } catch (net.project.persistence.PersistenceException pe) {
        	Logger.getLogger(SubscriptionManagerBean.class).error("SubscriptionManager.getSubscriptions(): threw an persistence exception: " + pe);

        }

        return presentation;
    }

    /**
     * Sets the stylesheet file name used to render this component.
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        xmlFormatter.setStylesheet(styleSheetFileName);
    }

}
