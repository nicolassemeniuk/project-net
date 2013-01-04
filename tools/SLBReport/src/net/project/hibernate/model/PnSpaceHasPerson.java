package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasPerson implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasPersonPK comp_id;

    /** nullable persistent field */
    private String relationshipPersonToSpace;

    /** nullable persistent field */
    private BigDecimal memberTypeId;

    /** nullable persistent field */
    private String relationshipSpaceToPerson;

    /** nullable persistent field */
    private String responsibilities;

    /** nullable persistent field */
    private String memberTitle;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private Set pnAssignments;

    /** full constructor */
    public PnSpaceHasPerson(net.project.hibernate.model.PnSpaceHasPersonPK comp_id, String relationshipPersonToSpace, BigDecimal memberTypeId, String relationshipSpaceToPerson, String responsibilities, String memberTitle, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, Set pnAssignments) {
        this.comp_id = comp_id;
        this.relationshipPersonToSpace = relationshipPersonToSpace;
        this.memberTypeId = memberTypeId;
        this.relationshipSpaceToPerson = relationshipSpaceToPerson;
        this.responsibilities = responsibilities;
        this.memberTitle = memberTitle;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnAssignments = pnAssignments;
    }

    /** default constructor */
    public PnSpaceHasPerson() {
    }

    /** minimal constructor */
    public PnSpaceHasPerson(net.project.hibernate.model.PnSpaceHasPersonPK comp_id, String recordStatus, Set pnAssignments) {
        this.comp_id = comp_id;
        this.recordStatus = recordStatus;
        this.pnAssignments = pnAssignments;
    }
    
    public PnSpaceHasPerson(net.project.hibernate.model.PnSpaceHasPersonPK comp_id, String relationshipPersonToSpace, String recordStatus) {
        this.comp_id = comp_id;
        this.relationshipPersonToSpace = relationshipPersonToSpace;
        this.recordStatus = recordStatus;
    }   

    public net.project.hibernate.model.PnSpaceHasPersonPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasPersonPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getRelationshipPersonToSpace() {
        return this.relationshipPersonToSpace;
    }

    public void setRelationshipPersonToSpace(String relationshipPersonToSpace) {
        this.relationshipPersonToSpace = relationshipPersonToSpace;
    }

    public BigDecimal getMemberTypeId() {
        return this.memberTypeId;
    }

    public void setMemberTypeId(BigDecimal memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getRelationshipSpaceToPerson() {
        return this.relationshipSpaceToPerson;
    }

    public void setRelationshipSpaceToPerson(String relationshipSpaceToPerson) {
        this.relationshipSpaceToPerson = relationshipSpaceToPerson;
    }

    public String getResponsibilities() {
        return this.responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getMemberTitle() {
        return this.memberTitle;
    }

    public void setMemberTitle(String memberTitle) {
        this.memberTitle = memberTitle;
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

    public Set getPnAssignments() {
        return this.pnAssignments;
    }

    public void setPnAssignments(Set pnAssignments) {
        this.pnAssignments = pnAssignments;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasPerson) ) return false;
        PnSpaceHasPerson castOther = (PnSpaceHasPerson) other;
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
