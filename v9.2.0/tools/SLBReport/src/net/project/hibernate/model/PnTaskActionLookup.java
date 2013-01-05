package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTaskActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnTaskHistories;

    /** full constructor */
    public PnTaskActionLookup(String action, String recordStatus, Set pnTaskHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnTaskHistories = pnTaskHistories;
    }

    /** default constructor */
    public PnTaskActionLookup() {
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

    public Set getPnTaskHistories() {
        return this.pnTaskHistories;
    }

    public void setPnTaskHistories(Set pnTaskHistories) {
        this.pnTaskHistories = pnTaskHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
