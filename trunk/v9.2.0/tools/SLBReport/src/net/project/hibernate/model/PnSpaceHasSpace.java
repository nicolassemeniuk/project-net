package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasSpace implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasSpacePK comp_id;

    /** nullable persistent field */
    private BigDecimal createdBy;

    /** nullable persistent field */
    private Date dateCreated;

    /** nullable persistent field */
    private String relationshipParentToChild;

    /** nullable persistent field */
    private String relationshipChildToParent;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private String parentSpaceType;

    /** nullable persistent field */
    private String childSpaceType;

    /** full constructor */
    public PnSpaceHasSpace(net.project.hibernate.model.PnSpaceHasSpacePK comp_id, BigDecimal createdBy, Date dateCreated, String relationshipParentToChild, String relationshipChildToParent, String recordStatus, String parentSpaceType, String childSpaceType) {
        this.comp_id = comp_id;
        this.createdBy = createdBy;
        this.dateCreated = dateCreated;
        this.relationshipParentToChild = relationshipParentToChild;
        this.relationshipChildToParent = relationshipChildToParent;
        this.recordStatus = recordStatus;
        this.parentSpaceType = parentSpaceType;
        this.childSpaceType = childSpaceType;
    }

    /** default constructor */
    public PnSpaceHasSpace() {
    }

    /** minimal constructor */
    public PnSpaceHasSpace(net.project.hibernate.model.PnSpaceHasSpacePK comp_id, String recordStatus) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnSpaceHasSpacePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasSpacePK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(BigDecimal createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getRelationshipParentToChild() {
        return this.relationshipParentToChild;
    }

    public void setRelationshipParentToChild(String relationshipParentToChild) {
        this.relationshipParentToChild = relationshipParentToChild;
    }

    public String getRelationshipChildToParent() {
        return this.relationshipChildToParent;
    }

    public void setRelationshipChildToParent(String relationshipChildToParent) {
        this.relationshipChildToParent = relationshipChildToParent;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getParentSpaceType() {
        return this.parentSpaceType;
    }

    public void setParentSpaceType(String parentSpaceType) {
        this.parentSpaceType = parentSpaceType;
    }

    public String getChildSpaceType() {
        return this.childSpaceType;
    }

    public void setChildSpaceType(String childSpaceType) {
        this.childSpaceType = childSpaceType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasSpace) ) return false;
        PnSpaceHasSpace castOther = (PnSpaceHasSpace) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
