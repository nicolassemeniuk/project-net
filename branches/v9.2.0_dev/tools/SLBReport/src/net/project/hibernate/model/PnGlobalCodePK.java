package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGlobalCodePK implements Serializable {

    /** identifier field */
    private Integer code;

    /** identifier field */
    private BigDecimal codeTypeId;

    /** full constructor */
    public PnGlobalCodePK(Integer code, BigDecimal codeTypeId) {
        this.code = code;
        this.codeTypeId = codeTypeId;
    }

    /** default constructor */
    public PnGlobalCodePK() {
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public BigDecimal getCodeTypeId() {
        return this.codeTypeId;
    }

    public void setCodeTypeId(BigDecimal codeTypeId) {
        this.codeTypeId = codeTypeId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("code", getCode())
            .append("codeTypeId", getCodeTypeId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGlobalCodePK) ) return false;
        PnGlobalCodePK castOther = (PnGlobalCodePK) other;
        return new EqualsBuilder()
            .append(this.getCode(), castOther.getCode())
            .append(this.getCodeTypeId(), castOther.getCodeTypeId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getCode())
            .append(getCodeTypeId())
            .toHashCode();
    }

}
