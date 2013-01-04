package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEventType implements Serializable {

    /** identifier field */
    private BigDecimal eventTypeId;

    /** nullable persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date crc;

    /** persistent field */
    private net.project.hibernate.model.PnObjectType pnObjectType;

    /** persistent field */
    private Set pnEventHasNotifications;

    /** full constructor */
    public PnEventType(BigDecimal eventTypeId, String name, String description, String recordStatus, Date crc, net.project.hibernate.model.PnObjectType pnObjectType, Set pnEventHasNotifications) {
        this.eventTypeId = eventTypeId;
        this.name = name;
        this.description = description;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.pnObjectType = pnObjectType;
        this.pnEventHasNotifications = pnEventHasNotifications;
    }

    /** default constructor */
    public PnEventType() {
    }

    /** minimal constructor */
    public PnEventType(BigDecimal eventTypeId, net.project.hibernate.model.PnObjectType pnObjectType, Set pnEventHasNotifications) {
        this.eventTypeId = eventTypeId;
        this.pnObjectType = pnObjectType;
        this.pnEventHasNotifications = pnEventHasNotifications;
    }

    public BigDecimal getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(BigDecimal eventTypeId) {
        this.eventTypeId = eventTypeId;
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

    public net.project.hibernate.model.PnObjectType getPnObjectType() {
        return this.pnObjectType;
    }

    public void setPnObjectType(net.project.hibernate.model.PnObjectType pnObjectType) {
        this.pnObjectType = pnObjectType;
    }

    public Set getPnEventHasNotifications() {
        return this.pnEventHasNotifications;
    }

    public void setPnEventHasNotifications(Set pnEventHasNotifications) {
        this.pnEventHasNotifications = pnEventHasNotifications;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("eventTypeId", getEventTypeId())
            .toString();
    }

}
