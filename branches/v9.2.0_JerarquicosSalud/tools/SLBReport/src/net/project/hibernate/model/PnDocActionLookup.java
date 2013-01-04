package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnDocHistories;

    /** full constructor */
    public PnDocActionLookup(String action, String recordStatus, Set pnDocHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnDocHistories = pnDocHistories;
    }

    /** default constructor */
    public PnDocActionLookup() {
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnDocHistories() {
        return this.pnDocHistories;
    }

    public void setPnDocHistories(Set pnDocHistories) {
        this.pnDocHistories = pnDocHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
