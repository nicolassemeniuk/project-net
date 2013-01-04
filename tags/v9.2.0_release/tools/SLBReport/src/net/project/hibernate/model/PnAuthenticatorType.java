package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAuthenticatorType implements Serializable {

    /** identifier field */
    private String type;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnAuthenticators;

    /** full constructor */
    public PnAuthenticatorType(String type, String description, String recordStatus, Set pnAuthenticators) {
        this.type = type;
        this.description = description;
        this.recordStatus = recordStatus;
        this.pnAuthenticators = pnAuthenticators;
    }

    /** default constructor */
    public PnAuthenticatorType() {
    }

    /** minimal constructor */
    public PnAuthenticatorType(String type, String recordStatus, Set pnAuthenticators) {
        this.type = type;
        this.recordStatus = recordStatus;
        this.pnAuthenticators = pnAuthenticators;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnAuthenticators() {
        return this.pnAuthenticators;
    }

    public void setPnAuthenticators(Set pnAuthenticators) {
        this.pnAuthenticators = pnAuthenticators;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("type", getType())
            .toString();
    }

}
