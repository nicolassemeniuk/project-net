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
package net.project.base.attribute;

import java.io.Serializable;

import net.project.datatransform.csv.transformer.AbstractDataTransformer;


public interface IAttribute extends net.project.persistence.IXMLPersistence, Serializable {
    public abstract AbstractDataTransformer getDataTransformer(String id,IAttribute iAttribute);

    /**
     * Returns the DisplayName for the Attribute
     * @return String
     */
    public String getDisplayName();

    /**
     * Returns the ID of the Object
     * @return String
     */
    public String getID();

    /**
     * Returns true if the attribute is required, false if not
     * 
     * @return  Returns true if the attribute is required, false if not
     * @since Gecko
     */
    public boolean isRequired();

    /**
     * Returns true if the specified IAttribute is in the specified AttributeCategory.
     * 
     * @param category The attribute category to be tested against.
     * @return True if the attribute is in the category, false if not.
     * 
     * @see net.project.base.attribute.AttributeCategory
     * @since Gecko
     */
    public boolean inCategory (AttributeCategory category);

}

