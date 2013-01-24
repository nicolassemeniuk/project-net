package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDiscussionActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnDiscussionHistories;

    /** full constructor */
    public PnDiscussionActionLookup(String action, String recordStatus, Set pnDiscussionHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnDiscussionHistories = pnDiscussionHistories;
    }

    /** default constructor */
    public PnDiscussionActionLookup() {
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

    public Set getPnDiscussionHistories() {
        return this.pnDiscussionHistories;
    }

    public void setPnDiscussionHistories(Set pnDiscussionHistories) {
        this.pnDiscussionHistories = pnDiscussionHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
