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
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}is_default"/>
 *         &lt;element ref="{}is_shared"/>
 *         &lt;element ref="{}is_admin"/>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}description"/>
 *         &lt;element ref="{}ListField" maxOccurs="unbounded"/>
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
    "isDefault",
    "isShared",
    "isAdmin",
    "name",
    "description",
    "listField"
})
@XmlRootElement(name = "FormList")
public class FormList {

    @XmlElement(name = "is_default")
    protected boolean isDefault;
    @XmlElement(name = "is_shared")
    protected boolean isShared;
    @XmlElement(name = "is_admin")
    protected boolean isAdmin;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "ListField", required = true)
    protected List<ListField> listField;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
 /*   public BigInteger getId() {
        return id;
    }*/

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
/*    public void setId(BigInteger value) {
        this.id = value;
    }*/

    /**
     * Gets the value of the isDefault property.
     * 
     */
    public boolean isIsDefault() {
        return isDefault;
    }

    /**
     * Sets the value of the isDefault property.
     * 
     */
    public void setIsDefault(boolean value) {
        this.isDefault = value;
    }

    /**
     * Gets the value of the isShared property.
     * 
     */
    public boolean isIsShared() {
        return isShared;
    }

    /**
     * Sets the value of the isShared property.
     * 
     */
    public void setIsShared(boolean value) {
        this.isShared = value;
    }

    /**
     * Gets the value of the isAdmin property.
     * 
     */
    public boolean isIsAdmin() {
        return isAdmin;
    }

    /**
     * Sets the value of the isAdmin property.
     * 
     */
    public void setIsAdmin(boolean value) {
        this.isAdmin = value;
    }

    /**
     * Gets the value of the ownerSpaceId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
/*    public BigInteger getOwnerSpaceId() {
        return ownerSpaceId;
    }*/

    /**
     * Sets the value of the ownerSpaceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
/*    public void setOwnerSpaceId(BigInteger value) {
        this.ownerSpaceId = value;
    }*/

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the fieldCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
/*    public Integer getFieldCount() {
        return fieldCount;
    }*/

    /**
     * Sets the value of the fieldCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
/*    public void setFieldCount(Integer value) {
        this.fieldCount = value;
    }*/

    /**
     * Gets the value of the listField property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listField property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListField }
     * 
     * 
     */
    public List<ListField> getListField() {
        if (listField == null) {
            listField = new ArrayList<ListField>();
        }
        return this.listField;
    }

}