package net.project.hibernate.model;

import java.io.Serializable;

import java.sql.Clob;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnMethodologyView implements Serializable {

	private PnMethodologyViewPK comp_id;

    /** identifier field */
    private String methodologyName;

    /** identifier field */
    private String methodologyDesc;

    /** identifier field */
    private Clob useScenarioClob;

    /** identifier field */
    private Integer statusId;

    /** identifier field */
    private Integer createdById;

    /** identifier field */
    private String createdBy;

    /** identifier field */
    private Date createdDate;

    /** identifier field */
    private Integer modifiedById;

    /** identifier field */
    private Date modifiedDate;

    /** identifier field */
    private String modifiedBy;

    /** identifier field */
    private String recordStatus;

    /** identifier field */
    private Integer isGlobal;

    /** identifier field */
    private Date crc;

    /** full constructor */
    public PnMethodologyView(PnMethodologyViewPK comp_id, String methodologyName, String methodologyDesc, Clob useScenarioClob, Integer statusId, Integer createdById, String createdBy, Date createdDate, Integer modifiedById, Date modifiedDate, String modifiedBy, String recordStatus, Integer isGlobal, Date crc) {
    	this.comp_id = comp_id;
    	this.methodologyName = methodologyName;
        this.methodologyDesc = methodologyDesc;
        this.useScenarioClob = useScenarioClob;
        this.statusId = statusId;
        this.createdById = createdById;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.modifiedById = modifiedById;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.recordStatus = recordStatus;
        this.isGlobal = isGlobal;
        this.crc = crc;
    }

    /** default constructor */
    public PnMethodologyView() {
    }

    public PnMethodologyViewPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnMethodologyViewPK comp_id) {
		this.comp_id = comp_id;
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

    public Clob getUseScenarioClob() {
        return this.useScenarioClob;
    }

    public void setUseScenarioClob(Clob useScenarioClob) {
        this.useScenarioClob = useScenarioClob;
    }

    public Integer getStatusId() {
        return this.statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(Integer modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Integer getIsGlobal() {
        return this.isGlobal;
    }

    public void setIsGlobal(Integer isGlobal) {
        this.isGlobal = isGlobal;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .append("methodologyName", getMethodologyName())
            .append("methodologyDesc", getMethodologyDesc())
            .append("useScenarioClob", getUseScenarioClob())
            .append("statusId", getStatusId())
            .append("createdById", getCreatedById())
            .append("createdBy", getCreatedBy())
            .append("createdDate", getCreatedDate())
            .append("modifiedById", getModifiedById())
            .append("modifiedDate", getModifiedDate())
            .append("modifiedBy", getModifiedBy())
            .append("recordStatus", getRecordStatus())
            .append("isGlobal", getIsGlobal())
            .append("crc", getCrc())
            .toString();
    }

}
