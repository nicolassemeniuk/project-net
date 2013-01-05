package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGlobalCode implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnGlobalCodePK comp_id;

    /** persistent field */
    private String codeName;

    /** nullable persistent field */
    private String codeDesc;

    /** nullable persistent field */
    private String codeUrl;

    /** nullable persistent field */
    private Integer presentationSequence;

    /** persistent field */
    private int isDefault;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCodeType pnCodeType;

    /** full constructor */
    public PnGlobalCode(net.project.hibernate.model.PnGlobalCodePK comp_id, String codeName, String codeDesc, String codeUrl, Integer presentationSequence, int isDefault, String recordStatus, net.project.hibernate.model.PnCodeType pnCodeType) {
        this.comp_id = comp_id;
        this.codeName = codeName;
        this.codeDesc = codeDesc;
        this.codeUrl = codeUrl;
        this.presentationSequence = presentationSequence;
        this.isDefault = isDefault;
        this.recordStatus = recordStatus;
        this.pnCodeType = pnCodeType;
    }

    /** default constructor */
    public PnGlobalCode() {
    }

    /** minimal constructor */
    public PnGlobalCode(net.project.hibernate.model.PnGlobalCodePK comp_id, String codeName, int isDefault, String recordStatus) {
        this.comp_id = comp_id;
        this.codeName = codeName;
        this.isDefault = isDefault;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnGlobalCodePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnGlobalCodePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeDesc() {
        return this.codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public String getCodeUrl() {
        return this.codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public Integer getPresentationSequence() {
        return this.presentationSequence;
    }

    public void setPresentationSequence(Integer presentationSequence) {
        this.presentationSequence = presentationSequence;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnCodeType getPnCodeType() {
        return this.pnCodeType;
    }

    public void setPnCodeType(net.project.hibernate.model.PnCodeType pnCodeType) {
        this.pnCodeType = pnCodeType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGlobalCode) ) return false;
        PnGlobalCode castOther = (PnGlobalCode) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
