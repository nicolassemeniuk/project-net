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

package net.project.portfolio.view;

import javax.servlet.http.HttpServletRequest;

import net.project.xml.XMLUtils;
import net.project.xml.document.XMLDocument;

import org.jdom.Element;

public class MetaColumn {

    private String propertyName;

    private boolean metaProperty;

    private boolean include;

    private int columnOrder;

    private String description;

    private String shortDescription;

    private int columnWidth;

    private boolean noWrap;

    private String _orderId;
    
    private String category;
    
    private String columnName;
    
    public MetaColumn() {

    }

    public MetaColumn(String propertyName, boolean metaProperty,
            String description , String category) {
        this(propertyName, metaProperty, description, description, category);

    }

    public MetaColumn(String propertyName, boolean metaProperty,
            String description, String shortDescription, String category) {

        this.propertyName = propertyName;
        this.metaProperty = metaProperty;
        this.description = description;
        this.shortDescription = shortDescription;
        this.category = category;
    }

    public MetaColumn(String propertyName, boolean metaProperty,
            String description, String shortDescription, String category,
            boolean include, int columnWidth) {

        this.propertyName = propertyName;
        this.metaProperty = metaProperty;
        this.description = description;
        this.shortDescription = shortDescription;
        this.category = category;
        this.include = include;
        this.columnWidth = columnWidth;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isMetaProperty() {
        return this.metaProperty;
    }

    public void setMetaProperty(boolean metaProperty) {
        this.metaProperty = metaProperty;
    }

    public boolean isInclude() {
        return this.include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }

    public int getColumnOrder() {
        return this.columnOrder;
    }

    public void setColumnOrder(int columnOrder) {
        this.columnOrder = columnOrder;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        if (this.shortDescription == null || "".equals(this.shortDescription)) {
            return this.description;
        } else {
            return this.shortDescription;
        }

    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getColumnWidth() {
        return this.columnWidth;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public boolean isNoWrap() {
        return this.noWrap;
    }

    public void setNoWrap(boolean noWrap) {
        this.noWrap = noWrap;
    }

    @Override
    public boolean equals(Object obj) {

        MetaColumn another = (MetaColumn) obj;

        if (this.propertyName != null
                && this.propertyName.equals(another.propertyName)) {
            return true;
        } else {
            return false;
        }

    }

    public void saveToXML(XMLDocument doc) {

        doc.startElement("MetaColumn");
        doc.addElement("PropertyName", this.propertyName);
        doc.addElement("MetaProperty", this.metaProperty);
        doc.addElement("Include", this.include);
        doc.addElement("ColumnOrder", this.columnOrder);
        doc.addElement("Description", this.description);
        doc.addElement("ShortDescription", this.shortDescription);
        doc.addElement("ColumnWidth", this.columnWidth);
        doc.addElement("NoWrap", this.noWrap);
        doc.endElement();

    }

    public void loadFromXML(Element element) {

        this.setPropertyName(element.getChildTextTrim("PropertyName"));
        this.setMetaProperty(XMLUtils.parseBoolean(element
                .getChildTextTrim("MetaProperty")));
        this.setInclude(XMLUtils.parseBoolean(element
                .getChildTextTrim("Include")));
        
        try {
            this.setColumnOrder(Integer.parseInt(element
                    .getChildTextTrim("ColumnOrder")));
        } catch (Exception ignored) {

        }

        this.setDescription(element.getChildTextTrim("Description"));
        this.setShortDescription(element.getChildTextTrim("ShortDescription"));
        try {

            this.setColumnWidth(Integer.parseInt(element
                    .getChildTextTrim("ColumnWidth")));

        } catch (Exception ignored) {

        }

        this.setNoWrap(XMLUtils
                .parseBoolean(element.getChildTextTrim("NoWrap")));
 
    }

    public String getHTML() {

        StringBuffer result = new StringBuffer();

        // include
        result.append("<td class=\"tableContent\">\n");
        if ("name".equals(this.propertyName)) {
            result
                    .append("<input type=\"checkbox\" checked disabled name=\"metainclude"
                            + this.getMyID() + "\" value=\"1\">\n");
        } else {
            result.append("<input type=\"checkbox\" "
                    + (this.isInclude() ? "checked" : "")
                    + " name=\"metainclude" + this.getMyID()
                    + "\" value=\"1\">\n");

        }

        result.append("</td>\n");

        // column order

        result.append("<td class=\"tableContent\">\n");

        result.append("<input type=\"text\" id=\"orderid" + this.getOrderId()
                + "\" name=\"metacolumnorder" + this.getMyID()
                + "\" size=\"2\" maxlength=\"2\" value=\""
                + this.zeroToEmptyString(this.getColumnOrder()) + "\">\n");

        result.append("</td>\n");

        // description

        result.append("<td class=\"tableContent\">\n");

        result.append(this.getDescription());

        result.append("</td>\n");

        // column width

        result.append("<td class=\"tableContent\">\n");

        result.append("<input type=\"text\" name=\"metacolumnwidth"
                + this.getMyID() + "\" size=\"2\" maxlength=\"3\" value=\""
                + this.zeroToEmptyString(this.getColumnWidth()) + "\">%\n");

        result.append("</td>\n");

        // no wrap

        result.append("<td class=\"tableContent\">\n");

        result.append("<input type=\"checkbox\" "
                + (this.isNoWrap() ? "checked" : "") + " name=\"metanowrap"
                + this.getMyID() + "\" value=\"1\">\n");

        result.append("</td>\n");

        return result.toString();

    }

    public void updateFromRequest(HttpServletRequest request) {

        String s = request.getParameter("metainclude" + this.getMyID());

        this.setInclude(s != null && s.trim().length() > 0);

        if ("name".equals(this.propertyName)) {
            this.setInclude(true);
        }

        s = request.getParameter("metacolumnorder" + this.getMyID());

        try {

            this.setColumnOrder(Integer.parseInt(s));

        } catch (Exception ignored) {

            this.setColumnOrder(0);

        }

        s = request.getParameter("metacolumnwidth" + this.getMyID());

        try {

            this.setColumnWidth(Integer.parseInt(s));

        } catch (Exception ignored) {

            this.setColumnWidth(0);

        }

        s = request.getParameter("metanowrap" + this.getMyID());

        this.setNoWrap(s != null && s.trim().length() > 0);

    }

    private String zeroToEmptyString(int i) {

        if (i == 0) {
            return "";
        } else {
            return "" + i;
        }

    }

    private String getMyID() {
        return "" + this.propertyName.hashCode();
    }

    public String getOrderId() {
        return this._orderId;
    }

    public void setOrderId(String orderId) {
        this._orderId = orderId;
    }

	/**
	 * @return the dragable
	 */
	public boolean isDragable() {
		return !("Name").equalsIgnoreCase(this.propertyName);
	}
	
	/**
	 * @return ths hidable
	 */
	public boolean isHidable() {
		return !("Name").equalsIgnoreCase(this.propertyName);
	}

	/**
     * To update column properties from project portfolio two pane view
     * @param projectColumn
     */
    public void updateFromProjectPortfolio(MetaColumn projectColumn) {
        this.setInclude(projectColumn.isInclude());
        this.setColumnOrder(projectColumn.getColumnOrder());
        this.setColumnWidth(projectColumn.getColumnWidth());
    }

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * Method to check whether category is status
	 * 
	 * @return true / false
	 */
	public boolean isStatus(){
		return ("status").equals(this.category);
	}
	
	/**
	 * Method to check whether category is general
	 * 
	 * @return true / false
	 */
	public boolean isGeneral(){
		return ("general").equals(this.category);
	}
	
	/**
	 * Method to check whether category is calculated
	 * 
	 * @return true / false
	 */
	public boolean isCompletion(){
		return ("completion").equals(this.category);
	}
	
	/**
	 * Method to check whether category is calculated
	 * 
	 * @return true / false
	 */
	public boolean isFinancial(){
		return ("financial").equals(this.category);
	}

	/**
	 * Method to check abbreviated lable to be displayed for column name
	 * 
	 * @return true / false
	 */
	public boolean isShortLabel(){
		return (("OverallStatus").equals(this.propertyName) || ("FinancialStatus").equals(this.propertyName) || ("ScheduleStatus").equals(this.propertyName) || ("ResourceStatus").equals(this.propertyName));
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
