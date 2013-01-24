package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLicenseStatusReason implements Serializable {

    /** identifier field */
    private Integer reasonCode;

    /** persistent field */
    private String shortName;

    /** persistent field */
    private String message;

    /** persistent field */
    private Set pnLicenses;

    /** full constructor */
    public PnLicenseStatusReason(Integer reasonCode, String shortName, String message, Set pnLicenses) {
        this.reasonCode = reasonCode;
        this.shortName = shortName;
        this.message = message;
        this.pnLicenses = pnLicenses;
    }

    /** default constructor */
    public PnLicenseStatusReason() {
    }

    public Integer getReasonCode() {
        return this.reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Set getPnLicenses() {
        return this.pnLicenses;
    }

    public void setPnLicenses(Set pnLicenses) {
        this.pnLicenses = pnLicenses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("reasonCode", getReasonCode())
            .toString();
    }

}
