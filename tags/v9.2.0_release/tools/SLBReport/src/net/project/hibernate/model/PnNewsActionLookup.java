package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNewsActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnNewsHistories;

    /** full constructor */
    public PnNewsActionLookup(String action, String recordStatus, Set pnNewsHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnNewsHistories = pnNewsHistories;
    }

    /** default constructor */
    public PnNewsActionLookup() {
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

    public Set getPnNewsHistories() {
        return this.pnNewsHistories;
    }

    public void setPnNewsHistories(Set pnNewsHistories) {
        this.pnNewsHistories = pnNewsHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
