package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPostActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnPostHistories;

    /** full constructor */
    public PnPostActionLookup(String action, String recordStatus, Set pnPostHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnPostHistories = pnPostHistories;
    }

    /** default constructor */
    public PnPostActionLookup() {
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

    public Set getPnPostHistories() {
        return this.pnPostHistories;
    }

    public void setPnPostHistories(Set pnPostHistories) {
        this.pnPostHistories = pnPostHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
