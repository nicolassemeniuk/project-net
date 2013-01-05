package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class DatabaseVersionPK implements Serializable {

    /** identifier field */
    private Integer majorVersion;

    /** identifier field */
    private Integer minorVersion;

    /** identifier field */
    private Integer subMinorVersion;

    /** full constructor */
    public DatabaseVersionPK(Integer majorVersion, Integer minorVersion, Integer subMinorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.subMinorVersion = subMinorVersion;
    }

    /** default constructor */
    public DatabaseVersionPK() {
    }

    public Integer getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public Integer getSubMinorVersion() {
        return this.subMinorVersion;
    }

    public void setSubMinorVersion(Integer subMinorVersion) {
        this.subMinorVersion = subMinorVersion;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("majorVersion", getMajorVersion())
            .append("minorVersion", getMinorVersion())
            .append("subMinorVersion", getSubMinorVersion())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof DatabaseVersionPK) ) return false;
        DatabaseVersionPK castOther = (DatabaseVersionPK) other;
        return new EqualsBuilder()
            .append(this.getMajorVersion(), castOther.getMajorVersion())
            .append(this.getMinorVersion(), castOther.getMinorVersion())
            .append(this.getSubMinorVersion(), castOther.getSubMinorVersion())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getMajorVersion())
            .append(getMinorVersion())
            .append(getSubMinorVersion())
            .toHashCode();
    }

}
