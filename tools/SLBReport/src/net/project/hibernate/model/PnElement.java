package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnElement implements Serializable {

    /** identifier field */
    private BigDecimal elementId;

    /** persistent field */
    private String elementName;

    /** nullable persistent field */
    private String elementDesc;

    /** nullable persistent field */
    private BigDecimal elementType;

    /** nullable persistent field */
    private String elementLabel;

    /** nullable persistent field */
    private String dbFieldDatatype;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnElementProperties;

    /** persistent field */
    private Set pnClassFields;

    /** persistent field */
    private Set pnClassTypeElements;

    /** persistent field */
    private Set pnElementDisplayClasses;

    /** full constructor */
    public PnElement(BigDecimal elementId, String elementName, String elementDesc, BigDecimal elementType, String elementLabel, String dbFieldDatatype, String recordStatus, Set pnElementProperties, Set pnClassFields, Set pnClassTypeElements, Set pnElementDisplayClasses) {
        this.elementId = elementId;
        this.elementName = elementName;
        this.elementDesc = elementDesc;
        this.elementType = elementType;
        this.elementLabel = elementLabel;
        this.dbFieldDatatype = dbFieldDatatype;
        this.recordStatus = recordStatus;
        this.pnElementProperties = pnElementProperties;
        this.pnClassFields = pnClassFields;
        this.pnClassTypeElements = pnClassTypeElements;
        this.pnElementDisplayClasses = pnElementDisplayClasses;
    }

    /** default constructor */
    public PnElement() {
    }

    /** minimal constructor */
    public PnElement(BigDecimal elementId, String elementName, String recordStatus, Set pnElementProperties, Set pnClassFields, Set pnClassTypeElements, Set pnElementDisplayClasses) {
        this.elementId = elementId;
        this.elementName = elementName;
        this.recordStatus = recordStatus;
        this.pnElementProperties = pnElementProperties;
        this.pnClassFields = pnClassFields;
        this.pnClassTypeElements = pnClassTypeElements;
        this.pnElementDisplayClasses = pnElementDisplayClasses;
    }

    public BigDecimal getElementId() {
        return this.elementId;
    }

    public void setElementId(BigDecimal elementId) {
        this.elementId = elementId;
    }

    public String getElementName() {
        return this.elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementDesc() {
        return this.elementDesc;
    }

    public void setElementDesc(String elementDesc) {
        this.elementDesc = elementDesc;
    }

    public BigDecimal getElementType() {
        return this.elementType;
    }

    public void setElementType(BigDecimal elementType) {
        this.elementType = elementType;
    }

    public String getElementLabel() {
        return this.elementLabel;
    }

    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

    public String getDbFieldDatatype() {
        return this.dbFieldDatatype;
    }

    public void setDbFieldDatatype(String dbFieldDatatype) {
        this.dbFieldDatatype = dbFieldDatatype;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnElementProperties() {
        return this.pnElementProperties;
    }

    public void setPnElementProperties(Set pnElementProperties) {
        this.pnElementProperties = pnElementProperties;
    }

    public Set getPnClassFields() {
        return this.pnClassFields;
    }

    public void setPnClassFields(Set pnClassFields) {
        this.pnClassFields = pnClassFields;
    }

    public Set getPnClassTypeElements() {
        return this.pnClassTypeElements;
    }

    public void setPnClassTypeElements(Set pnClassTypeElements) {
        this.pnClassTypeElements = pnClassTypeElements;
    }

    public Set getPnElementDisplayClasses() {
        return this.pnElementDisplayClasses;
    }

    public void setPnElementDisplayClasses(Set pnElementDisplayClasses) {
        this.pnElementDisplayClasses = pnElementDisplayClasses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("elementId", getElementId())
            .toString();
    }

}
