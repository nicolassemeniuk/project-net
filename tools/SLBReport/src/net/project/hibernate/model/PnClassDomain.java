package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassDomain implements Serializable {

    /** identifier field */
    private BigDecimal domainId;

    /** nullable persistent field */
    private String domainName;

    /** nullable persistent field */
    private String domainType;

    /** nullable persistent field */
    private String domainDesc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private Set pnClassDomainValues;

    /** persistent field */
    private Set pnClassFields;

    /** full constructor */
    public PnClassDomain(BigDecimal domainId, String domainName, String domainType, String domainDesc, String recordStatus, net.project.hibernate.model.PnObject pnObject, Set pnClassDomainValues, Set pnClassFields) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.domainType = domainType;
        this.domainDesc = domainDesc;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnClassDomainValues = pnClassDomainValues;
        this.pnClassFields = pnClassFields;
    }

    /** default constructor */
    public PnClassDomain() {
    }

    /** minimal constructor */
    public PnClassDomain(BigDecimal domainId, String recordStatus, Set pnClassDomainValues, Set pnClassFields) {
        this.domainId = domainId;
        this.recordStatus = recordStatus;
        this.pnClassDomainValues = pnClassDomainValues;
        this.pnClassFields = pnClassFields;
    }

    public BigDecimal getDomainId() {
        return this.domainId;
    }

    public void setDomainId(BigDecimal domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return this.domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainType() {
        return this.domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDomainDesc() {
        return this.domainDesc;
    }

    public void setDomainDesc(String domainDesc) {
        this.domainDesc = domainDesc;
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

    public Set getPnClassDomainValues() {
        return this.pnClassDomainValues;
    }

    public void setPnClassDomainValues(Set pnClassDomainValues) {
        this.pnClassDomainValues = pnClassDomainValues;
    }

    public Set getPnClassFields() {
        return this.pnClassFields;
    }

    public void setPnClassFields(Set pnClassFields) {
        this.pnClassFields = pnClassFields;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("domainId", getDomainId())
            .toString();
    }

}
