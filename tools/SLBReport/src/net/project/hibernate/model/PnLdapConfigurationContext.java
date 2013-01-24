package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLdapConfigurationContext implements Serializable {

    /** identifier field */
    private BigDecimal contextId;

    /** persistent field */
    private String protocol;

    /** persistent field */
    private String host;

    /** persistent field */
    private String port;

    /** persistent field */
    private String searchBase;

    /** persistent field */
    private String authenticationMethod;

    /** nullable persistent field */
    private String authenticatedUserDn;

    /** nullable persistent field */
    private String authenticatedUserPassword;

    /** nullable persistent field */
    private String description;

    /** full constructor */
    public PnLdapConfigurationContext(BigDecimal contextId, String protocol, String host, String port, String searchBase, String authenticationMethod, String authenticatedUserDn, String authenticatedUserPassword, String description) {
        this.contextId = contextId;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.searchBase = searchBase;
        this.authenticationMethod = authenticationMethod;
        this.authenticatedUserDn = authenticatedUserDn;
        this.authenticatedUserPassword = authenticatedUserPassword;
        this.description = description;
    }

    /** default constructor */
    public PnLdapConfigurationContext() {
    }

    /** minimal constructor */
    public PnLdapConfigurationContext(BigDecimal contextId, String protocol, String host, String port, String searchBase, String authenticationMethod) {
        this.contextId = contextId;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.searchBase = searchBase;
        this.authenticationMethod = authenticationMethod;
    }

    public BigDecimal getContextId() {
        return this.contextId;
    }

    public void setContextId(BigDecimal contextId) {
        this.contextId = contextId;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSearchBase() {
        return this.searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public String getAuthenticationMethod() {
        return this.authenticationMethod;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public String getAuthenticatedUserDn() {
        return this.authenticatedUserDn;
    }

    public void setAuthenticatedUserDn(String authenticatedUserDn) {
        this.authenticatedUserDn = authenticatedUserDn;
    }

    public String getAuthenticatedUserPassword() {
        return this.authenticatedUserPassword;
    }

    public void setAuthenticatedUserPassword(String authenticatedUserPassword) {
        this.authenticatedUserPassword = authenticatedUserPassword;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("contextId", getContextId())
            .toString();
    }

}
