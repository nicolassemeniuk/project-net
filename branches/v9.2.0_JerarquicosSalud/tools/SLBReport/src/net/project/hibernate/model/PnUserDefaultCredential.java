package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnUserDefaultCredential implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnUserDefaultCredentialPK comp_id;

    /** persistent field */
    private String password;

    /** persistent field */
    private String jogPhrase;

    /** persistent field */
    private String jogAnswer;

    /** full constructor */
    public PnUserDefaultCredential(net.project.hibernate.model.PnUserDefaultCredentialPK comp_id, String password, String jogPhrase, String jogAnswer) {
        this.comp_id = comp_id;
        this.password = password;
        this.jogPhrase = jogPhrase;
        this.jogAnswer = jogAnswer;
    }

    /** default constructor */
    public PnUserDefaultCredential() {
    }

    public net.project.hibernate.model.PnUserDefaultCredentialPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnUserDefaultCredentialPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJogPhrase() {
        return this.jogPhrase;
    }

    public void setJogPhrase(String jogPhrase) {
        this.jogPhrase = jogPhrase;
    }

    public String getJogAnswer() {
        return this.jogAnswer;
    }

    public void setJogAnswer(String jogAnswer) {
        this.jogAnswer = jogAnswer;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnUserDefaultCredential) ) return false;
        PnUserDefaultCredential castOther = (PnUserDefaultCredential) other;
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
