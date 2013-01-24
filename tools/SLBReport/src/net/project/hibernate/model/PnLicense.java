package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLicense implements Serializable {

    /** identifier field */
    private BigDecimal licenseId;

    /** persistent field */
    private String licenseKeyValue;

    /** persistent field */
    private int isTrial;

    /** nullable persistent field */
    private Integer licenseStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnLicensePurchaser pnLicensePurchaser;

    /** persistent field */
    private net.project.hibernate.model.PnLicenseCertificate pnLicenseCertificate;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnLicenseStatusReason pnLicenseStatusReason;

    /** persistent field */
    private net.project.hibernate.model.PnPaymentInformation pnPaymentInformation;

    /** persistent field */
    private Set pnBills;

    /** persistent field */
    private Set pnPersonHasLicenses;

    /** full constructor */
    public PnLicense(BigDecimal licenseId, String licenseKeyValue, int isTrial, Integer licenseStatus, net.project.hibernate.model.PnLicensePurchaser pnLicensePurchaser, net.project.hibernate.model.PnLicenseCertificate pnLicenseCertificate, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnLicenseStatusReason pnLicenseStatusReason, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation, Set pnBills, Set pnPersonHasLicenses) {
        this.licenseId = licenseId;
        this.licenseKeyValue = licenseKeyValue;
        this.isTrial = isTrial;
        this.licenseStatus = licenseStatus;
        this.pnLicensePurchaser = pnLicensePurchaser;
        this.pnLicenseCertificate = pnLicenseCertificate;
        this.pnPerson = pnPerson;
        this.pnLicenseStatusReason = pnLicenseStatusReason;
        this.pnPaymentInformation = pnPaymentInformation;
        this.pnBills = pnBills;
        this.pnPersonHasLicenses = pnPersonHasLicenses;
    }

    /** default constructor */
    public PnLicense() {
    }

    /** minimal constructor */
    public PnLicense(BigDecimal licenseId, String licenseKeyValue, int isTrial, net.project.hibernate.model.PnLicenseCertificate pnLicenseCertificate, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnLicenseStatusReason pnLicenseStatusReason, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation, Set pnBills, Set pnPersonHasLicenses) {
        this.licenseId = licenseId;
        this.licenseKeyValue = licenseKeyValue;
        this.isTrial = isTrial;
        this.pnLicenseCertificate = pnLicenseCertificate;
        this.pnPerson = pnPerson;
        this.pnLicenseStatusReason = pnLicenseStatusReason;
        this.pnPaymentInformation = pnPaymentInformation;
        this.pnBills = pnBills;
        this.pnPersonHasLicenses = pnPersonHasLicenses;
    }

    public BigDecimal getLicenseId() {
        return this.licenseId;
    }

    public void setLicenseId(BigDecimal licenseId) {
        this.licenseId = licenseId;
    }

    public String getLicenseKeyValue() {
        return this.licenseKeyValue;
    }

    public void setLicenseKeyValue(String licenseKeyValue) {
        this.licenseKeyValue = licenseKeyValue;
    }

    public int getIsTrial() {
        return this.isTrial;
    }

    public void setIsTrial(int isTrial) {
        this.isTrial = isTrial;
    }

    public Integer getLicenseStatus() {
        return this.licenseStatus;
    }

    public void setLicenseStatus(Integer licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public net.project.hibernate.model.PnLicensePurchaser getPnLicensePurchaser() {
        return this.pnLicensePurchaser;
    }

    public void setPnLicensePurchaser(net.project.hibernate.model.PnLicensePurchaser pnLicensePurchaser) {
        this.pnLicensePurchaser = pnLicensePurchaser;
    }

    public net.project.hibernate.model.PnLicenseCertificate getPnLicenseCertificate() {
        return this.pnLicenseCertificate;
    }

    public void setPnLicenseCertificate(net.project.hibernate.model.PnLicenseCertificate pnLicenseCertificate) {
        this.pnLicenseCertificate = pnLicenseCertificate;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnLicenseStatusReason getPnLicenseStatusReason() {
        return this.pnLicenseStatusReason;
    }

    public void setPnLicenseStatusReason(net.project.hibernate.model.PnLicenseStatusReason pnLicenseStatusReason) {
        this.pnLicenseStatusReason = pnLicenseStatusReason;
    }

    public net.project.hibernate.model.PnPaymentInformation getPnPaymentInformation() {
        return this.pnPaymentInformation;
    }

    public void setPnPaymentInformation(net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.pnPaymentInformation = pnPaymentInformation;
    }

    public Set getPnBills() {
        return this.pnBills;
    }

    public void setPnBills(Set pnBills) {
        this.pnBills = pnBills;
    }

    public Set getPnPersonHasLicenses() {
        return this.pnPersonHasLicenses;
    }

    public void setPnPersonHasLicenses(Set pnPersonHasLicenses) {
        this.pnPersonHasLicenses = pnPersonHasLicenses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("licenseId", getLicenseId())
            .toString();
    }

}
