package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasLicense implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnLicense pnLicense;

    /** full constructor */
    public PnPersonHasLicense(BigDecimal personId, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnLicense pnLicense) {
        this.personId = personId;
        this.pnPerson = pnPerson;
        this.pnLicense = pnLicense;
    }

    /** default constructor */
    public PnPersonHasLicense() {
    }

    /** minimal constructor */
    public PnPersonHasLicense(BigDecimal personId, net.project.hibernate.model.PnLicense pnLicense) {
        this.personId = personId;
        this.pnLicense = pnLicense;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnLicense getPnLicense() {
        return this.pnLicense;
    }

    public void setPnLicense(net.project.hibernate.model.PnLicense pnLicense) {
        this.pnLicense = pnLicense;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .toString();
    }

}
