package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDirectoryHasPersonPK implements Serializable {

    /** identifier field */
    private BigDecimal directoryId;

    /** identifier field */
    private BigDecimal personId;

    /** full constructor */
    public PnDirectoryHasPersonPK(BigDecimal directoryId, BigDecimal personId) {
        this.directoryId = directoryId;
        this.personId = personId;
    }

    /** default constructor */
    public PnDirectoryHasPersonPK() {
    }

    public BigDecimal getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(BigDecimal directoryId) {
        this.directoryId = directoryId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("directoryId", getDirectoryId())
            .append("personId", getPersonId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDirectoryHasPersonPK) ) return false;
        PnDirectoryHasPersonPK castOther = (PnDirectoryHasPersonPK) other;
        return new EqualsBuilder()
            .append(this.getDirectoryId(), castOther.getDirectoryId())
            .append(this.getPersonId(), castOther.getPersonId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDirectoryId())
            .append(getPersonId())
            .toHashCode();
    }

}
