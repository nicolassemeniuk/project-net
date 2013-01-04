package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserDomain implements Serializable {

    /** identifier field */
    private BigDecimal domainId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Integer isVerificationRequired;

    /** nullable persistent field */
    private Clob registrationInstructionsClob;

    /** nullable persistent field */
    private Integer supportsCreditCardPurchases;

    /** persistent field */
    private net.project.hibernate.model.PnDirectoryProviderType pnDirectoryProviderType;

    /** persistent field */
    private Set pnDomainMigrationsByToDomain;

    /** persistent field */
    private Set pnDomainMigrationsByFromDomain;

    /** persistent field */
    private Set pnUsers;

    /** full constructor */
    public PnUserDomain(BigDecimal domainId, String name, String description, String recordStatus, Integer isVerificationRequired, Clob registrationInstructionsClob, Integer supportsCreditCardPurchases, net.project.hibernate.model.PnDirectoryProviderType pnDirectoryProviderType, Set pnDomainMigrationsByToDomain, Set pnDomainMigrationsByFromDomain, Set pnUsers) {
        this.domainId = domainId;
        this.name = name;
        this.description = description;
        this.recordStatus = recordStatus;
        this.isVerificationRequired = isVerificationRequired;
        this.registrationInstructionsClob = registrationInstructionsClob;
        this.supportsCreditCardPurchases = supportsCreditCardPurchases;
        this.pnDirectoryProviderType = pnDirectoryProviderType;
        this.pnDomainMigrationsByToDomain = pnDomainMigrationsByToDomain;
        this.pnDomainMigrationsByFromDomain = pnDomainMigrationsByFromDomain;
        this.pnUsers = pnUsers;
    }

    /** default constructor */
    public PnUserDomain() {
    }

    /** minimal constructor */
    public PnUserDomain(BigDecimal domainId, String name, String recordStatus, net.project.hibernate.model.PnDirectoryProviderType pnDirectoryProviderType, Set pnDomainMigrationsByToDomain, Set pnDomainMigrationsByFromDomain, Set pnUsers) {
        this.domainId = domainId;
        this.name = name;
        this.recordStatus = recordStatus;
        this.pnDirectoryProviderType = pnDirectoryProviderType;
        this.pnDomainMigrationsByToDomain = pnDomainMigrationsByToDomain;
        this.pnDomainMigrationsByFromDomain = pnDomainMigrationsByFromDomain;
        this.pnUsers = pnUsers;
    }

    public BigDecimal getDomainId() {
        return this.domainId;
    }

    public void setDomainId(BigDecimal domainId) {
        this.domainId = domainId;
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

    public Integer getIsVerificationRequired() {
        return this.isVerificationRequired;
    }

    public void setIsVerificationRequired(Integer isVerificationRequired) {
        this.isVerificationRequired = isVerificationRequired;
    }

    public Clob getRegistrationInstructionsClob() {
        return this.registrationInstructionsClob;
    }

    public void setRegistrationInstructionsClob(Clob registrationInstructionsClob) {
        this.registrationInstructionsClob = registrationInstructionsClob;
    }

    public Integer getSupportsCreditCardPurchases() {
        return this.supportsCreditCardPurchases;
    }

    public void setSupportsCreditCardPurchases(Integer supportsCreditCardPurchases) {
        this.supportsCreditCardPurchases = supportsCreditCardPurchases;
    }

    public net.project.hibernate.model.PnDirectoryProviderType getPnDirectoryProviderType() {
        return this.pnDirectoryProviderType;
    }

    public void setPnDirectoryProviderType(net.project.hibernate.model.PnDirectoryProviderType pnDirectoryProviderType) {
        this.pnDirectoryProviderType = pnDirectoryProviderType;
    }

    public Set getPnDomainMigrationsByToDomain() {
        return this.pnDomainMigrationsByToDomain;
    }

    public void setPnDomainMigrationsByToDomain(Set pnDomainMigrationsByToDomain) {
        this.pnDomainMigrationsByToDomain = pnDomainMigrationsByToDomain;
    }

    public Set getPnDomainMigrationsByFromDomain() {
        return this.pnDomainMigrationsByFromDomain;
    }

    public void setPnDomainMigrationsByFromDomain(Set pnDomainMigrationsByFromDomain) {
        this.pnDomainMigrationsByFromDomain = pnDomainMigrationsByFromDomain;
    }

    public Set getPnUsers() {
        return this.pnUsers;
    }

    public void setPnUsers(Set pnUsers) {
        this.pnUsers = pnUsers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("domainId", getDomainId())
            .toString();
    }

}
