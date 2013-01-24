package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDomainMigration implements Serializable {

    /** identifier field */
    private BigDecimal domainMigrationId;

    /** nullable persistent field */
    private Clob adminMessageClob;

    /** persistent field */
    private net.project.hibernate.model.PnUserDomain pnUserDomainByToDomain;

    /** persistent field */
    private net.project.hibernate.model.PnUserDomain pnUserDomainByFromDomain;

    /** persistent field */
    private Set pnUserDomainMigrations;

    /** full constructor */
    public PnDomainMigration(BigDecimal domainMigrationId, Clob adminMessageClob, net.project.hibernate.model.PnUserDomain pnUserDomainByToDomain, net.project.hibernate.model.PnUserDomain pnUserDomainByFromDomain, Set pnUserDomainMigrations) {
        this.domainMigrationId = domainMigrationId;
        this.adminMessageClob = adminMessageClob;
        this.pnUserDomainByToDomain = pnUserDomainByToDomain;
        this.pnUserDomainByFromDomain = pnUserDomainByFromDomain;
        this.pnUserDomainMigrations = pnUserDomainMigrations;
    }

    /** default constructor */
    public PnDomainMigration() {
    }

    /** minimal constructor */
    public PnDomainMigration(BigDecimal domainMigrationId, net.project.hibernate.model.PnUserDomain pnUserDomainByToDomain, net.project.hibernate.model.PnUserDomain pnUserDomainByFromDomain, Set pnUserDomainMigrations) {
        this.domainMigrationId = domainMigrationId;
        this.pnUserDomainByToDomain = pnUserDomainByToDomain;
        this.pnUserDomainByFromDomain = pnUserDomainByFromDomain;
        this.pnUserDomainMigrations = pnUserDomainMigrations;
    }

    public BigDecimal getDomainMigrationId() {
        return this.domainMigrationId;
    }

    public void setDomainMigrationId(BigDecimal domainMigrationId) {
        this.domainMigrationId = domainMigrationId;
    }

    public Clob getAdminMessageClob() {
        return this.adminMessageClob;
    }

    public void setAdminMessageClob(Clob adminMessageClob) {
        this.adminMessageClob = adminMessageClob;
    }

    public net.project.hibernate.model.PnUserDomain getPnUserDomainByToDomain() {
        return this.pnUserDomainByToDomain;
    }

    public void setPnUserDomainByToDomain(net.project.hibernate.model.PnUserDomain pnUserDomainByToDomain) {
        this.pnUserDomainByToDomain = pnUserDomainByToDomain;
    }

    public net.project.hibernate.model.PnUserDomain getPnUserDomainByFromDomain() {
        return this.pnUserDomainByFromDomain;
    }

    public void setPnUserDomainByFromDomain(net.project.hibernate.model.PnUserDomain pnUserDomainByFromDomain) {
        this.pnUserDomainByFromDomain = pnUserDomainByFromDomain;
    }

    public Set getPnUserDomainMigrations() {
        return this.pnUserDomainMigrations;
    }

    public void setPnUserDomainMigrations(Set pnUserDomainMigrations) {
        this.pnUserDomainMigrations = pnUserDomainMigrations;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("domainMigrationId", getDomainMigrationId())
            .toString();
    }

}
