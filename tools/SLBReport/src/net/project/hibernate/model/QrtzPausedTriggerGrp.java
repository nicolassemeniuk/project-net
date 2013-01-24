package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzPausedTriggerGrp implements Serializable {

    /** identifier field */
    private String triggerGroup;

    /** full constructor */
    public QrtzPausedTriggerGrp(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    /** default constructor */
    public QrtzPausedTriggerGrp() {
    }

    public String getTriggerGroup() {
        return this.triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("triggerGroup", getTriggerGroup())
            .toString();
    }

}
