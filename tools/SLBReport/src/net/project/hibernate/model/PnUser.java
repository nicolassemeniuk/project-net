package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUser implements Serializable {

    /** identifier field */
    private BigDecimal userId;

    /** persistent field */
    private String username;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private int isLogin;

    /** nullable persistent field */
    private BigDecimal lastBrandId;

    /** nullable persistent field */
    private Date lastLogin;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnUserDomain pnUserDomain;

    /** persistent field */
    private Set pnUserDomainMigrations;

    /** full constructor */
    public PnUser(BigDecimal userId, String username, String recordStatus, int isLogin, BigDecimal lastBrandId, Date lastLogin, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnUserDomain pnUserDomain, Set pnUserDomainMigrations) {
        this.userId = userId;
        this.username = username;
        this.recordStatus = recordStatus;
        this.isLogin = isLogin;
        this.lastBrandId = lastBrandId;
        this.lastLogin = lastLogin;
        this.pnPerson = pnPerson;
        this.pnUserDomain = pnUserDomain;
        this.pnUserDomainMigrations = pnUserDomainMigrations;
    }

    /** default constructor */
    public PnUser() {
    }

    /** minimal constructor */
    public PnUser(BigDecimal userId, String username, String recordStatus, int isLogin, net.project.hibernate.model.PnUserDomain pnUserDomain, Set pnUserDomainMigrations) {
        this.userId = userId;
        this.username = username;
        this.recordStatus = recordStatus;
        this.isLogin = isLogin;
        this.pnUserDomain = pnUserDomain;
        this.pnUserDomainMigrations = pnUserDomainMigrations;
    }

    /** 
     * 		       Foreign key to pn_person.person_id
     * 		    
     */
    public BigDecimal getUserId() {
        return this.userId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getIsLogin() {
        return this.isLogin;
    }

    public void setIsLogin(int isLogin) {
        this.isLogin = isLogin;
    }

    public BigDecimal getLastBrandId() {
        return this.lastBrandId;
    }

    public void setLastBrandId(BigDecimal lastBrandId) {
        this.lastBrandId = lastBrandId;
    }

    public Date getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnUserDomain getPnUserDomain() {
        return this.pnUserDomain;
    }

    public void setPnUserDomain(net.project.hibernate.model.PnUserDomain pnUserDomain) {
        this.pnUserDomain = pnUserDomain;
    }

    public Set getPnUserDomainMigrations() {
        return this.pnUserDomainMigrations;
    }

    public void setPnUserDomainMigrations(Set pnUserDomainMigrations) {
        this.pnUserDomainMigrations = pnUserDomainMigrations;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("userId", getUserId())
            .toString();
    }

}
