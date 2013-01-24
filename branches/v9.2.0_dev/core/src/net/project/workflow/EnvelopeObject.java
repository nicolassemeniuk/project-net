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
package net.project.workflow;

import java.io.Serializable;

import net.project.base.ObjectFactory;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

/**
  * An envelope object represents an object belonging to, or to be added to
  * an envelope.
  */
class EnvelopeObject implements Serializable, IXMLPersistence, ErrorCodes {

    /** object id */
    private String objectID = null;
    /** envelope to which object belongs */
    private String envelopeID = null;
    /** real object for this id */
    private IWorkflowable realObject = null;
    
    String getEnvelopeID() {
        return this.envelopeID;
    }
    void setEnvelopeID(String envelopeID) {
        this.envelopeID = envelopeID;
    }
    String getID() {
        return objectID;
    }
    
    void setID(String id) {
        objectID = id;
    }
    
    /**
      * Return the real object for this envelope object.
      * The real object is instantiated if necessary.
      * @throws NotWorkflowableException if the object does not implement the
      * IWorkflowable interface
      * @see net.project.workflow.IWorkflowable
      */
    IWorkflowable getRealObject() throws NotWorkflowableException {
        if (realObject == null) {
            Object obj = null;
            ObjectFactory objectFactory = new ObjectFactory();

            /* Get object for objectID */
            obj = objectFactory.make(objectID);
            if (!(obj instanceof IWorkflowable)) {
                NotWorkflowableException wfe = 
                    new NotWorkflowableException("Object for id " + objectID + " cannot be placed in a workflow.");
                wfe.setErrorCode(ErrorCodes.OBJECT_NOT_WORKFLOWABLE);
                throw wfe;
            } else {
                realObject = (IWorkflowable) obj;
                realObject.setID(objectID);
                try {
                    realObject.load();
                } catch (PersistenceException pe) {
                    NotWorkflowableException wfe = 
                        new NotWorkflowableException("Error placing object for id " + objectID + " in envelope: " + pe);
                    wfe.setErrorCode(ErrorCodes.OBJECT_NOT_WORKFLOWABLE);
                    throw wfe;
                }
            }
        }
        return realObject;
    }

    /**
      * Return the EnvelopeObject XML including the XML version tag
      * @return the XML string
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }    
    
    /**
      * Return the EnvelopeObject XML without the XML version tag
      * @return XML string
      */
    public java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<envelope_object>\n");
        xml.append("<object_id>" + XMLUtils.escape(getID()) + "</object_id>");
        xml.append("</envelope_object>\n");
        return xml.toString();
    }

}
