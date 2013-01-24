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

/**
 * Provides an enhanced alternative to the HTML <code>&lt;input type="text"...&gt;</code>
 * element.
 * <p>
 * Includes escaping of values to allow double- and single-quotes in values.
 * </p>
 */
public class TextTag extends AbstractInputTag {

    /**
     * Creates an empty TextTag.
     */
    public TextTag() {
        super();
    }

    /**
     * Constructs the HTML for the input element.
     * Attributes are added in the order used in the taglib.
     * Calls {@link #formatAllAttributes}.
     * @return the input text element HTML code
     * @throws JspTagException if there is a problem constructing the input element
     */
    protected String constructInputElement()  throws JspTagException {
        StringBuffer elementText = new StringBuffer();

        elementText.append("<input type=\"text\"");
        elementText.append(formatAllAttributes());
        elementText.append(" />");

        return elementText.toString();
    }

    /**
     * Provides an implementation that handles certain attributes.
     * <p>
     * The following attributes are handled:
     * <ul>
     * <li>value - replaces double-quotes with entity <code>&amp;#034;</code>
     * </ul>
     * @param attributeName the name of the attribute
     * @param value the attribute's value
     * @return the result; untouched
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
     * Currently does nothing since TextTag has no
     * custom attributes.
     * <p>
     * Sub-classes should call this method.
     * </p>
     */
    protected void clear() {
        // Do nothing; no more items to clear
    }

}
