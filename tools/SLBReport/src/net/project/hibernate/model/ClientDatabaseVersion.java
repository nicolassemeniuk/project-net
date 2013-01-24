package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class ClientDatabaseVersion implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.ClientDatabaseVersionPK comp_id;

    /** persistent field */
    private String clientName;

    /** nullable persistent field */
    private Integer prmMajorVersion;

    /** nullable persistent field */
    private Integer prmMinorVersion;

    /** nullable persistent field */
    private Integer prmSubMinorVersion;

    /** persistent field */
    private Date timestamp;

    /** persistent field */
    private String description;

    /** full constructor */
    public ClientDatabaseVersion(net.project.hibernate.model.ClientDatabaseVersionPK comp_id, String clientName, Integer prmMajorVersion, Integer prmMinorVersion, Integer prmSubMinorVersion, Date timestamp, String description) {
        this.comp_id = comp_id;
        this.clientName = clientName;
        this.prmMajorVersion = prmMajorVersion;
        this.prmMinorVersion = prmMinorVersion;
        this.prmSubMinorVersion = prmSubMinorVersion;
        this.timestamp = timestamp;
        this.description = description;
    }

    /** default constructor */
    public ClientDatabaseVersion() {
    }

    /** minimal constructor */
    public ClientDatabaseVersion(net.project.hibernate.model.ClientDatabaseVersionPK comp_id, String clientName, Date timestamp, String description) {
        this.comp_id = comp_id;
        this.clientName = clientName;
        this.timestamp = timestamp;
        this.description = description;
    }

    public net.project.hibernate.model.ClientDatabaseVersionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.ClientDatabaseVersionPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getPrmMajorVersion() {
        return this.prmMajorVersion;
    }

    public void setPrmMajorVersion(Integer prmMajorVersion) {
        this.prmMajorVersion = prmMajorVersion;
    }

    public Integer getPrmMinorVersion() {
        return this.prmMinorVersion;
    }

    public void setPrmMinorVersion(Integer prmMinorVersion) {
        this.prmMinorVersion = prmMinorVersion;
    }

    public Integer getPrmSubMinorVersion() {
        return this.prmSubMinorVersion;
    }

    public void setPrmSubMinorVersion(Integer prmSubMinorVersion) {
        this.prmSubMinorVersion = prmSubMinorVersion;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof ClientDatabaseVersion) ) return false;
        ClientDatabaseVersion castOther = (ClientDatabaseVersion) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
