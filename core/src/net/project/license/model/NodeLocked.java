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

import net.project.license.LicenseException;
import net.project.persistence.PersistenceException;

import org.jdom.Element;

/**
 * A NodeLocked model is tied to a specific node id.  It is valid while the
 * system node id is the same as the one with which this model was created.
 * @see net.project.license.system.LicenseProperties#getCurrentNodeID
 */
public class NodeLocked extends LicenseModel {

    /** The node to which this license model is locked. */
    private NodeID nodeID = null;

    /**
     * Creates an empty NodeLocked.
     */
    NodeLocked() {
        super();
    }

    /**
     * Creates a new NodeLocked based on the specified nodeID.
     * @param lockedNodeID the node to which this model is locked
     */
    public NodeLocked(NodeID lockedNodeID) {
        setNodeID(lockedNodeID);
    }

    /**
     * Sets the node id.
     * @param nodeID the node id
     * @see #getNodeID
     */
    private void setNodeID(NodeID nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Returns the nodeID for this node locked model.
     * @return the nodeID
     * @see #setNodeID
     */
    public NodeID getNodeID() {
        return this.nodeID;
    }

    /**
     * Returns the xml Element for this LicenseModel.
     * This is used for persistence the NodeLocked as XML.  It cannot change
     * without changing the {@link #populate} method.  Additionally, changing
     * this may break compatibility with existing XML structures.
     * @return the element
     */
    public Element getXMLElement() {
        String elementName = LicenseModelTypes.getAll().getLicenseModelType(getLicenseModelTypeID()).getXMLElementName();
        
        Element rootElement = new Element(elementName);
        rootElement.addContent(getNodeID().getXMLElement());
        return rootElement;
    }

    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     */
    protected void populate(Element element) {
        
        // Iterate over each child element if this NodeLocked element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("NodeID")) {
                setNodeID(NodeID.create(childElement));
            }
        
        }

    }

    /**
     * Indicates if this license model's constraints have already been met.
     * @return a check status value of true if this NodeLock's nodeID is
     * not equal to the system's current node id; false if nodeID matches
     */
    public net.project.license.CheckStatus checkConstraintMet() 
	throws LicenseException, PersistenceException {
        net.project.license.CheckStatus status = null;

        NodeID currentSystemID = net.project.license.system.LicenseProperties.getInstance().getCurrentNodeID();

        if (currentSystemID != null && currentSystemID.equals(getNodeID())) {
            // Current system node id matches this one; constraint not met
            status = new net.project.license.CheckStatus(false);
        
        } else {
            // System node id differs from this one
            // This is a problem for a node-locked license
            status = new net.project.license.CheckStatus(true, "Node locked node ID differs from system ID");
        }

        return status;
    }

    /**
     * Indicates if this license model's constraints have already been exceeded.
     * @return a check status value of true if this NodeLock's nodeID is
     * not equal to the system's current node id; false if nodeID matches
     * @throws LicenseException
     */
    public net.project.license.CheckStatus checkConstraintExceeded() 
	throws LicenseException, PersistenceException {
        return checkConstraintMet();
    }
    
    /**
     * Returns the license model type id for this license model.
     * @return the license model type id
     */
    public LicenseModelTypeID getLicenseModelTypeID() {
        return LicenseModelTypeID.NODE_LOCKED;
    }    
    
    /**
     * Acquires this node locked model.
     * Succeeds if its constraints have not been met.
     * @throws LicenseModelAcquisitionException if there is a problem acquiring
     * this model
     * @throws LicenseException if there is a problem loading license master properties
     * @see #checkConstraintMet
     
     */
    public void acquisitionEvent() 
	throws LicenseModelAcquisitionException, LicenseException, PersistenceException {
        net.project.license.CheckStatus status = checkConstraintMet();
        
        // If the constraints have been met, then model cannot be acquired
        if (status.booleanValue()) {
            String message = (status.hasMessage() ? status.getMessage() : "Node Locked model constraints met");
            throw new LicenseModelAcquisitionException(message);
        }

    }

    /**
     * Reqlinquishes this node locked model.
     * Always succeeds
     */
    public void relinquishEvent() throws LicenseModelRelinquishException {
        // Do nothing
        // No action prohibits the relinquish of this model
    }

    /**
     * Provides an XML structure of this NodeLocked.
     * This structure may be used for presentation purposes.
     */
    public net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement(LicenseModelTypes.getAll().getLicenseModelType(getLicenseModelTypeID()).getXMLElementName());
            doc.addElement(getNodeID().getXMLDocument());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

}
