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

 package net.project.taglibs.xml;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Specifies parameter names and values to pass to an XSL stylesheet.
 * <p/>
 * Use the following in the XSL file to access the parameter: <br>
 * <code>&lt;xsl:param name="<i>parameterName</i>" /&gt;</code>
 * @author Tim Morrow
 * @since Version 7.7.1
 */
public class ParamTag extends TagSupport {

    //
    // Attributes
    //

    private String name = null;
    private Object value = null;


    //
    // Attribute Setters
    //

    /**
     * Sets the parameter name.
     * @param name the name of the parameter
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the parameter value.
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Adds this property to enclosing transform tag.
     * @throws javax.servlet.jsp.JspTagException if this property tag is not within a
     * transform tag.
     */
    public int doEndTag() throws JspTagException {
        try {
            TransformTag transformTag = (TransformTag) findAncestorWithClass(this, TransformTag.class);
            if (transformTag == null) {
                throw new JspTagException("Unable to locate ancestor TransformTag");
            }

            if (this.name == null) {
                throw new JspTagException("Missing required attribute 'name'");
            }

            if (this.value == null) {
                throw new JspTagException("Missing required attribute 'value'");
            }

            // Add the parameter to the transformer
            transformTag.addParameter(name, value);

        } finally {
            clear();
        }

        return EVAL_PAGE;
    }


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
