package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLicensePurchaser implements Serializable {

    /** identifier field */
    private BigDecimal licenseId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnLicense pnLicense;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnLicensePurchaser(BigDecimal licenseId, net.project.hibernate.model.PnLicense pnLicense, net.project.hibernate.model.PnPerson pnPerson) {
        this.licenseId = licenseId;
        this.pnLicense = pnLicense;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnLicensePurchaser() {
    }

    /** minimal constructor */
    public PnLicensePurchaser(BigDecimal licenseId, net.project.hibernate.model.PnPerson pnPerson) {
        this.licenseId = licenseId;
        this.pnPerson = pnPerson;
    }

    public BigDecimal getLicenseId() {
        return this.licenseId;
    }

    public void setLicenseId(BigDecimal licenseId) {
        this.licenseId = licenseId;
    }

    public net.project.hibernate.model.PnLicense getPnLicense() {
        return this.pnLicense;
    }

    public void setPnLicense(net.project.hibernate.model.PnLicense pnLicense) {
        this.pnLicense = pnLicense;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("licenseId", getLicenseId())
            .toString();
    }

}
