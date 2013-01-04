package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonSurvey implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** persistent field */
    private String spamAllowed;

    /** nullable persistent field */
    private String spamMethod;

    /** persistent field */
    private String modelvistaSource;

    /** persistent field */
    private String previousBentleyExp;

    /** persistent field */
    private String referralPage;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private Set pnPersonPicksSpams;

    /** full constructor */
    public PnPersonSurvey(BigDecimal personId, String spamAllowed, String spamMethod, String modelvistaSource, String previousBentleyExp, String referralPage, net.project.hibernate.model.PnPerson pnPerson, Set pnPersonPicksSpams) {
        this.personId = personId;
        this.spamAllowed = spamAllowed;
        this.spamMethod = spamMethod;
        this.modelvistaSource = modelvistaSource;
        this.previousBentleyExp = previousBentleyExp;
        this.referralPage = referralPage;
        this.pnPerson = pnPerson;
        this.pnPersonPicksSpams = pnPersonPicksSpams;
    }

    /** default constructor */
    public PnPersonSurvey() {
    }

    /** minimal constructor */
    public PnPersonSurvey(BigDecimal personId, String spamAllowed, String modelvistaSource, String previousBentleyExp, String referralPage, Set pnPersonPicksSpams) {
        this.personId = personId;
        this.spamAllowed = spamAllowed;
        this.modelvistaSource = modelvistaSource;
        this.previousBentleyExp = previousBentleyExp;
        this.referralPage = referralPage;
        this.pnPersonPicksSpams = pnPersonPicksSpams;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getSpamAllowed() {
        return this.spamAllowed;
    }

    public void setSpamAllowed(String spamAllowed) {
        this.spamAllowed = spamAllowed;
    }

    public String getSpamMethod() {
        return this.spamMethod;
    }

    public void setSpamMethod(String spamMethod) {
        this.spamMethod = spamMethod;
    }

    public String getModelvistaSource() {
        return this.modelvistaSource;
    }

    public void setModelvistaSource(String modelvistaSource) {
        this.modelvistaSource = modelvistaSource;
    }

    public String getPreviousBentleyExp() {
        return this.previousBentleyExp;
    }

    public void setPreviousBentleyExp(String previousBentleyExp) {
        this.previousBentleyExp = previousBentleyExp;
    }

    public String getReferralPage() {
        return this.referralPage;
    }

    public void setReferralPage(String referralPage) {
        this.referralPage = referralPage;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public Set getPnPersonPicksSpams() {
        return this.pnPersonPicksSpams;
    }

    public void setPnPersonPicksSpams(Set pnPersonPicksSpams) {
        this.pnPersonPicksSpams = pnPersonPicksSpams;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .toString();
    }

}
