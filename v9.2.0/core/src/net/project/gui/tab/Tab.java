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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.gui.tab;

import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLUtils;

public class Tab implements java.io.Serializable, IXMLPersistence {

    /** Text label to appear on tab */
    private String label = null;

    /** Text label token to appear on tab */
    private String labelToken = null;

    /** href to navigate to when clicked */
    private String href = null;

    /** indicates whether tab is selected or not */
    private boolean isSelected = false;

    /** indicates whether tab is displayed. */
    private boolean isDisplay = true;

    /** indicates whether the tab is clickable by default */
    private boolean isClickable = true;

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelToken() {
        return this.labelToken;
    }

    public void setLabelToken(String labelToken) {
        this.labelToken = labelToken;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isClickable() {
        return this.isClickable;
    }

    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }


    /**
     * Sets whether this tab should be displayed.
     * @param isDisplay true if this tab should be displayed; false if not.
     * Default value of <code>true</code>
     */
    public void setDisplay(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    protected boolean isDisplay() {
        return this.isDisplay;
    }

    public String getResolvedLabel() {
        // Return the label or label token value
        return getProperty(getLabelToken(), getLabel());
    }

    /**
     * Return tab XML body including version tag
     * @return the xml string
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return tab XML body without version tag.
     * @return the xml string
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<tab>");
        xml.append("<label>" + XMLUtils.escape(getLabel()) + "</label>");
        xml.append("<href>" + XMLUtils.escape(getHref()) + "</href>");
        xml.append("<is_selected>" + XMLUtils.escape((isSelected() ? "1" : "0")) + "</is_selected>");
        xml.append("<is_display>" + XMLUtils.escape((isDisplay() ? "1" : "0")) + "</is_display>");
        xml.append("<is_clickable>" + XMLUtils.escape((isClickable() ? "1" : "0")) + "</is_clickable>");
        xml.append("</tab>");
        return xml.toString();
    }

    /**
     * Returns the absolute value if not null or the value for propertyName.
     * @param propertyName the property name to get the value for
     * @param absoluteValue the value to return if present
     */
    private String getProperty(String propertyName, String absoluteValue) {
        String value = null;

        if (absoluteValue != null) {
            value = absoluteValue;

        } else {
            // Lookup up the property name if specified
            if (propertyName != null) {
                value = net.project.base.property.PropertyProvider.get(propertyName);
            }

        }

        return value;
    }

}
