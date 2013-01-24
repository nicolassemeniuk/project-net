package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGate implements Serializable {

    /** identifier field */
    private BigDecimal gateId;

    /** persistent field */
    private String gateName;

    /** nullable persistent field */
    private String gateDesc;

    /** nullable persistent field */
    private Date gateDate;

    /** persistent field */
    private BigDecimal statusId;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnPhase pnPhase;

    /** persistent field */
    private Set pnProcesses;

    /** full constructor */
    public PnGate(BigDecimal gateId, String gateName, String gateDesc, Date gateDate, BigDecimal statusId, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnPhase pnPhase, Set pnProcesses) {
        this.gateId = gateId;
        this.gateName = gateName;
        this.gateDesc = gateDesc;
        this.gateDate = gateDate;
        this.statusId = statusId;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnPhase = pnPhase;
        this.pnProcesses = pnProcesses;
    }

    /** default constructor */
    public PnGate() {
    }

    /** minimal constructor */
    public PnGate(BigDecimal gateId, String gateName, BigDecimal statusId, String recordStatus, net.project.hibernate.model.PnPhase pnPhase, Set pnProcesses) {
        this.gateId = gateId;
        this.gateName = gateName;
        this.statusId = statusId;
        this.recordStatus = recordStatus;
        this.pnPhase = pnPhase;
        this.pnProcesses = pnProcesses;
    }

    public BigDecimal getGateId() {
        return this.gateId;
    }

    public void setGateId(BigDecimal gateId) {
        this.gateId = gateId;
    }

    public String getGateName() {
        return this.gateName;
    }

    public void setGateName(String gateName) {
        this.gateName = gateName;
    }

    public String getGateDesc() {
        return this.gateDesc;
    }

    public void setGateDesc(String gateDesc) {
        this.gateDesc = gateDesc;
    }

    public Date getGateDate() {
        return this.gateDate;
    }

    public void setGateDate(Date gateDate) {
        this.gateDate = gateDate;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
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

    public net.project.hibernate.model.PnPhase getPnPhase() {
        return this.pnPhase;
    }

    public void setPnPhase(net.project.hibernate.model.PnPhase pnPhase) {
        this.pnPhase = pnPhase;
    }

    public Set getPnProcesses() {
        return this.pnProcesses;
    }

    public void setPnProcesses(Set pnProcesses) {
        this.pnProcesses = pnProcesses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("gateId", getGateId())
            .toString();
    }

}
