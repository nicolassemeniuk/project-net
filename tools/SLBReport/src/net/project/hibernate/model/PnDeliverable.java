package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDeliverable implements Serializable {

    /** identifier field */
    private BigDecimal deliverableId;

    /** persistent field */
    private String deliverableName;

    /** nullable persistent field */
    private String deliverableDesc;

    /** persistent field */
    private int statusId;

    /** nullable persistent field */
    private BigDecimal methodologyDeliverableId;

    /** persistent field */
    private int isOptional;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Clob deliverableCommentsClob;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private Set pnPhaseHasDeliverables;

    /** full constructor */
    public PnDeliverable(BigDecimal deliverableId, String deliverableName, String deliverableDesc, int statusId, BigDecimal methodologyDeliverableId, int isOptional, String recordStatus, Clob deliverableCommentsClob, net.project.hibernate.model.PnObject pnObject, Set pnPhaseHasDeliverables) {
        this.deliverableId = deliverableId;
        this.deliverableName = deliverableName;
        this.deliverableDesc = deliverableDesc;
        this.statusId = statusId;
        this.methodologyDeliverableId = methodologyDeliverableId;
        this.isOptional = isOptional;
        this.recordStatus = recordStatus;
        this.deliverableCommentsClob = deliverableCommentsClob;
        this.pnObject = pnObject;
        this.pnPhaseHasDeliverables = pnPhaseHasDeliverables;
    }

    /** default constructor */
    public PnDeliverable() {
    }

    /** minimal constructor */
    public PnDeliverable(BigDecimal deliverableId, String deliverableName, int statusId, int isOptional, String recordStatus, Set pnPhaseHasDeliverables) {
        this.deliverableId = deliverableId;
        this.deliverableName = deliverableName;
        this.statusId = statusId;
        this.isOptional = isOptional;
        this.recordStatus = recordStatus;
        this.pnPhaseHasDeliverables = pnPhaseHasDeliverables;
    }

    public BigDecimal getDeliverableId() {
        return this.deliverableId;
    }

    public void setDeliverableId(BigDecimal deliverableId) {
        this.deliverableId = deliverableId;
    }

    public String getDeliverableName() {
        return this.deliverableName;
    }

    public void setDeliverableName(String deliverableName) {
        this.deliverableName = deliverableName;
    }

    public String getDeliverableDesc() {
        return this.deliverableDesc;
    }

    public void setDeliverableDesc(String deliverableDesc) {
        this.deliverableDesc = deliverableDesc;
    }

    public int getStatusId() {
        return this.statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public BigDecimal getMethodologyDeliverableId() {
        return this.methodologyDeliverableId;
    }

    public void setMethodologyDeliverableId(BigDecimal methodologyDeliverableId) {
        this.methodologyDeliverableId = methodologyDeliverableId;
    }

    public int getIsOptional() {
        return this.isOptional;
    }

    public void setIsOptional(int isOptional) {
        this.isOptional = isOptional;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Clob getDeliverableCommentsClob() {
        return this.deliverableCommentsClob;
    }

    public void setDeliverableCommentsClob(Clob deliverableCommentsClob) {
        this.deliverableCommentsClob = deliverableCommentsClob;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public Set getPnPhaseHasDeliverables() {
        return this.pnPhaseHasDeliverables;
    }

    public void setPnPhaseHasDeliverables(Set pnPhaseHasDeliverables) {
        this.pnPhaseHasDeliverables = pnPhaseHasDeliverables;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("deliverableId", getDeliverableId())
            .toString();
    }

}
