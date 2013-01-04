package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnConfigurationSpace implements Serializable {

    /** identifier field */
    private BigDecimal configurationId;

    /** persistent field */
    private String configurationName;

    /** nullable persistent field */
    private String configurationDesc;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** nullable persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private Date modifiedDatetime;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private BigDecimal brandId;

    /** persistent field */
    private Set pnPortfolioHasConfigurations;

    /** full constructor */
    public PnConfigurationSpace(BigDecimal configurationId, String configurationName, String configurationDesc, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, BigDecimal brandId, Set pnPortfolioHasConfigurations) {
        this.configurationId = configurationId;
        this.configurationName = configurationName;
        this.configurationDesc = configurationDesc;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.brandId = brandId;
        this.pnPortfolioHasConfigurations = pnPortfolioHasConfigurations;
    }

    /** default constructor */
    public PnConfigurationSpace() {
    }

    /** minimal constructor */
    public PnConfigurationSpace(BigDecimal configurationId, String configurationName, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, BigDecimal brandId, Set pnPortfolioHasConfigurations) {
        this.configurationId = configurationId;
        this.configurationName = configurationName;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.brandId = brandId;
        this.pnPortfolioHasConfigurations = pnPortfolioHasConfigurations;
    }

    public BigDecimal getConfigurationId() {
        return this.configurationId;
    }

    public void setConfigurationId(BigDecimal configurationId) {
        this.configurationId = configurationId;
    }

    public String getConfigurationName() {
        return this.configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getConfigurationDesc() {
        return this.configurationDesc;
    }

    public void setConfigurationDesc(String configurationDesc) {
        this.configurationDesc = configurationDesc;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public BigDecimal getBrandId() {
        return this.brandId;
    }

    public void setBrandId(BigDecimal brandId) {
        this.brandId = brandId;
    }

    public Set getPnPortfolioHasConfigurations() {
        return this.pnPortfolioHasConfigurations;
    }

    public void setPnPortfolioHasConfigurations(Set pnPortfolioHasConfigurations) {
        this.pnPortfolioHasConfigurations = pnPortfolioHasConfigurations;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("configurationId", getConfigurationId())
            .toString();
    }

}
