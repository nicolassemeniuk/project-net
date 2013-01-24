package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDirectory implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasDirectoryPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDirectory pnDirectory;

    /** full constructor */
    public PnSpaceHasDirectory(net.project.hibernate.model.PnSpaceHasDirectoryPK comp_id, net.project.hibernate.model.PnDirectory pnDirectory) {
        this.comp_id = comp_id;
        this.pnDirectory = pnDirectory;
    }

    /** default constructor */
    public PnSpaceHasDirectory() {
    }

    /** minimal constructor */
    public PnSpaceHasDirectory(net.project.hibernate.model.PnSpaceHasDirectoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnSpaceHasDirectoryPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasDirectoryPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnDirectory getPnDirectory() {
        return this.pnDirectory;
    }

    public void setPnDirectory(net.project.hibernate.model.PnDirectory pnDirectory) {
        this.pnDirectory = pnDirectory;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasDirectory) ) return false;
        PnSpaceHasDirectory castOther = (PnSpaceHasDirectory) other;
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
