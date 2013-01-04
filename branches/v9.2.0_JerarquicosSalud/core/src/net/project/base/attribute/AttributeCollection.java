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
|   $Revision: 20062 $
|       $Date: 2009-10-06 05:04:10 -0300 (mar, 06 oct 2009) $
|     $Author: dpatil $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.attribute;

import java.sql.SQLException;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;

/**
 * A Attributes class is the collection of  various types of Attribute.
 *
 * @author Deepak
 * @since emu
 */
public class AttributeCollection extends java.util.ArrayList implements IXMLPersistence {

    /**
     * Creates an empty AttributeCollection.
     */
    public AttributeCollection() {
        super();
    }

    /**
     * Creates an empty AttributeCollection with the specified initial
     * capacity.
     * @param initialCapacity the initial capcity of this collection
     * @see java.util.ArrayList#ArrayList(int)
     */
    public AttributeCollection(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Returns the attribute with specified id.
     * @param attributeID the id of the attribute to find
     * @return the attribute, or null if no matching attribute was found
     */   
    public IAttribute getAttributeByID(String attributeID) {
        IAttribute foundAttribute = null;

        // Iterate over all attributes, breaking when an attribute is found
        for (Iterator it = iterator(); it.hasNext() && foundAttribute == null; ) {
            IAttribute nextAttribute = (IAttribute) it.next();
            if (nextAttribute != null && nextAttribute.getID().equals(attributeID)) {
                foundAttribute = nextAttribute;
            }
        }

        return foundAttribute;
    }

    /**
     * Indicates whether this attribute collection contains an 
     * attribute with the specified ID.
     * @return true if this collection contains an attribute with
     * the specified id; false otherwise
     */
    public boolean containsAttributeByID(String attributeID) {
        return (getAttributeByID(attributeID) != null);
    }

    /**
     * Returns the IAttribute type of Object based on the ID passed passed 
     * @return IAttribute
     * @param name a <code>String</code> value containing the name of the
     * Attribute.
     * @deprecated As of Gecko Update 3; This method may be removed
     * after two releases. <br>
     * The display name is determined by a property value; it should
     * never be programmed against.  Additionally, this method
     * returns the last attribute if no matching attribute is found;
     * this is not documented and is generally an undesirable behavior.
     * Finally, the javadoc on the method is wrong
     */   
    public IAttribute getAttributeByName (String name) {
        Iterator itr = this.iterator();
        IAttribute iattr=null;

        while (itr.hasNext()) {

            iattr = (IAttribute)itr.next();

            if (iattr.getDisplayName().equals(name)) {
                return iattr;
            }
        }

        return iattr;
    }


    public AttributeCollection getAttributeCollectionByCategory (AttributeCategory category) {
        AttributeCollection collection = new AttributeCollection();
        Iterator attributes = this.iterator();
        IAttribute attribute = null;

        while (attributes.hasNext()) {

            attribute = (IAttribute) attributes.next();
            if (attribute.inCategory (category)) {
                collection.add (attribute);
            }
        }

        return collection;
    }

    public String getXML() throws SQLException {
        StringBuffer xml = new StringBuffer();

        xml.append (XML_VERSION);
        xml.append (getXMLBody());

        return xml.toString();
    }


    public String getXMLBody() throws SQLException {
        StringBuffer xml = new StringBuffer();
        Iterator attributes = this.iterator();

        xml.append ("<AttributeCollection>");

        while (attributes.hasNext()) {

            IAttribute attribute = (IAttribute) attributes.next();
            xml.append (attribute.getXMLBody());
        }

        xml.append ("</AttributeCollection>");

        return xml.toString();
    }
}
