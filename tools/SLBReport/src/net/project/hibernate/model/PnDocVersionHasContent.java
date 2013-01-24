package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocVersionHasContent implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnDocVersionHasContentPK comp_id;

    /** persistent field */
    private BigDecimal docId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocVersion pnDocVersion;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocContentElement pnDocContentElement;

    /** full constructor */
    public PnDocVersionHasContent(net.project.hibernate.model.PnDocVersionHasContentPK comp_id, BigDecimal docId, net.project.hibernate.model.PnDocVersion pnDocVersion, net.project.hibernate.model.PnDocContentElement pnDocContentElement) {
        this.comp_id = comp_id;
        this.docId = docId;
        this.pnDocVersion = pnDocVersion;
        this.pnDocContentElement = pnDocContentElement;
    }

    /** default constructor */
    public PnDocVersionHasContent() {
    }

    /** minimal constructor */
    public PnDocVersionHasContent(net.project.hibernate.model.PnDocVersionHasContentPK comp_id, BigDecimal docId) {
        this.comp_id = comp_id;
        this.docId = docId;
    }

    public net.project.hibernate.model.PnDocVersionHasContentPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnDocVersionHasContentPK comp_id) {
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

    public net.project.hibernate.model.PnDocContentElement getPnDocContentElement() {
        return this.pnDocContentElement;
    }

    public void setPnDocContentElement(net.project.hibernate.model.PnDocContentElement pnDocContentElement) {
        this.pnDocContentElement = pnDocContentElement;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnDocVersionHasContent) ) return false;
        PnDocVersionHasContent castOther = (PnDocVersionHasContent) other;
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
