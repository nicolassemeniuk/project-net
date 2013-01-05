package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class DatabaseVersionUpdate implements Serializable {

    /** identifier field */
    private Integer majorVersion;

    /** identifier field */
    private Integer minorVersion;

    /** identifier field */
    private Integer subMinorVersion;

    /** identifier field */
    private String patchFilename;

    /** identifier field */
    private String patchDescription;

    /** identifier field */
    private Date timestamp;

    /** full constructor */
    public DatabaseVersionUpdate(Integer majorVersion, Integer minorVersion, Integer subMinorVersion, String patchFilename, String patchDescription, Date timestamp) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.subMinorVersion = subMinorVersion;
        this.patchFilename = patchFilename;
        this.patchDescription = patchDescription;
        this.timestamp = timestamp;
    }

    /** default constructor */
    public DatabaseVersionUpdate() {
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

    public String getPatchFilename() {
        return this.patchFilename;
    }

    public void setPatchFilename(String patchFilename) {
        this.patchFilename = patchFilename;
    }

    public String getPatchDescription() {
        return this.patchDescription;
    }

    public void setPatchDescription(String patchDescription) {
        this.patchDescription = patchDescription;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("majorVersion", getMajorVersion())
            .append("minorVersion", getMinorVersion())
            .append("subMinorVersion", getSubMinorVersion())
            .append("patchFilename", getPatchFilename())
            .append("patchDescription", getPatchDescription())
            .append("timestamp", getTimestamp())
            .toString();
    }

}
