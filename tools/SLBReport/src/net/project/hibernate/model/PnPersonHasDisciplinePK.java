package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasDisciplinePK implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private Integer disciplineCode;

    /** full constructor */
    public PnPersonHasDisciplinePK(BigDecimal personId, Integer disciplineCode) {
        this.personId = personId;
        this.disciplineCode = disciplineCode;
    }

    /** default constructor */
    public PnPersonHasDisciplinePK() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Integer getDisciplineCode() {
        return this.disciplineCode;
    }

    public void setDisciplineCode(Integer disciplineCode) {
        this.disciplineCode = disciplineCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("disciplineCode", getDisciplineCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasDisciplinePK) ) return false;
        PnPersonHasDisciplinePK castOther = (PnPersonHasDisciplinePK) other;
        return new EqualsBuilder()
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getDisciplineCode(), castOther.getDisciplineCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getPersonId())
            .append(getDisciplineCode())
            .toHashCode();
    }

}
