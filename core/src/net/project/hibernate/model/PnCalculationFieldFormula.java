package net.project.hibernate.model;
// Generated Jun 13, 2009 11:41:49 PM by Hibernate Tools 3.2.4.GA


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnCalculationFieldFormula generated by hbm2java
 */
@Entity
@Table(name="PN_CALCULATION_FIELD_FORMULA"
)
public class PnCalculationFieldFormula  implements java.io.Serializable {


     private PnCalculationFieldFormulaPK comp_id;
     private String opValue;
     private String opType;

    public PnCalculationFieldFormula() {
    }

	
    public PnCalculationFieldFormula(PnCalculationFieldFormulaPK comp_id) {
        this.comp_id = comp_id;
    }
    public PnCalculationFieldFormula(PnCalculationFieldFormulaPK comp_id, String opValue, String opType) {
       this.comp_id = comp_id;
       this.opValue = opValue;
       this.opType = opType;
    }
   
     @EmbeddedId

    
    @AttributeOverrides( {
        @AttributeOverride(name="classId", column=@Column(name="CLASS_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="fieldId", column=@Column(name="FIELD_ID", nullable=false, length=20) ), 
        @AttributeOverride(name="orderId", column=@Column(name="ORDER_ID", nullable=false, length=20) ) } )
    public PnCalculationFieldFormulaPK getComp_id() {
        return this.comp_id;
    }
    
    public void setComp_id(PnCalculationFieldFormulaPK comp_id) {
        this.comp_id = comp_id;
    }

    
    @Column(name="OP_VALUE", length=80)
    public String getOpValue() {
        return this.opValue;
    }
    
    public void setOpValue(String opValue) {
        this.opValue = opValue;
    }

    
    @Column(name="OP_TYPE", length=80)
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


