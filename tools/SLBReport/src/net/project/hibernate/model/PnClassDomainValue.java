package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassDomainValue implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassDomainValuePK comp_id;

    /** persistent field */
    private String domainValueName;

    /** nullable persistent field */
    private Integer domainValueSeq;

    /** nullable persistent field */
    private String domainValueDesc;

    /** persistent field */
    private int isDefault;

    /** nullable persistent field */
    private BigDecimal sourceValueId;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClassDomain pnClassDomain;

    /** full constructor */
    public PnClassDomainValue(net.project.hibernate.model.PnClassDomainValuePK comp_id, String domainValueName, Integer domainValueSeq, String domainValueDesc, int isDefault, BigDecimal sourceValueId, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnClassDomain pnClassDomain) {
        this.comp_id = comp_id;
        this.domainValueName = domainValueName;
        this.domainValueSeq = domainValueSeq;
        this.domainValueDesc = domainValueDesc;
        this.isDefault = isDefault;
        this.sourceValueId = sourceValueId;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnClassDomain = pnClassDomain;
    }

    /** default constructor */
    public PnClassDomainValue() {
    }

    /** minimal constructor */
    public PnClassDomainValue(net.project.hibernate.model.PnClassDomainValuePK comp_id, String domainValueName, int isDefault, String recordStatus) {
        this.comp_id = comp_id;
        this.domainValueName = domainValueName;
        this.isDefault = isDefault;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnClassDomainValuePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassDomainValuePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDomainValueName() {
        return this.domainValueName;
    }

    public void setDomainValueName(String domainValueName) {
        this.domainValueName = domainValueName;
    }

    public Integer getDomainValueSeq() {
        return this.domainValueSeq;
    }

    public void setDomainValueSeq(Integer domainValueSeq) {
        this.domainValueSeq = domainValueSeq;
    }

    public String getDomainValueDesc() {
        return this.domainValueDesc;
    }

    public void setDomainValueDesc(String domainValueDesc) {
        this.domainValueDesc = domainValueDesc;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public BigDecimal getSourceValueId() {
        return this.sourceValueId;
    }

    public void setSourceValueId(BigDecimal sourceValueId) {
        this.sourceValueId = sourceValueId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnClassDomain getPnClassDomain() {
        return this.pnClassDomain;
    }

    public void setPnClassDomain(net.project.hibernate.model.PnClassDomain pnClassDomain) {
        this.pnClassDomain = pnClassDomain;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassDomainValue) ) return false;
        PnClassDomainValue castOther = (PnClassDomainValue) other;
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
