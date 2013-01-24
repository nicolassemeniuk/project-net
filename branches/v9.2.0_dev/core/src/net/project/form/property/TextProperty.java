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
package net.project.form.property;

/**
 * A TextProperty is used for capturing text values.
 * It is rendered as an HTML <code>&lt;input type="text" /&gt;</code> element.
 */
class TextProperty extends PersistentCustomProperty {
    
    // DisplayWidth of property
    private int displayWidth = 20;
    
    private boolean numValidationRequired;
    
    private int maxlength = 0;

    public TextProperty(String id, String displayName) {
        super(id, displayName);
    }
    
    /**
     * Sets the display width for this property.
     * @param displayWidth the display width, in characters.
     */
    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    /**
     * Returns the display width of this property.
     * @return the display width, in characters
     */
    public int getDisplayWidth() {
        return this.displayWidth;
    }


    /**
     * Returns this custom property as HTML presentation for editing property
     * value.
     * @return HTML presentation of custom property
     */
    public String getPresentationHTML() {
        StringBuffer s = new StringBuffer();

        // Label
        s.append("<td class=\"tableHeader\" align=\"left\">" + getDisplayName() + ":</td>");

        // HTML Form field
        s.append("<td class=\"tableHeader\" align=\"left\">");
        s.append("<input type=\"text\" ");
        s.append("name=\"" + getID() + "\" ");
        s.append("value=\"" + getDisplayValue() + "\" ");
        s.append("size=\"" + getDisplayWidth() + "\" ");
        if(getMaxlength() != 0) {
        	s.append("maxlength=\"" + getMaxlength() + "\" ");
        }
        if(isValueRequired()){
        	s.append("required=\"true\" ");
        }
        if(isNumValidationRequired()) {
        	s.append("onkeypress=\"javascript:return isNumberKey(event);\"");
        }
        s.append("/>");
        if (isValueRequired()) {
            s.append(getRequiredValueJavascript());
        }
        s.append("</td>");

        return s.toString();
    }

	/**
	 * @return the maxlength
	 */
	public int getMaxlength() {
		return maxlength;
	}

	/**
	 * @param maxlength the maxlength to set
	 */
	public void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}
	

	/**
	 * @return the numValidationRequired
	 */
	public boolean isNumValidationRequired() {
		return numValidationRequired;
	}

	/**
	 * @param numValidationRequired the numValidationRequired to set
	 */
	public void setNumValidationRequired(boolean numValidationRequired) {
		this.numValidationRequired = numValidationRequired;
	}

}
