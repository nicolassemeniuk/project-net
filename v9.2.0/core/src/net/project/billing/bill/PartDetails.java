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
package net.project.billing.bill;

/**
 *
 * @author tim
 */
public class PartDetails {

    /** The part number of this PartDetails. */
    private String partNumber = null;
    
    /** The description of this PartDetaisl. */
    private String partDescription = null;
    
    /**
     * Creates a new empty PartDetails.
     */
    public PartDetails() {
        // Do Nothing
    }
    
    /**
     * Creates a new PartDetails.
     * @param partNumber the part number
     * @param partDescription the part's description
     */
    public PartDetails(String partNumber, String partDescription) {
        this.setPartNumber(partNumber);
	this.setPartDescription(partDescription);
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    
    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }
    
    public String getPartNumber() {
        return this.partNumber;
    }
    
    public String getPartDescription() {
        return this.partDescription;
    }

    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("PartDetails");

            doc.addElement("PartNumber", this.getPartNumber());
            doc.addElement("PartDescription", this.getPartDescription());
            
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    /**
     * Populates this part details from the xml element.
     * @param element the xml element from which to populate this part details
     */
    protected void populate(org.jdom.Element element) {
        
        // Iterate over each child element of this PartDetails element
        // and handle each one
        org.jdom.Element childElement = null;
        for (java.util.Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (org.jdom.Element) it.next();

            if (childElement.getName().equals("PartNumber")) {
                
                this.setPartNumber(childElement.getTextTrim());
            
            } else if (childElement.getName().equals("PartDescription")) {
                
                this.setPartDescription(childElement.getTextTrim());
            
            } 
	}

    }

    // Static Members

    /**
     * Creates a part details from the specified part details element.
     * @param responsiblePartyElement the xml element from which to create the
     * part details
     * @return the part details
     */
    public static PartDetails create(org.jdom.Element partDetailsElement) {
        PartDetails partDetails = new PartDetails();
        partDetails.populate(partDetailsElement);
        return partDetails;
    }

}
