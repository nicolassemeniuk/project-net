package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnProcess implements Serializable {

    /** identifier field */
    private BigDecimal processId;

    /** persistent field */
    private String processName;

    /** nullable persistent field */
    private String processDesc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnPhase pnPhase;

    /** persistent field */
    private net.project.hibernate.model.PnGate pnGate;

    /** persistent field */
    private Set pnSpaceHasProcesses;

    /** persistent field */
    private Set pnPhases;

    /** full constructor */
    public PnProcess(BigDecimal processId, String processName, String processDesc, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnPhase pnPhase, net.project.hibernate.model.PnGate pnGate, Set pnSpaceHasProcesses, Set pnPhases) {
        this.processId = processId;
        this.processName = processName;
        this.processDesc = processDesc;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnPhase = pnPhase;
        this.pnGate = pnGate;
        this.pnSpaceHasProcesses = pnSpaceHasProcesses;
        this.pnPhases = pnPhases;
    }

    /** default constructor */
    public PnProcess() {
    }

    /** minimal constructor */
    public PnProcess(BigDecimal processId, String processName, String recordStatus, net.project.hibernate.model.PnPhase pnPhase, net.project.hibernate.model.PnGate pnGate, Set pnSpaceHasProcesses, Set pnPhases) {
        this.processId = processId;
        this.processName = processName;
        this.recordStatus = recordStatus;
        this.pnPhase = pnPhase;
        this.pnGate = pnGate;
        this.pnSpaceHasProcesses = pnSpaceHasProcesses;
        this.pnPhases = pnPhases;
    }

    public BigDecimal getProcessId() {
        return this.processId;
    }

    public void setProcessId(BigDecimal processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return this.processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessDesc() {
        return this.processDesc;
    }

    public void setProcessDesc(String processDesc) {
        this.processDesc = processDesc;
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

    public net.project.hibernate.model.PnGate getPnGate() {
        return this.pnGate;
    }

    public void setPnGate(net.project.hibernate.model.PnGate pnGate) {
        this.pnGate = pnGate;
    }

    public Set getPnSpaceHasProcesses() {
        return this.pnSpaceHasProcesses;
    }

    public void setPnSpaceHasProcesses(Set pnSpaceHasProcesses) {
        this.pnSpaceHasProcesses = pnSpaceHasProcesses;
    }

    public Set getPnPhases() {
        return this.pnPhases;
    }

    public void setPnPhases(Set pnPhases) {
        this.pnPhases = pnPhases;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("processId", getProcessId())
            .toString();
    }

}
