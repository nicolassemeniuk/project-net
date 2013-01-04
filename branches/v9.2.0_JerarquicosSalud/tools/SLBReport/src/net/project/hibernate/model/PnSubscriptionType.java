package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSubscriptionType implements Serializable {

    /** identifier field */
    private BigDecimal subscriptionTypeId;

    /** nullable persistent field */
    private String tableName;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** full constructor */
    public PnSubscriptionType(BigDecimal subscriptionTypeId, String tableName, String name, String description, String recordStatus, Date crc) {
        this.subscriptionTypeId = subscriptionTypeId;
        this.tableName = tableName;
        this.name = name;
        this.description = description;
        this.recordStatus = recordStatus;
        this.crc = crc;
    }

    /** default constructor */
    public PnSubscriptionType() {
    }

    /** minimal constructor */
    public PnSubscriptionType(BigDecimal subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }

    public BigDecimal getSubscriptionTypeId() {
        return this.subscriptionTypeId;
    }

    public void setSubscriptionTypeId(BigDecimal subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("subscriptionTypeId", getSubscriptionTypeId())
            .toString();
    }

}
