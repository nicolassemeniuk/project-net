package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocType implements Serializable {

    /** identifier field */
    private BigDecimal docTypeId;

    /** nullable persistent field */
    private BigDecimal propertySheetClassId;

    /** nullable persistent field */
    private String typeName;

    /** nullable persistent field */
    private String typeDescription;

    /** nullable persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnDocuments;

    /** full constructor */
    public PnDocType(BigDecimal docTypeId, BigDecimal propertySheetClassId, String typeName, String typeDescription, String recordStatus, Set pnDocuments) {
        this.docTypeId = docTypeId;
        this.propertySheetClassId = propertySheetClassId;
        this.typeName = typeName;
        this.typeDescription = typeDescription;
        this.recordStatus = recordStatus;
        this.pnDocuments = pnDocuments;
    }

    /** default constructor */
    public PnDocType() {
    }

    /** minimal constructor */
    public PnDocType(BigDecimal docTypeId, Set pnDocuments) {
        this.docTypeId = docTypeId;
        this.pnDocuments = pnDocuments;
    }

    public BigDecimal getDocTypeId() {
        return this.docTypeId;
    }

    public void setDocTypeId(BigDecimal docTypeId) {
        this.docTypeId = docTypeId;
    }

    public BigDecimal getPropertySheetClassId() {
        return this.propertySheetClassId;
    }

    public void setPropertySheetClassId(BigDecimal propertySheetClassId) {
        this.propertySheetClassId = propertySheetClassId;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDescription() {
        return this.typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnDocuments() {
        return this.pnDocuments;
    }

    public void setPnDocuments(Set pnDocuments) {
        this.pnDocuments = pnDocuments;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docTypeId", getDocTypeId())
            .toString();
    }

}
