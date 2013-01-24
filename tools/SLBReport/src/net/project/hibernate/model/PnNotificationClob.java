package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationClob implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** nullable persistent field */
    private Clob clobField;

    /** persistent field */
    private Set pnNotifications;

    /** full constructor */
    public PnNotificationClob(BigDecimal objectId, Clob clobField, Set pnNotifications) {
        this.objectId = objectId;
        this.clobField = clobField;
        this.pnNotifications = pnNotifications;
    }

    /** default constructor */
    public PnNotificationClob() {
    }

    /** minimal constructor */
    public PnNotificationClob(BigDecimal objectId, Set pnNotifications) {
        this.objectId = objectId;
        this.pnNotifications = pnNotifications;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public Clob getClobField() {
        return this.clobField;
    }

    public void setClobField(Clob clobField) {
        this.clobField = clobField;
    }

    public Set getPnNotifications() {
        return this.pnNotifications;
    }

    public void setPnNotifications(Set pnNotifications) {
        this.pnNotifications = pnNotifications;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
