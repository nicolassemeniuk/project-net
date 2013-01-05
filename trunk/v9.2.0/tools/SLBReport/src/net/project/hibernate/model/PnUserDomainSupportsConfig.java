package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserDomainSupportsConfig implements Serializable {

    /** identifier field */
    private BigDecimal domainId;

    /** identifier field */
    private BigDecimal configurationId;

    /** full constructor */
    public PnUserDomainSupportsConfig(BigDecimal domainId, BigDecimal configurationId) {
        this.domainId = domainId;
        this.configurationId = configurationId;
    }

    /** default constructor */
    public PnUserDomainSupportsConfig() {
    }

    public BigDecimal getDomainId() {
        return this.domainId;
    }

    public void setDomainId(BigDecimal domainId) {
        this.domainId = domainId;
    }

    public BigDecimal getConfigurationId() {
        return this.configurationId;
    }

    public void setConfigurationId(BigDecimal configurationId) {
        this.configurationId = configurationId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("domainId", getDomainId())
            .append("configurationId", getConfigurationId())
            .toString();
    }

}
