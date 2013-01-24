package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupActionLookup implements Serializable {

    /** identifier field */
    private String action;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnGroupHistories;

    /** full constructor */
    public PnGroupActionLookup(String action, String recordStatus, Set pnGroupHistories) {
        this.action = action;
        this.recordStatus = recordStatus;
        this.pnGroupHistories = pnGroupHistories;
    }

    /** default constructor */
    public PnGroupActionLookup() {
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

    public Set getPnGroupHistories() {
        return this.pnGroupHistories;
    }

    public void setPnGroupHistories(Set pnGroupHistories) {
        this.pnGroupHistories = pnGroupHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("action", getAction())
            .toString();
    }

}
