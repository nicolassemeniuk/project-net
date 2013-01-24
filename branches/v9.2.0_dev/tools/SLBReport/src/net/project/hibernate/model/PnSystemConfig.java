package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSystemConfig implements Serializable {

    /** identifier field */
    private BigDecimal systemSpaceId;

    /** full constructor */
    public PnSystemConfig(BigDecimal systemSpaceId) {
        this.systemSpaceId = systemSpaceId;
    }

    /** default constructor */
    public PnSystemConfig() {
    }

    public BigDecimal getSystemSpaceId() {
        return this.systemSpaceId;
    }

    public void setSystemSpaceId(BigDecimal systemSpaceId) {
        this.systemSpaceId = systemSpaceId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("systemSpaceId", getSystemSpaceId())
            .toString();
    }

}
