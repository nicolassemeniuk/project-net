package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasMethodology implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasMethodologyPK comp_id;

    /** nullable persistent field */
    private BigDecimal personId;

    /** nullable persistent field */
    private Date dateApplied;

    /** nullable persistent field */
    private net.project.hibernate.model.PnMethodologySpace pnMethodologySpace;

    /** full constructor */
    public PnSpaceHasMethodology(net.project.hibernate.model.PnSpaceHasMethodologyPK comp_id, BigDecimal personId, Date dateApplied, net.project.hibernate.model.PnMethodologySpace pnMethodologySpace) {
        this.comp_id = comp_id;
        this.personId = personId;
        this.dateApplied = dateApplied;
        this.pnMethodologySpace = pnMethodologySpace;
    }

    /** default constructor */
    public PnSpaceHasMethodology() {
    }

    /** minimal constructor */
    public PnSpaceHasMethodology(net.project.hibernate.model.PnSpaceHasMethodologyPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasMethodologyPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasMethodologyPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Date getDateApplied() {
        return this.dateApplied;
    }

    public void setDateApplied(Date dateApplied) {
        this.dateApplied = dateApplied;
    }

    public net.project.hibernate.model.PnMethodologySpace getPnMethodologySpace() {
        return this.pnMethodologySpace;
    }

    public void setPnMethodologySpace(net.project.hibernate.model.PnMethodologySpace pnMethodologySpace) {
        this.pnMethodologySpace = pnMethodologySpace;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasMethodology) ) return false;
        PnSpaceHasMethodology castOther = (PnSpaceHasMethodology) other;
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
