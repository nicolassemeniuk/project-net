package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnShareable implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** persistent field */
    private BigDecimal permissionType;

    /** nullable persistent field */
    private BigDecimal containerId;

    /** nullable persistent field */
    private BigDecimal spaceId;

    /** nullable persistent field */
    private BigDecimal allowableActions;

    /** nullable persistent field */
    private BigDecimal propagateToChildren;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** full constructor */
    public PnShareable(BigDecimal objectId, BigDecimal permissionType, BigDecimal containerId, BigDecimal spaceId, BigDecimal allowableActions, BigDecimal propagateToChildren, net.project.hibernate.model.PnObject pnObject) {
        this.objectId = objectId;
        this.permissionType = permissionType;
        this.containerId = containerId;
        this.spaceId = spaceId;
        this.allowableActions = allowableActions;
        this.propagateToChildren = propagateToChildren;
        this.pnObject = pnObject;
    }

    /** default constructor */
    public PnShareable() {
    }

    /** minimal constructor */
    public PnShareable(BigDecimal objectId, BigDecimal permissionType) {
        this.objectId = objectId;
        this.permissionType = permissionType;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(BigDecimal permissionType) {
        this.permissionType = permissionType;
    }

    public BigDecimal getContainerId() {
        return this.containerId;
    }

    public void setContainerId(BigDecimal containerId) {
        this.containerId = containerId;
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getAllowableActions() {
        return this.allowableActions;
    }

    public void setAllowableActions(BigDecimal allowableActions) {
        this.allowableActions = allowableActions;
    }

    public BigDecimal getPropagateToChildren() {
        return this.propagateToChildren;
    }

    public void setPropagateToChildren(BigDecimal propagateToChildren) {
        this.propagateToChildren = propagateToChildren;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
