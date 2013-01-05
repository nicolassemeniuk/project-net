package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDirectoryProviderType implements Serializable {

    /** identifier field */
    private BigDecimal providerTypeId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String serviceProviderClassName;

    /** persistent field */
    private String configuratorClassName;

    /** persistent field */
    private String configurationClassName;

    /** persistent field */
    private Set pnUserDomains;

    /** full constructor */
    public PnDirectoryProviderType(BigDecimal providerTypeId, String name, String description, String serviceProviderClassName, String configuratorClassName, String configurationClassName, Set pnUserDomains) {
        this.providerTypeId = providerTypeId;
        this.name = name;
        this.description = description;
        this.serviceProviderClassName = serviceProviderClassName;
        this.configuratorClassName = configuratorClassName;
        this.configurationClassName = configurationClassName;
        this.pnUserDomains = pnUserDomains;
    }

    /** default constructor */
    public PnDirectoryProviderType() {
    }

    /** minimal constructor */
    public PnDirectoryProviderType(BigDecimal providerTypeId, String name, String serviceProviderClassName, String configuratorClassName, String configurationClassName, Set pnUserDomains) {
        this.providerTypeId = providerTypeId;
        this.name = name;
        this.serviceProviderClassName = serviceProviderClassName;
        this.configuratorClassName = configuratorClassName;
        this.configurationClassName = configurationClassName;
        this.pnUserDomains = pnUserDomains;
    }

    public BigDecimal getProviderTypeId() {
        return this.providerTypeId;
    }

    public void setProviderTypeId(BigDecimal providerTypeId) {
        this.providerTypeId = providerTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceProviderClassName() {
        return this.serviceProviderClassName;
    }

    public void setServiceProviderClassName(String serviceProviderClassName) {
        this.serviceProviderClassName = serviceProviderClassName;
    }

    public String getConfiguratorClassName() {
        return this.configuratorClassName;
    }

    public void setConfiguratorClassName(String configuratorClassName) {
        this.configuratorClassName = configuratorClassName;
    }

    public String getConfigurationClassName() {
        return this.configurationClassName;
    }

    public void setConfigurationClassName(String configurationClassName) {
        this.configurationClassName = configurationClassName;
    }

    public Set getPnUserDomains() {
        return this.pnUserDomains;
    }

    public void setPnUserDomains(Set pnUserDomains) {
        this.pnUserDomains = pnUserDomains;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("providerTypeId", getProviderTypeId())
            .toString();
    }

}
