package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceTypeHasReportTypePK implements Serializable {

    /** identifier field */
    private String spaceType;

    /** identifier field */
    private String reportType;

    /** full constructor */
    public PnSpaceTypeHasReportTypePK(String spaceType, String reportType) {
        this.spaceType = spaceType;
        this.reportType = reportType;
    }

    /** default constructor */
    public PnSpaceTypeHasReportTypePK() {
    }

    public String getSpaceType() {
        return this.spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    public String getReportType() {
        return this.reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spaceType", getSpaceType())
            .append("reportType", getReportType())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceTypeHasReportTypePK) ) return false;
        PnSpaceTypeHasReportTypePK castOther = (PnSpaceTypeHasReportTypePK) other;
        return new EqualsBuilder()
            .append(this.getSpaceType(), castOther.getSpaceType())
            .append(this.getReportType(), castOther.getReportType())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getSpaceType())
            .append(getReportType())
            .toHashCode();
    }

}
