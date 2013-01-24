package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonPicksSpam implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonPicksSpamPK comp_id;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonSurvey pnPersonSurvey;

    /** nullable persistent field */
    private net.project.hibernate.model.PnSpamLookup pnSpamLookup;

    /** full constructor */
    public PnPersonPicksSpam(net.project.hibernate.model.PnPersonPicksSpamPK comp_id, net.project.hibernate.model.PnPersonSurvey pnPersonSurvey, net.project.hibernate.model.PnSpamLookup pnSpamLookup) {
        this.comp_id = comp_id;
        this.pnPersonSurvey = pnPersonSurvey;
        this.pnSpamLookup = pnSpamLookup;
    }

    /** default constructor */
    public PnPersonPicksSpam() {
    }

    /** minimal constructor */
    public PnPersonPicksSpam(net.project.hibernate.model.PnPersonPicksSpamPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonPicksSpamPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonPicksSpamPK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonSurvey getPnPersonSurvey() {
        return this.pnPersonSurvey;
    }

    public void setPnPersonSurvey(net.project.hibernate.model.PnPersonSurvey pnPersonSurvey) {
        this.pnPersonSurvey = pnPersonSurvey;
    }

    public net.project.hibernate.model.PnSpamLookup getPnSpamLookup() {
        return this.pnSpamLookup;
    }

    public void setPnSpamLookup(net.project.hibernate.model.PnSpamLookup pnSpamLookup) {
        this.pnSpamLookup = pnSpamLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonPicksSpam) ) return false;
        PnPersonPicksSpam castOther = (PnPersonPicksSpam) other;
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
