package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskDependency implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnTaskDependencyPK comp_id;

    /** nullable persistent field */
    private BigDecimal lag;

    /** nullable persistent field */
    private String lagUnits;

    /** full constructor */
    public PnTaskDependency(net.project.hibernate.model.PnTaskDependencyPK comp_id, BigDecimal lag, String lagUnits) {
        this.comp_id = comp_id;
        this.lag = lag;
        this.lagUnits = lagUnits;
    }

    /** default constructor */
    public PnTaskDependency() {
    }

    /** minimal constructor */
    public PnTaskDependency(net.project.hibernate.model.PnTaskDependencyPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnTaskDependencyPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnTaskDependencyPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getLag() {
        return this.lag;
    }

    public void setLag(BigDecimal lag) {
        this.lag = lag;
    }

    public String getLagUnits() {
        return this.lagUnits;
    }

    public void setLagUnits(String lagUnits) {
        this.lagUnits = lagUnits;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnTaskDependency) ) return false;
        PnTaskDependency castOther = (PnTaskDependency) other;
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
