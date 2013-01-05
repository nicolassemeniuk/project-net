package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAuthenticator implements Serializable {

    /** identifier field */
    private BigDecimal authenticatorId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnAuthenticatorType pnAuthenticatorType;

    /** persistent field */
    private Set pnPersonAuthenticators;

    /** full constructor */
    public PnAuthenticator(BigDecimal authenticatorId, String name, String description, String recordStatus, net.project.hibernate.model.PnAuthenticatorType pnAuthenticatorType, Set pnPersonAuthenticators) {
        this.authenticatorId = authenticatorId;
        this.name = name;
        this.description = description;
        this.recordStatus = recordStatus;
        this.pnAuthenticatorType = pnAuthenticatorType;
        this.pnPersonAuthenticators = pnPersonAuthenticators;
    }

    /** default constructor */
    public PnAuthenticator() {
    }

    /** minimal constructor */
    public PnAuthenticator(BigDecimal authenticatorId, String recordStatus, net.project.hibernate.model.PnAuthenticatorType pnAuthenticatorType, Set pnPersonAuthenticators) {
        this.authenticatorId = authenticatorId;
        this.recordStatus = recordStatus;
        this.pnAuthenticatorType = pnAuthenticatorType;
        this.pnPersonAuthenticators = pnPersonAuthenticators;
    }

    public BigDecimal getAuthenticatorId() {
        return this.authenticatorId;
    }

    public void setAuthenticatorId(BigDecimal authenticatorId) {
        this.authenticatorId = authenticatorId;
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

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnAuthenticatorType getPnAuthenticatorType() {
        return this.pnAuthenticatorType;
    }

    public void setPnAuthenticatorType(net.project.hibernate.model.PnAuthenticatorType pnAuthenticatorType) {
        this.pnAuthenticatorType = pnAuthenticatorType;
    }

    public Set getPnPersonAuthenticators() {
        return this.pnPersonAuthenticators;
    }

    public void setPnPersonAuthenticators(Set pnPersonAuthenticators) {
        this.pnPersonAuthenticators = pnPersonAuthenticators;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("authenticatorId", getAuthenticatorId())
            .toString();
    }

}
