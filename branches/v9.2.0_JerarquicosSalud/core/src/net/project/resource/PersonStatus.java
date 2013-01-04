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

 package net.project.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

/**
 * A class which represents the types of Person Status
 */
public class PersonStatus implements IXMLPersistence, Serializable {

    private static ArrayList personStatusList = new ArrayList();

    /**
     * Active status
     */
    public static final PersonStatus ACTIVE = new PersonStatus("Active","@prm.resource.person.status.active.name");
    public static final PersonStatus UNREGISTERED = new PersonStatus("Unregistered","@prm.resource.person.status.unregistered.name");
    public static final PersonStatus UNCONFIRMED = new PersonStatus("Unconfirmed","@prm.resource.person.status.unconfirmed.name");
    public static final PersonStatus DISABLED = new PersonStatus("Disabled","@prm.resource.person.status.disabled.name");
    public static final PersonStatus DELETED = new PersonStatus("Deleted","@prm.resource.person.status.deleted.name");
    
    private String id = null;
    private String nameToken = null;

    public PersonStatus(String id , String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        this.personStatusList.add(this);
    }

    /**
     * Get a list of all Person Statuses.
     *
     * @return a <code>List</code> value containing all of the possible Person Status
     * objects in the system.
     */
    public static List getPersonStatusList() {
        return personStatusList;
    }

    /**
     * Get the PersonStatus that corresponds to a given ID string.
     *
     * @param id a <code>String</code> value that represents a PersonStatus.
     * @return a <code>PersonStatus</code> object which corresponds to the
     * id parameter passed to this method.
     */
    public static PersonStatus getStatusForID(String id) {
        PersonStatus personStatusToReturn = null;
        Iterator it = personStatusList.iterator();
        
        while (it.hasNext()) {
            PersonStatus personStatus = (PersonStatus)it.next();
            if (personStatus.getID().equals(id)) {
                personStatusToReturn = personStatus;
            }
        }
        
        return personStatusToReturn;
    }
    /**
     * Get the human-readable name of this PersonStatus type.  This is based
     * on the token that was passed to the constructor of this object.
     *
     * @return a human-readable string identifying this person status.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get the token which is used to load the name of this  PersonStatus type.
     *
     * @return a <code>String</code> value used to load the human-readable name
     * of this  PersonStatus type.
     * @see #getName
     */
    public String getNameToken() {
        return nameToken;
    }

    /**
     * Get the unique identifier used to identify this PersonStatus type.
     *
     * @return a <code>String</code> value which uniquely identifies this PersonStatus
     * type.
     */
    public String getID() {
        return id;
    }

     /**
     * Return a string identifying this Person Status Type.
     *
     * @return a <code>String</code> value identifying this Person Status type.
     */
    public String toString() {
        return getName() + "(" + getID() + ")";
    }

    /**
     * Determine if a given object is equal to the current Person Status Type
     * instance.
     *
     * @param o the object to be compared.
     * @return <code>true</code> if the objects are equal, otherwise false.
     */
    public boolean equals(Object o) {
        boolean isEqual = true;
        
        if (!(o instanceof PersonStatus)) {
            isEqual = false;
        } else {
            isEqual = (((PersonStatus)o).getID().equals(this.getID()));
        }
        
        return isEqual;
    }

    /**
     * Return a valid hash code for this object.  This allows the object to be
     * looked up in a hash table with minimal required hash table buckets.
     *
     * @return a valid hash code.
     */
    public int hashCode() {
        int result;
        result = (nameToken != null ? nameToken.hashCode() : 0);
        result = 29 * result + id.hashCode();
        return result;
    }


    /**
     * Get the HTML for all of the <option> tags which represent that available
     * Person Status types.  We do not return the surrounding <select> tags as
     * the HTML author might need to set custom values in that tag.
     *
     * @return a <code>String</code> value containing all of the HTML select
     * values.
     */
    public static String getHTMLOptionList() {
        return getHTMLOptionList(UNREGISTERED);
    }   

    /**
     * Get the HTML for all of the <option> tags which represent that available
     * PersonStatus types.  We do not return the surrounding <select> tags as
     * the HTML author might need to set custom values in that tag.
     *
     * @param selectedConstraint a <code>PersonStatus</code> object which
     * represents the default value selected in the select list.
     * @return a <code>String</code> value containing all of the HTML select
     * values.
     */
    public static String getHTMLOptionList(PersonStatus selectedConstraint) {
        StringBuffer html = new StringBuffer();
        
        Iterator it = personStatusList.iterator();
        while (it.hasNext()) {
            PersonStatus ps = (PersonStatus)it.next();
            html.append("<option value=\"").append(ps.getID()).append("\"")
                .append((selectedConstraint.equals(ps) ? " SELECTED" : ""))
                .append(">").append(ps.getName()).append("</option>\r\n");
        }
        
        return html.toString();
    }

   /**
    * Converts the object to XML representation without the XML version tag.
    * This method returns the object as XML text. 
    *
    * @return XML representation
    */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();     
        xml.ensureCapacity(100);
        xml.append("<PersonStatus>\n");
        xml.append("<id>" + id + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("</PersonStatus>\n");

        return xml.toString();
    }


   /**
    * Converts the object to XML representation.
    * This method returns the object as XML text. 
    *
    * @return XML representation
    *
    */
    public String getXML() {
        return(IXMLPersistence.XML_VERSION + getXMLBody() );
    }

}
