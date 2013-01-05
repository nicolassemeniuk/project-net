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
package net.project.document;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import net.project.security.SecurityProvider;
import net.project.xml.XMLFormatter;

/**
 * DocumentManagerBean adds additional view-oriented capabilities to
 * a DocumentManager.
 */
public class DocumentManagerBean extends DocumentManager implements Serializable {

    /** The XMLFormatter used for transforming xml */
    private XMLFormatter xml;

    /**
     * Creates a new, empty DocumentManagerBean.
     */
    public DocumentManagerBean () {
        xml = new XMLFormatter();
    }

    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet (String styleSheetFileName) {
        xml.setStylesheet(styleSheetFileName);
    }

    /**
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getDocsCheckedOutByUserPresentation () {

        // return the XML representation of the container's contents
        return( xml.getPresentation( getXMLDocsCheckedOutByUser()) );

    } // end getPresentation


    /**
     * Indicates whether the specified security action is allowed by the
     * current object's ancestor containers.<br>
     * For example, a document may only be viewed by a user if the user
     * has view permission for the folder that the document is in, and 
     * has view permission that folder's parent folder etc...
     */
    public boolean isActionAllowedByAncestorContainers(SecurityProvider securityProvider, int module, int action) {
        boolean isActionAllowed = true;
        ContainerEntry containerEntry = null;

        // Get all ancestor containers for the container that the current object
        // is in.  This includes the immediate parent container and all its ancestors
        List containers = getDocumentSpace().getAncestorContainers(getContainerID(getCurrentObject().getID()));

        // For each container, check security
        Iterator it = containers.iterator();
        while (it.hasNext()) {
            containerEntry = (ContainerEntry) it.next();
            System.out.println("DEBUG [DocumentManager.isActionAllowedByAncestorContainers] Container: " + containerEntry.getID());

            // Action is allowed only if ALL containers allow access
            isActionAllowed &= securityProvider.isActionAllowed(containerEntry.getID(), module, action);

            // If Action is no longer allowed, stop processing containers
            if (!isActionAllowed) {
                break;
            }
        }
        
        return isActionAllowed;
    }

} // end class DocumentManagerBean
