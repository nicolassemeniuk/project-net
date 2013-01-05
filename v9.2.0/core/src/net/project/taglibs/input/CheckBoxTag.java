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

public class CheckBoxTag extends AbstractInputTag {
    /**
     * Constructs the HTML for the input element.
     * Sub-classes should construct the appropriate HTML input element
     * from the current attributes.
     *
     * @return the input text element HTML code
     * @throws javax.servlet.jsp.JspTagException
     *          if there is a problem constructing the input element
     */
    protected String constructInputElement() throws JspTagException {
        StringBuffer elementText = new StringBuffer();

        elementText.append("<input type=\"checkbox\"");
        elementText.append(formatAllAttributes());
        elementText.append(">");

        return elementText.toString();
    }

    /**
     * Sub-classes should clear all their custom attributes.
     * This is called to prepare the tag instance for re-use.
     */
    protected void clear() {
        //Nothing custom to delete
    }

    public void setChecked(boolean checked) {
        this.getAttributeValueMap().put("checked", new Boolean(checked));
    }

    public boolean isChecked() {
        Boolean checked = ((Boolean)this.getAttributeValueMap().get("checked"));
        return (checked == null ? false : checked.booleanValue());
    }
}
