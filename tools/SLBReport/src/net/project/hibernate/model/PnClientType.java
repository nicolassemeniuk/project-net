package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClientType implements Serializable {

    /** identifier field */
    private BigDecimal clientTypeId;

    /** persistent field */
    private String clientName;

    /** nullable persistent field */
    private String clientDesc;

    /** persistent field */
    private Set pnElementProperties;

    /** persistent field */
    private Set pnClassFieldProperties;

    /** full constructor */
    public PnClientType(BigDecimal clientTypeId, String clientName, String clientDesc, Set pnElementProperties, Set pnClassFieldProperties) {
        this.clientTypeId = clientTypeId;
        this.clientName = clientName;
        this.clientDesc = clientDesc;
        this.pnElementProperties = pnElementProperties;
        this.pnClassFieldProperties = pnClassFieldProperties;
    }

    /** default constructor */
    public PnClientType() {
    }

    /** minimal constructor */
    public PnClientType(BigDecimal clientTypeId, String clientName, Set pnElementProperties, Set pnClassFieldProperties) {
        this.clientTypeId = clientTypeId;
        this.clientName = clientName;
        this.pnElementProperties = pnElementProperties;
        this.pnClassFieldProperties = pnClassFieldProperties;
    }

    public BigDecimal getClientTypeId() {
        return this.clientTypeId;
    }

    public void setClientTypeId(BigDecimal clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientDesc() {
        return this.clientDesc;
    }

    public void setClientDesc(String clientDesc) {
        this.clientDesc = clientDesc;
    }

    public Set getPnElementProperties() {
        return this.pnElementProperties;
    }

    public void setPnElementProperties(Set pnElementProperties) {
        this.pnElementProperties = pnElementProperties;
    }

    public Set getPnClassFieldProperties() {
        return this.pnClassFieldProperties;
    }

    public void setPnClassFieldProperties(Set pnClassFieldProperties) {
        this.pnClassFieldProperties = pnClassFieldProperties;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("clientTypeId", getClientTypeId())
            .toString();
    }

}
