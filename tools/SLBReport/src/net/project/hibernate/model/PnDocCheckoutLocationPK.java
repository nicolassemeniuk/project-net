package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocCheckoutLocationPK implements Serializable {

    /** identifier field */
    private BigDecimal docId;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal clientMachineId;

    /** full constructor */
    public PnDocCheckoutLocationPK(BigDecimal docId, BigDecimal personId, BigDecimal clientMachineId) {
        this.docId = docId;
        this.personId = personId;
        this.clientMachineId = clientMachineId;
    }

    /** default constructor */
    public PnDocCheckoutLocationPK() {
    }

    public BigDecimal getDocId() {
        return this.docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getClientMachineId() {
        return this.clientMachineId;
    }

    public void setClientMachineId(BigDecimal clientMachineId) {
        this.clientMachineId = clientMachineId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docId", getDocId())
            .append("personId", getPersonId())
            .append("clientMachineId", getClientMachineId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocCheckoutLocationPK) ) return false;
        PnDocCheckoutLocationPK castOther = (PnDocCheckoutLocationPK) other;
        return new EqualsBuilder()
            .append(this.getDocId(), castOther.getDocId())
            .append(this.getPersonId(), castOther.getPersonId())
            .append(this.getClientMachineId(), castOther.getClientMachineId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getDocId())
            .append(getPersonId())
            .append(getClientMachineId())
            .toHashCode();
    }

}
