package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocConfiguration implements Serializable {

    /** identifier field */
    private BigDecimal docConfigurationId;

    /** persistent field */
    private Date dateFrozen;

    /** persistent field */
    private String docConfigurationName;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private Set pnDocConfigurationHasDocs;

    /** full constructor */
    public PnDocConfiguration(BigDecimal docConfigurationId, Date dateFrozen, String docConfigurationName, Date crc, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, Set pnDocConfigurationHasDocs) {
        this.docConfigurationId = docConfigurationId;
        this.dateFrozen = dateFrozen;
        this.docConfigurationName = docConfigurationName;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnDocConfigurationHasDocs = pnDocConfigurationHasDocs;
    }

    /** default constructor */
    public PnDocConfiguration() {
    }

    public BigDecimal getDocConfigurationId() {
        return this.docConfigurationId;
    }

    public void setDocConfigurationId(BigDecimal docConfigurationId) {
        this.docConfigurationId = docConfigurationId;
    }

    public Date getDateFrozen() {
        return this.dateFrozen;
    }

    public void setDateFrozen(Date dateFrozen) {
        this.dateFrozen = dateFrozen;
    }

    public String getDocConfigurationName() {
        return this.docConfigurationName;
    }

    public void setDocConfigurationName(String docConfigurationName) {
        this.docConfigurationName = docConfigurationName;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public Set getPnDocConfigurationHasDocs() {
        return this.pnDocConfigurationHasDocs;
    }

    public void setPnDocConfigurationHasDocs(Set pnDocConfigurationHasDocs) {
        this.pnDocConfigurationHasDocs = pnDocConfigurationHasDocs;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docConfigurationId", getDocConfigurationId())
            .toString();
    }

}
