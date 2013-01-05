package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnFormsActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnFormsHistories;

    /** full constructor */
    public PnFormsActionLookup(String action, String recordStatus, Set pnFormsHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnFormsHistories = pnFormsHistories;
    }

    /** default constructor */
    public PnFormsActionLookup() {
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

    public Set getPnFormsHistories() {
        return this.pnFormsHistories;
    }

    public void setPnFormsHistories(Set pnFormsHistories) {
        this.pnFormsHistories = pnFormsHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
