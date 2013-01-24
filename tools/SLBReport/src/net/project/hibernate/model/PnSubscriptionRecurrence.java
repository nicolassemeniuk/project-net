package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSubscriptionRecurrence implements Serializable {

    /** identifier field */
    private BigDecimal recurrenceId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** full constructor */
    public PnSubscriptionRecurrence(BigDecimal recurrenceId, String name, String description, String recordStatus, Date crc) {
        this.recurrenceId = recurrenceId;
        this.name = name;
        this.description = description;
        this.recordStatus = recordStatus;
        this.crc = crc;
    }

    /** default constructor */
    public PnSubscriptionRecurrence() {
    }

    /** minimal constructor */
    public PnSubscriptionRecurrence(BigDecimal recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    public BigDecimal getRecurrenceId() {
        return this.recurrenceId;
    }

    public void setRecurrenceId(BigDecimal recurrenceId) {
        this.recurrenceId = recurrenceId;
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
            .append("recurrenceId", getRecurrenceId())
            .toString();
    }

}
