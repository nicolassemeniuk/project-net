package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassListFilter implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassListFilterPK comp_id;

    /** persistent field */
    private String operator;

    /** persistent field */
    private String filterValue;

    /** full constructor */
    public PnClassListFilter(net.project.hibernate.model.PnClassListFilterPK comp_id, String operator, String filterValue) {
        this.comp_id = comp_id;
        this.operator = operator;
        this.filterValue = filterValue;
    }

    /** default constructor */
    public PnClassListFilter() {
    }

    public net.project.hibernate.model.PnClassListFilterPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassListFilterPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFilterValue() {
        return this.filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassListFilter) ) return false;
        PnClassListFilter castOther = (PnClassListFilter) other;
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
