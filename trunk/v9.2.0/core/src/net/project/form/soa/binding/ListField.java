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
package net.project.form.soa.binding;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}id"/>
 *         &lt;element ref="{}field_width"/>
 *         &lt;element ref="{}field_order"/>
 *         &lt;element ref="{}wrap_mode"/>
 *         &lt;element ref="{}is_subfield"/>
 *         &lt;element ref="{}is_list_field"/>
 *         &lt;element ref="{}is_sort_field"/>
 *         &lt;element ref="{}is_calculate_total"/>
 *         &lt;element ref="{}sort_order"/>
 *         &lt;element ref="{}sort_ascending"/>
 *         &lt;element ref="{}FieldFilter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "fieldWidth",
    "fieldOrder",
    "wrapMode",
    "isSubfield",
    "isListField",    
    "isSortField",
    "isCalculateTotal",
    "sortOrder",
    "sortAscending",
    "fieldFilter"
})
@XmlRootElement(name = "ListField")
public class ListField {

    @XmlElement(required = true)
    protected BigInteger id;
    @XmlElement(name = "field_width", required = true)
    protected String fieldWidth;
    @XmlElement(name = "field_order", required = true)
    protected Integer fieldOrder;
    @XmlElement(name = "wrap_mode")
    protected boolean wrapMode;
    @XmlElement(name = "is_subfield")
    protected boolean isSubfield;
    @XmlElement(name = "is_list_field")
    protected boolean isListField;
    @XmlElement(name = "is_sort_field")
    protected boolean isSortField;
    @XmlElement(name = "is_calculate_total")
    protected boolean isCalculateTotal;    
    @XmlElement(name = "sort_order", required = true)
    protected Integer sortOrder;
    @XmlElement(name = "sort_ascending")
    protected boolean sortAscending;
    @XmlElement(name = "FieldFilter")
    protected FieldFilter fieldFilter;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the fieldWidth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldWidth() {
        return fieldWidth;
    }

    /**
     * Sets the value of the fieldWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldWidth(String value) {
        this.fieldWidth = value;
    }

    /**
     * Gets the value of the fieldOrder property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public Integer getFieldOrder() {
        return fieldOrder;
    }

    /**
     * Sets the value of the fieldOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFieldOrder(Integer value) {
        this.fieldOrder = value;
    }

    /**
     * Gets the value of the wrapMode property.
     * 
     */
    public boolean isWrapMode() {
        return wrapMode;
    }

    /**
     * Sets the value of the wrapMode property.
     * 
     */
    public void setWrapMode(boolean value) {
        this.wrapMode = value;
    }

    /**
     * Gets the value of the isSubfield property.
     * 
     */
    public boolean isIsSubfield() {
        return isSubfield;
    }

    /**
     * Sets the value of the isSubfield property.
     * 
     */
    public void setIsSubfield(boolean value) {
        this.isSubfield = value;
    }

    /**
     * Gets the value of the isListField property.
     * 
     */
    public boolean isIsListField() {
        return isListField;
    }

    /**
     * Sets the value of the isListField property.
     * 
     */
    public void setIsListField(boolean value) {
        this.isListField = value;
    }

    /**
     * Gets the value of the isSortField property.
     * 
     */
    public boolean isIsSortField() {
        return isSortField;
    }

    /**
     * Sets the value of the isSortField property.
     * 
     */
    public void setIsSortField(boolean value) {
        this.isSortField = value;
    }
    
    /**
     * Gets the value of the isCalculateTotal property.
     * 
     */
    public boolean isIsCalculateTotal() {
        return isCalculateTotal;
    }

    /**
     * Sets the value of the isCalculateTotal property.
     * 
     */
    public void setIsCalculateTotal(boolean value) {
        this.isCalculateTotal = value;
    }    
    

    /**
     * Gets the value of the sortOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSortOrder(Integer value) {
        this.sortOrder = value;
    }

    /**
     * Gets the value of the sortAscending property.
     * 
     */
    public boolean isSortAscending() {
        return sortAscending;
    }

    /**
     * Sets the value of the sortAscending property.
     * 
     */
    public void setSortAscending(boolean value) {
        this.sortAscending = value;
    }

    /**
     * Gets the value of the fieldFilter property.
     * 
     * @return
     *     possible object is
     *     {@link FieldFilter }
     *     
     */
    public FieldFilter getFieldFilter() {
        return fieldFilter;
    }

    /**
     * Sets the value of the fieldFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FieldFilter }
     *     
     */
    public void setFieldFilter(FieldFilter value) {
        this.fieldFilter = value;
    }

}