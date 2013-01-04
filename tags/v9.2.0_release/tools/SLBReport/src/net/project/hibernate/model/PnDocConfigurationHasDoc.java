package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocConfigurationHasDoc implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocConfigurationHasDocPK comp_id;

    /** persistent field */
    private BigDecimal docId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocVersion pnDocVersion;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocConfiguration pnDocConfiguration;

    /** full constructor */
    public PnDocConfigurationHasDoc(net.project.hibernate.model.PnDocConfigurationHasDocPK comp_id, BigDecimal docId, net.project.hibernate.model.PnDocVersion pnDocVersion, net.project.hibernate.model.PnDocConfiguration pnDocConfiguration) {
        this.comp_id = comp_id;
        this.docId = docId;
        this.pnDocVersion = pnDocVersion;
        this.pnDocConfiguration = pnDocConfiguration;
    }

    /** default constructor */
    public PnDocConfigurationHasDoc() {
    }

    /** minimal constructor */
    public PnDocConfigurationHasDoc(net.project.hibernate.model.PnDocConfigurationHasDocPK comp_id, BigDecimal docId) {
        this.comp_id = comp_id;
        this.docId = docId;
    }

    public net.project.hibernate.model.PnDocConfigurationHasDocPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocConfigurationHasDocPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getDocId() {
        return this.docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public net.project.hibernate.model.PnDocVersion getPnDocVersion() {
        return this.pnDocVersion;
    }

    public void setPnDocVersion(net.project.hibernate.model.PnDocVersion pnDocVersion) {
        this.pnDocVersion = pnDocVersion;
    }

    public net.project.hibernate.model.PnDocConfiguration getPnDocConfiguration() {
        return this.pnDocConfiguration;
    }

    public void setPnDocConfiguration(net.project.hibernate.model.PnDocConfiguration pnDocConfiguration) {
        this.pnDocConfiguration = pnDocConfiguration;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocConfigurationHasDoc) ) return false;
        PnDocConfigurationHasDoc castOther = (PnDocConfigurationHasDoc) other;
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
