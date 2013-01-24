package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnMethodologySpace implements Serializable {

    /** identifier field */
    private BigDecimal methodologyId;

    /** nullable persistent field */
    private String methodologyName;

    /** nullable persistent field */
    private String methodologyDesc;

    /** nullable persistent field */
    private BigDecimal statusId;

    /** persistent field */
    private BigDecimal createdById;

    /** nullable persistent field */
    private Date createdDate;

    /** persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private Date modifiedDate;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** nullable persistent field */
    private Clob useScenarioClob;

    /** persistent field */
    private int isGlobal;

    /** persistent field */
    private Set pnSpaceHasMethodologies;

    /** full constructor */
    public PnMethodologySpace(BigDecimal methodologyId, String methodologyName, String methodologyDesc, BigDecimal statusId, BigDecimal createdById, Date createdDate, BigDecimal modifiedById, Date modifiedDate, String recordStatus, Date crc, Clob useScenarioClob, int isGlobal, Set pnSpaceHasMethodologies) {
        this.methodologyId = methodologyId;
        this.methodologyName = methodologyName;
        this.methodologyDesc = methodologyDesc;
        this.statusId = statusId;
        this.createdById = createdById;
        this.createdDate = createdDate;
        this.modifiedById = modifiedById;
        this.modifiedDate = modifiedDate;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.useScenarioClob = useScenarioClob;
        this.isGlobal = isGlobal;
        this.pnSpaceHasMethodologies = pnSpaceHasMethodologies;
    }

    /** default constructor */
    public PnMethodologySpace() {
    }

    /** minimal constructor */
    public PnMethodologySpace(BigDecimal methodologyId, BigDecimal createdById, BigDecimal modifiedById, int isGlobal, Set pnSpaceHasMethodologies) {
        this.methodologyId = methodologyId;
        this.createdById = createdById;
        this.modifiedById = modifiedById;
        this.isGlobal = isGlobal;
        this.pnSpaceHasMethodologies = pnSpaceHasMethodologies;
    }

    public BigDecimal getMethodologyId() {
        return this.methodologyId;
    }

    public void setMethodologyId(BigDecimal methodologyId) {
        this.methodologyId = methodologyId;
    }

    public String getMethodologyName() {
        return this.methodologyName;
    }

    public void setMethodologyName(String methodologyName) {
        this.methodologyName = methodologyName;
    }

    public String getMethodologyDesc() {
        return this.methodologyDesc;
    }

    public void setMethodologyDesc(String methodologyDesc) {
        this.methodologyDesc = methodologyDesc;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public Clob getUseScenarioClob() {
        return this.useScenarioClob;
    }

    public void setUseScenarioClob(Clob useScenarioClob) {
        this.useScenarioClob = useScenarioClob;
    }

    public int getIsGlobal() {
        return this.isGlobal;
    }

    public void setIsGlobal(int isGlobal) {
        this.isGlobal = isGlobal;
    }

    public Set getPnSpaceHasMethodologies() {
        return this.pnSpaceHasMethodologies;
    }

    public void setPnSpaceHasMethodologies(Set pnSpaceHasMethodologies) {
        this.pnSpaceHasMethodologies = pnSpaceHasMethodologies;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("methodologyId", getMethodologyId())
            .toString();
    }

}
