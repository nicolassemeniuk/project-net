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
import java.util.Date;

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
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}description"/>
 *         &lt;element ref="{}abbreviation"/>
 *         &lt;element ref="{}include_assignments"/>
 *         &lt;element ref="{}version"/>     
 *         &lt;element ref="{}creation_date"/>             
 *         &lt;element ref="{}FormFields"/>
 *         &lt;element ref="{}FormLists"/>
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
    "name",
    "description",
    "abbreviation",
    "includeAssignments",
    "version",
    "creationDate",
    "formFields",
    "formLists"
})
@XmlRootElement(name = "formDef")
public class FormDef {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String abbreviation;
    @XmlElement(name = "include_assignments")
    protected boolean includeAssignments;    
    @XmlElement(name = "version")
    protected String version;    
    @XmlElement(name = "creation_date")
    protected Date creationDate;    
    @XmlElement(name = "FormFields", required = true)
    protected FormFields formFields;
    @XmlElement(name = "FormLists", required = true)
    protected FormLists formLists;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
  /*  public BigInteger getId() {
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
     * Gets the value of the classTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
  /*  public BigInteger getClassTypeId() {
        return classTypeId;
    }*/

    /**
     * Sets the value of the classTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
/*    public void setClassTypeId(BigInteger value) {
        this.classTypeId = value;
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
     * Gets the value of the abbreviation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Sets the value of the abbreviation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbbreviation(String value) {
        this.abbreviation = value;
    }

    /**
     * Gets the value of the maxRow property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
/*    public Integer getMaxRow() {
        return maxRow;
    }*/

    /**
     * Sets the value of the maxRow property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
/*    public void setMaxRow(Integer value) {
        this.maxRow = value;
    }*/

    /**
     * Gets the value of the maxColumn property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
/*    public Integer getMaxColumn() {
        return maxColumn;
    }*/

    /**
     * Sets the value of the maxColumn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
/*    public void setMaxColumn(Integer value) {
        this.maxColumn = value;
    }*/

    /**
     * Gets the value of the formFields property.
     * 
     * @return
     *     possible object is
     *     {@link FormFields }
     *     
     */
    public FormFields getFormFields() {
        return formFields;
    }

    /**
     * Sets the value of the formFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormFields }
     *     
     */
    public void setFormFields(FormFields value) {
        this.formFields = value;
    }

    /**
     * Gets the value of the formLists property.
     * 
     * @return
     *     possible object is
     *     {@link FormLists }
     *     
     */
    public FormLists getFormLists() {
        return formLists;
    }

    /**
     * Sets the value of the formLists property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormLists }
     *     
     */
    public void setFormLists(FormLists value) {
        this.formLists = value;
    }
    
    /**
     * Gets the value of the useDefault property.
     * 
     */
    public boolean isIncludeAssignments() {
        return includeAssignments;
    }

    /**
     * Sets the value of the useDefault property.
     * 
     */
    public void setIncludeAssignments(boolean value) {
        this.includeAssignments = value;
    }

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	
	
        
}