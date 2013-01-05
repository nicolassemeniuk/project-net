package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnApplicationSpace implements Serializable {

    /** identifier field */
    private BigDecimal applicationId;

    /** persistent field */
    private String applicationName;

    /** nullable persistent field */
    private String applicationDesc;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** full constructor */
    public PnApplicationSpace(BigDecimal applicationId, String applicationName, String applicationDesc, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationDesc = applicationDesc;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
    }

    /** default constructor */
    public PnApplicationSpace() {
    }

    /** minimal constructor */
    public PnApplicationSpace(BigDecimal applicationId, String applicationName, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
    }

    public BigDecimal getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(BigDecimal applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationDesc() {
        return this.applicationDesc;
    }

    public void setApplicationDesc(String applicationDesc) {
        this.applicationDesc = applicationDesc;
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("applicationId", getApplicationId())
            .toString();
    }

}
