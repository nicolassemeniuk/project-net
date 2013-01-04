package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzSimpleTriggerPK implements Serializable {

    /** identifier field */
    private String triggerName;

    /** identifier field */
    private String triggerGroup;

    /** full constructor */
    public QrtzSimpleTriggerPK(String triggerName, String triggerGroup) {
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
    }

    /** default constructor */
    public QrtzSimpleTriggerPK() {
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

    public String toString() {
        return new ToStringBuilder(this)
            .append("triggerName", getTriggerName())
            .append("triggerGroup", getTriggerGroup())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof QrtzSimpleTriggerPK) ) return false;
        QrtzSimpleTriggerPK castOther = (QrtzSimpleTriggerPK) other;
        return new EqualsBuilder()
            .append(this.getTriggerName(), castOther.getTriggerName())
            .append(this.getTriggerGroup(), castOther.getTriggerGroup())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTriggerName())
            .append(getTriggerGroup())
            .toHashCode();
    }

}
