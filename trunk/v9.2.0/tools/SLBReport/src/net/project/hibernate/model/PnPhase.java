package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPhase implements Serializable {

    /** identifier field */
    private BigDecimal phaseId;

    /** persistent field */
    private String phaseName;

    /** nullable persistent field */
    private String phaseDesc;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date endDate;

    /** persistent field */
    private BigDecimal sequence;

    /** persistent field */
    private BigDecimal statusId;

    /** nullable persistent field */
    private BigDecimal enteredPercentComplete;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private String progressReportingMethod;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnProcess pnProcess;

    /** persistent field */
    private Set pnGates;

    /** persistent field */
    private Set pnProcesses;

    /** persistent field */
    private Set pnPhaseHasTasks;

    /** persistent field */
    private Set pnPhaseHasDeliverables;

    /** full constructor */
    public PnPhase(BigDecimal phaseId, String phaseName, String phaseDesc, Date startDate, Date endDate, BigDecimal sequence, BigDecimal statusId, BigDecimal enteredPercentComplete, String recordStatus, String progressReportingMethod, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnProcess pnProcess, Set pnGates, Set pnProcesses, Set pnPhaseHasTasks, Set pnPhaseHasDeliverables) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.phaseDesc = phaseDesc;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sequence = sequence;
        this.statusId = statusId;
        this.enteredPercentComplete = enteredPercentComplete;
        this.recordStatus = recordStatus;
        this.progressReportingMethod = progressReportingMethod;
        this.pnObject = pnObject;
        this.pnProcess = pnProcess;
        this.pnGates = pnGates;
        this.pnProcesses = pnProcesses;
        this.pnPhaseHasTasks = pnPhaseHasTasks;
        this.pnPhaseHasDeliverables = pnPhaseHasDeliverables;
    }

    /** default constructor */
    public PnPhase() {
    }

    /** minimal constructor */
    public PnPhase(BigDecimal phaseId, String phaseName, BigDecimal sequence, BigDecimal statusId, String recordStatus, String progressReportingMethod, net.project.hibernate.model.PnProcess pnProcess, Set pnGates, Set pnProcesses, Set pnPhaseHasTasks, Set pnPhaseHasDeliverables) {
        this.phaseId = phaseId;
        this.phaseName = phaseName;
        this.sequence = sequence;
        this.statusId = statusId;
        this.recordStatus = recordStatus;
        this.progressReportingMethod = progressReportingMethod;
        this.pnProcess = pnProcess;
        this.pnGates = pnGates;
        this.pnProcesses = pnProcesses;
        this.pnPhaseHasTasks = pnPhaseHasTasks;
        this.pnPhaseHasDeliverables = pnPhaseHasDeliverables;
    }

    public BigDecimal getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(BigDecimal phaseId) {
        this.phaseId = phaseId;
    }

    public String getPhaseName() {
        return this.phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getPhaseDesc() {
        return this.phaseDesc;
    }

    public void setPhaseDesc(String phaseDesc) {
        this.phaseDesc = phaseDesc;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getSequence() {
        return this.sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getEnteredPercentComplete() {
        return this.enteredPercentComplete;
    }

    public void setEnteredPercentComplete(BigDecimal enteredPercentComplete) {
        this.enteredPercentComplete = enteredPercentComplete;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getProgressReportingMethod() {
        return this.progressReportingMethod;
    }

    public void setProgressReportingMethod(String progressReportingMethod) {
        this.progressReportingMethod = progressReportingMethod;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnProcess getPnProcess() {
        return this.pnProcess;
    }

    public void setPnProcess(net.project.hibernate.model.PnProcess pnProcess) {
        this.pnProcess = pnProcess;
    }

    public Set getPnGates() {
        return this.pnGates;
    }

    public void setPnGates(Set pnGates) {
        this.pnGates = pnGates;
    }

    public Set getPnProcesses() {
        return this.pnProcesses;
    }

    public void setPnProcesses(Set pnProcesses) {
        this.pnProcesses = pnProcesses;
    }

    public Set getPnPhaseHasTasks() {
        return this.pnPhaseHasTasks;
    }

    public void setPnPhaseHasTasks(Set pnPhaseHasTasks) {
        this.pnPhaseHasTasks = pnPhaseHasTasks;
    }

    public Set getPnPhaseHasDeliverables() {
        return this.pnPhaseHasDeliverables;
    }

    public void setPnPhaseHasDeliverables(Set pnPhaseHasDeliverables) {
        this.pnPhaseHasDeliverables = pnPhaseHasDeliverables;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("phaseId", getPhaseId())
            .toString();
    }

}
