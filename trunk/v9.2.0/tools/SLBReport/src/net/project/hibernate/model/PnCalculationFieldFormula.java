package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCalculationFieldFormula implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnCalculationFieldFormulaPK comp_id;

    /** nullable persistent field */
    private String opValue;

    /** nullable persistent field */
    private String opType;

    /** full constructor */
    public PnCalculationFieldFormula(net.project.hibernate.model.PnCalculationFieldFormulaPK comp_id, String opValue, String opType) {
        this.comp_id = comp_id;
        this.opValue = opValue;
        this.opType = opType;
    }

    /** default constructor */
    public PnCalculationFieldFormula() {
    }

    /** minimal constructor */
    public PnCalculationFieldFormula(net.project.hibernate.model.PnCalculationFieldFormulaPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnCalculationFieldFormulaPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnCalculationFieldFormulaPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getOpValue() {
        return this.opValue;
    }

    public void setOpValue(String opValue) {
        this.opValue = opValue;
    }

    public String getOpType() {
        return this.opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnCalculationFieldFormula) ) return false;
        PnCalculationFieldFormula castOther = (PnCalculationFieldFormula) other;
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
