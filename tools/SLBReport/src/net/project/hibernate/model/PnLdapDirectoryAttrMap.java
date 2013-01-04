package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLdapDirectoryAttrMap implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnLdapDirectoryAttrMapPK comp_id;

    /** persistent field */
    private String ldapAttributeName;

    /** nullable persistent field */
    private Integer ldapAttributeValueIndex;

    /** nullable persistent field */
    private net.project.hibernate.model.PnLdapDirectoryConfig pnLdapDirectoryConfig;

    /** full constructor */
    public PnLdapDirectoryAttrMap(net.project.hibernate.model.PnLdapDirectoryAttrMapPK comp_id, String ldapAttributeName, Integer ldapAttributeValueIndex, net.project.hibernate.model.PnLdapDirectoryConfig pnLdapDirectoryConfig) {
        this.comp_id = comp_id;
        this.ldapAttributeName = ldapAttributeName;
        this.ldapAttributeValueIndex = ldapAttributeValueIndex;
        this.pnLdapDirectoryConfig = pnLdapDirectoryConfig;
    }

    /** default constructor */
    public PnLdapDirectoryAttrMap() {
    }

    /** minimal constructor */
    public PnLdapDirectoryAttrMap(net.project.hibernate.model.PnLdapDirectoryAttrMapPK comp_id, String ldapAttributeName) {
        this.comp_id = comp_id;
        this.ldapAttributeName = ldapAttributeName;
    }

    public net.project.hibernate.model.PnLdapDirectoryAttrMapPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnLdapDirectoryAttrMapPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getLdapAttributeName() {
        return this.ldapAttributeName;
    }

    public void setLdapAttributeName(String ldapAttributeName) {
        this.ldapAttributeName = ldapAttributeName;
    }

    public Integer getLdapAttributeValueIndex() {
        return this.ldapAttributeValueIndex;
    }

    public void setLdapAttributeValueIndex(Integer ldapAttributeValueIndex) {
        this.ldapAttributeValueIndex = ldapAttributeValueIndex;
    }

    public net.project.hibernate.model.PnLdapDirectoryConfig getPnLdapDirectoryConfig() {
        return this.pnLdapDirectoryConfig;
    }

    public void setPnLdapDirectoryConfig(net.project.hibernate.model.PnLdapDirectoryConfig pnLdapDirectoryConfig) {
        this.pnLdapDirectoryConfig = pnLdapDirectoryConfig;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnLdapDirectoryAttrMap) ) return false;
        PnLdapDirectoryAttrMap castOther = (PnLdapDirectoryAttrMap) other;
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
