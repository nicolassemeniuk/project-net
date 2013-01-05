package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzTriggerListenerPK implements Serializable {

    /** identifier field */
    private String triggerName;

    /** identifier field */
    private String triggerGroup;

    /** identifier field */
    private String triggerListener;

    /** full constructor */
    public QrtzTriggerListenerPK(String triggerName, String triggerGroup, String triggerListener) {
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        this.triggerListener = triggerListener;
    }

    /** default constructor */
    public QrtzTriggerListenerPK() {
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerGroup() {
        return this.triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getTriggerListener() {
        return this.triggerListener;
    }

    public void setTriggerListener(String triggerListener) {
        this.triggerListener = triggerListener;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("triggerName", getTriggerName())
            .append("triggerGroup", getTriggerGroup())
            .append("triggerListener", getTriggerListener())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzTriggerListenerPK) ) return false;
        QrtzTriggerListenerPK castOther = (QrtzTriggerListenerPK) other;
        return new EqualsBuilder()
            .append(this.getTriggerName(), castOther.getTriggerName())
            .append(this.getTriggerGroup(), castOther.getTriggerGroup())
            .append(this.getTriggerListener(), castOther.getTriggerListener())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTriggerName())
            .append(getTriggerGroup())
            .append(getTriggerListener())
            .toHashCode();
    }

}
