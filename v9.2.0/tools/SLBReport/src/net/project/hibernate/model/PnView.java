package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnView implements Serializable {

    /** identifier field */
    private BigDecimal viewId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** persistent field */
    private BigDecimal modifiedById;

    /** persistent field */
    private Date modifiedDatetime;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnFinderIngredient pnFinderIngredient;

    /** persistent field */
    private Set pnSpaceViewContexts;

    /** persistent field */
    private Set pnViewDefaultSettings;

    /** full constructor */
    public PnView(BigDecimal viewId, String name, String description, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, String recordStatus, net.project.hibernate.model.PnFinderIngredient pnFinderIngredient, Set pnSpaceViewContexts, Set pnViewDefaultSettings) {
        this.viewId = viewId;
        this.name = name;
        this.description = description;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.recordStatus = recordStatus;
        this.pnFinderIngredient = pnFinderIngredient;
        this.pnSpaceViewContexts = pnSpaceViewContexts;
        this.pnViewDefaultSettings = pnViewDefaultSettings;
    }

    /** default constructor */
    public PnView() {
    }

    /** minimal constructor */
    public PnView(BigDecimal viewId, String name, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, String recordStatus, net.project.hibernate.model.PnFinderIngredient pnFinderIngredient, Set pnSpaceViewContexts, Set pnViewDefaultSettings) {
        this.viewId = viewId;
        this.name = name;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.recordStatus = recordStatus;
        this.pnFinderIngredient = pnFinderIngredient;
        this.pnSpaceViewContexts = pnSpaceViewContexts;
        this.pnViewDefaultSettings = pnViewDefaultSettings;
    }

    public BigDecimal getViewId() {
        return this.viewId;
    }

    public void setViewId(BigDecimal viewId) {
        this.viewId = viewId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnFinderIngredient getPnFinderIngredient() {
        return this.pnFinderIngredient;
    }

    public void setPnFinderIngredient(net.project.hibernate.model.PnFinderIngredient pnFinderIngredient) {
        this.pnFinderIngredient = pnFinderIngredient;
    }

    public Set getPnSpaceViewContexts() {
        return this.pnSpaceViewContexts;
    }

    public void setPnSpaceViewContexts(Set pnSpaceViewContexts) {
        this.pnSpaceViewContexts = pnSpaceViewContexts;
    }

    public Set getPnViewDefaultSettings() {
        return this.pnViewDefaultSettings;
    }

    public void setPnViewDefaultSettings(Set pnViewDefaultSettings) {
        this.pnViewDefaultSettings = pnViewDefaultSettings;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("viewId", getViewId())
            .toString();
    }

}
