package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserDomainMigration implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnUserDomainMigrationPK comp_id;

    /** persistent field */
    private int migrationStatusId;

    /** persistent field */
    private Date activityDate;

    /** persistent field */
    private int isCurrent;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDomainMigration pnDomainMigration;

    /** nullable persistent field */
    private net.project.hibernate.model.PnUser pnUser;

    /** full constructor */
    public PnUserDomainMigration(net.project.hibernate.model.PnUserDomainMigrationPK comp_id, int migrationStatusId, Date activityDate, int isCurrent, net.project.hibernate.model.PnDomainMigration pnDomainMigration, net.project.hibernate.model.PnUser pnUser) {
        this.comp_id = comp_id;
        this.migrationStatusId = migrationStatusId;
        this.activityDate = activityDate;
        this.isCurrent = isCurrent;
        this.pnDomainMigration = pnDomainMigration;
        this.pnUser = pnUser;
    }

    /** default constructor */
    public PnUserDomainMigration() {
    }

    /** minimal constructor */
    public PnUserDomainMigration(net.project.hibernate.model.PnUserDomainMigrationPK comp_id, int migrationStatusId, Date activityDate, int isCurrent) {
        this.comp_id = comp_id;
        this.migrationStatusId = migrationStatusId;
        this.activityDate = activityDate;
        this.isCurrent = isCurrent;
    }

    public net.project.hibernate.model.PnUserDomainMigrationPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnUserDomainMigrationPK comp_id) {
        this.comp_id = comp_id;
    }

    public int getMigrationStatusId() {
        return this.migrationStatusId;
    }

    public void setMigrationStatusId(int migrationStatusId) {
        this.migrationStatusId = migrationStatusId;
    }

    public Date getActivityDate() {
        return this.activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public int getIsCurrent() {
        return this.isCurrent;
    }

    public void setIsCurrent(int isCurrent) {
        this.isCurrent = isCurrent;
    }

    public net.project.hibernate.model.PnDomainMigration getPnDomainMigration() {
        return this.pnDomainMigration;
    }

    public void setPnDomainMigration(net.project.hibernate.model.PnDomainMigration pnDomainMigration) {
        this.pnDomainMigration = pnDomainMigration;
    }

    public net.project.hibernate.model.PnUser getPnUser() {
        return this.pnUser;
    }

    public void setPnUser(net.project.hibernate.model.PnUser pnUser) {
        this.pnUser = pnUser;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnUserDomainMigration) ) return false;
        PnUserDomainMigration castOther = (PnUserDomainMigration) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
