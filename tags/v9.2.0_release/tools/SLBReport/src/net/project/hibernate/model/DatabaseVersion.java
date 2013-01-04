package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class DatabaseVersion implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.DatabaseVersionPK comp_id;

    /** persistent field */
    private Date timestamp;

    /** persistent field */
    private String description;

    /** full constructor */
    public DatabaseVersion(net.project.hibernate.model.DatabaseVersionPK comp_id, Date timestamp, String description) {
        this.comp_id = comp_id;
        this.timestamp = timestamp;
        this.description = description;
    }

    /** default constructor */
    public DatabaseVersion() {
    }

    public net.project.hibernate.model.DatabaseVersionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.DatabaseVersionPK comp_id) {
        this.comp_id = comp_id;
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
        if ( !(other instanceof DatabaseVersion) ) return false;
        DatabaseVersion castOther = (DatabaseVersion) other;
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
