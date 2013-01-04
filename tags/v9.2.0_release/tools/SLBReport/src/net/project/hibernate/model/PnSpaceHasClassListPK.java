package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasClassListPK implements Serializable {

    /** identifier field */
    private BigDecimal spaceId;

    /** identifier field */
    private BigDecimal classId;

    /** identifier field */
    private BigDecimal listId;

    /** full constructor */
    public PnSpaceHasClassListPK(BigDecimal spaceId, BigDecimal classId, BigDecimal listId) {
        this.spaceId = spaceId;
        this.classId = classId;
        this.listId = listId;
    }

    /** default constructor */
    public PnSpaceHasClassListPK() {
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public BigDecimal getListId() {
        return this.listId;
    }

    public void setListId(BigDecimal listId) {
        this.listId = listId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceId", getSpaceId())
            .append("classId", getClassId())
            .append("listId", getListId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasClassListPK) ) return false;
        PnSpaceHasClassListPK castOther = (PnSpaceHasClassListPK) other;
        return new EqualsBuilder()
            .append(this.getSpaceId(), castOther.getSpaceId())
            .append(this.getClassId(), castOther.getClassId())
            .append(this.getListId(), castOther.getListId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceId())
            .append(getClassId())
            .append(getListId())
            .toHashCode();
    }

}
