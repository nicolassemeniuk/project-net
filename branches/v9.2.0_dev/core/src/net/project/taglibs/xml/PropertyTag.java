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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.xml;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.property.PropertyProvider;

/**
 * Specifies property names and values to be included in XML processed
 * by a stylesheet.
 */
public class PropertyTag extends TagSupport {

    //
    // Attributes
    //

    private String name = null;
    private String value = null;


    //
    // Attribute Setters
    //

    /**
     * Sets this property's names.
     * This is optional; It may be omitted when the value is a token.  In that
     * case the token name is used as the name.  The name is always used if specified.
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets this property's value.
     * Required.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }


    //
    // Overriding TagSupport methods
    //

    /**
     * Adds this property to enclosing transform tag.
     * @throws JspTagException if this property tag is not within a
     * transform tag.
     */
    public int doEndTag() throws JspTagException {
        try {
            String propertyName = null;
            String propertyValue = null;

            TransformTag transformTag = (TransformTag) findAncestorWithClass(this, TransformTag.class);
            if (transformTag == null) {
                throw new JspTagException("Error in property tag: property not inside transform tag");
            }

            if (this.value == null) {
                throw new JspTagException("Error in property tag: Required attribute value not specified");
            }

            // Determine property name and value from attributes
            // and add property to transform tag

            if (PropertyProvider.isToken(this.value)) {
                
                // The value specified is a token
                // so we use that token name as the property name
                // unless the name attribute was also specified; the name
                // attribute takes precedence
                if (this.name == null) {
                    propertyName = PropertyProvider.stripTokenPrefix(this.value);
                
                } else {
                    propertyName = this.name;
                
                }
                
                // The property value is the value of the token
                propertyValue = PropertyProvider.get(this.value);
            
            } else {

                // The value is not a token, therefore we must have a name
                // attribute.
                // The name and value attributes are used for the property
                // name and value
                if (this.name == null) {
                    throw new JspTagException("Error in property tag: Attribute 'name' must be specified when 'value' attribute is not a token");
                
                } else {
                    propertyName = this.name;
                    propertyValue = this.value;
                
                }

            }

            // Add property to transform tag
            transformTag.addProperty(propertyName, propertyValue);

            return this.EVAL_PAGE;
        
        } finally {
            clear();

        }
    }


    //
    // Additional utility methods
    //

    /**
     * Clears out all properties.
     * This is should be called when the tag is finished to avoid lingering
     * values when this tag object is reused.
     */
    void clear() {
        this.name = null;
        this.value = null;
    }

}
