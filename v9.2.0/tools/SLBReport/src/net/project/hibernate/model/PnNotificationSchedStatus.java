package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationSchedStatus implements Serializable {

    /** identifier field */
    private BigDecimal schedulerId;

    /** persistent field */
    private Date lastCheckDatetime;

    /** full constructor */
    public PnNotificationSchedStatus(BigDecimal schedulerId, Date lastCheckDatetime) {
        this.schedulerId = schedulerId;
        this.lastCheckDatetime = lastCheckDatetime;
    }

    /** default constructor */
    public PnNotificationSchedStatus() {
    }

    public BigDecimal getSchedulerId() {
        return this.schedulerId;
    }

    public void setSchedulerId(BigDecimal schedulerId) {
        this.schedulerId = schedulerId;
    }

    public Date getLastCheckDatetime() {
        return this.lastCheckDatetime;
    }

    public void setLastCheckDatetime(Date lastCheckDatetime) {
        this.lastCheckDatetime = lastCheckDatetime;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("schedulerId", getSchedulerId())
            .toString();
    }

}
