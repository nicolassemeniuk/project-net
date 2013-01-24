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

 package net.project.taglibs.input;

import javax.servlet.jsp.JspTagException;

public class RadioTag extends AbstractInputTag {
    /**
     * Constructs the HTML for the input element. Sub-classes should construct
     * the appropriate HTML input element from the current attributes.
     *
     * @return the input text element HTML code
     * @throws javax.servlet.jsp.JspTagException if there is a problem
     * constructing the input element
     */
    protected String constructInputElement() throws JspTagException {
        String radioText = "<input type=\"radio\"";
        radioText += formatAllAttributes();
        radioText += "/>";

        return radioText;
    }

    /**
     * Processes the specified attribute and value while formatting all
     * attributes.
     * <p/>
     * Sub-classes should override this to apply and special handling to
     * particular attributes. <br> The default implementation performs no
     * action; that is, value is returned unmodified. </p>
     *
     * @param attributeName the attribute to which the value belongs
     * @param value the value for the attribute
     * @return the processed value
     * @see #formatAllAttributes()
     */
    protected Object process(String attributeName, Object value) {
        Object result = value;

        if ("value".equals(attributeName)) {
            // We escape double quotes for the value attribute
            result = replaceDoubleQuotes((String) value);
        }

        return result;
    }

    /**
     * Sub-classes should clear all their custom attributes. This is called to
     * prepare the tag instance for re-use.
     */
    protected void clear() {
    }
}
