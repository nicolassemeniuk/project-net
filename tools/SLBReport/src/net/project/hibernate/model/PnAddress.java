package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnAddress implements Serializable {

    /** identifier field */
    private BigDecimal addressId;

    /** nullable persistent field */
    private String address1;

    /** nullable persistent field */
    private String address2;

    /** nullable persistent field */
    private String address3;

    /** nullable persistent field */
    private String address4;

    /** nullable persistent field */
    private String address5;

    /** nullable persistent field */
    private String address6;

    /** nullable persistent field */
    private String address7;

    /** nullable persistent field */
    private String city;

    /** nullable persistent field */
    private String cityDistrict;

    /** nullable persistent field */
    private String region;

    /** nullable persistent field */
    private String stateProvence;

    /** nullable persistent field */
    private String zipcode;

    /** nullable persistent field */
    private String officePhone;

    /** nullable persistent field */
    private String faxPhone;

    /** nullable persistent field */
    private String homePhone;

    /** nullable persistent field */
    private String mobilePhone;

    /** nullable persistent field */
    private String pagerPhone;

    /** nullable persistent field */
    private String pagerEmail;

    /** nullable persistent field */
    private String websiteUrl;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnCountryLookup pnCountryLookup;

    /** persistent field */
    private Set pnFacilities;

    /** persistent field */
    private Set pnBusinesses;

    
    public PnAddress(BigDecimal addressId, String address1, String address2, String address3, String address4,
    		String address5, String address6, String address7, String city, String cityDistrict, String region, 
    		String stateProvence,  PnCountryLookup pnCountryLookup, String zipcode, String officePhone, String faxPhone,     		
    		String recordStatus) {
        this.addressId = addressId;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.address6 = address6;
        this.address7 = address7;
        this.city = city;
        this.cityDistrict = cityDistrict;
        this.region = region;
        this.stateProvence = stateProvence;
        this.zipcode = zipcode;
        this.pnCountryLookup = pnCountryLookup;
        this.officePhone = officePhone;
        this.faxPhone = faxPhone;
        this.recordStatus = recordStatus;
    }
    
    /** full constructor */
    public PnAddress(BigDecimal addressId, String address1, String address2, String address3, String address4, String address5, String address6, String address7, String city, String cityDistrict, String region, String stateProvence, String zipcode, String officePhone, String faxPhone, String homePhone, String mobilePhone, String pagerPhone, String pagerEmail, String websiteUrl, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnCountryLookup pnCountryLookup, Set pnFacilities, Set pnBusinesses) {
        this.addressId = addressId;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.address6 = address6;
        this.address7 = address7;
        this.city = city;
        this.cityDistrict = cityDistrict;
        this.region = region;
        this.stateProvence = stateProvence;
        this.zipcode = zipcode;
        this.officePhone = officePhone;
        this.faxPhone = faxPhone;
        this.homePhone = homePhone;
        this.mobilePhone = mobilePhone;
        this.pagerPhone = pagerPhone;
        this.pagerEmail = pagerEmail;
        this.websiteUrl = websiteUrl;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnCountryLookup = pnCountryLookup;
        this.pnFacilities = pnFacilities;
        this.pnBusinesses = pnBusinesses;
    }

    /** default constructor */
    public PnAddress() {
    }

    /** minimal constructor */
    public PnAddress(BigDecimal addressId, String recordStatus, net.project.hibernate.model.PnCountryLookup pnCountryLookup, Set pnFacilities, Set pnBusinesses) {
        this.addressId = addressId;
        this.recordStatus = recordStatus;
        this.pnCountryLookup = pnCountryLookup;
        this.pnFacilities = pnFacilities;
        this.pnBusinesses = pnBusinesses;
    }

    public BigDecimal getAddressId() {
        return this.addressId;
    }

    public void setAddressId(BigDecimal addressId) {
        this.addressId = addressId;
    }

    public String getAddress1() {
        return this.address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return this.address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return this.address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getAddress5() {
        return this.address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public String getAddress6() {
        return this.address6;
    }

    public void setAddress6(String address6) {
        this.address6 = address6;
    }

    public String getAddress7() {
        return this.address7;
    }

    public void setAddress7(String address7) {
        this.address7 = address7;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityDistrict() {
        return this.cityDistrict;
    }

    public void setCityDistrict(String cityDistrict) {
        this.cityDistrict = cityDistrict;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getStateProvence() {
        return this.stateProvence;
    }

    public void setStateProvence(String stateProvence) {
        this.stateProvence = stateProvence;
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getOfficePhone() {
        return this.officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getFaxPhone() {
        return this.faxPhone;
    }

    public void setFaxPhone(String faxPhone) {
        this.faxPhone = faxPhone;
    }

    public String getHomePhone() {
        return this.homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return this.mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPagerPhone() {
        return this.pagerPhone;
    }

    public void setPagerPhone(String pagerPhone) {
        this.pagerPhone = pagerPhone;
    }

    public String getPagerEmail() {
        return this.pagerEmail;
    }

    public void setPagerEmail(String pagerEmail) {
        this.pagerEmail = pagerEmail;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnCountryLookup getPnCountryLookup() {
        return this.pnCountryLookup;
    }

    public void setPnCountryLookup(net.project.hibernate.model.PnCountryLookup pnCountryLookup) {
        this.pnCountryLookup = pnCountryLookup;
    }

    public Set getPnFacilities() {
        return this.pnFacilities;
    }

    public void setPnFacilities(Set pnFacilities) {
        this.pnFacilities = pnFacilities;
    }

    public Set getPnBusinesses() {
        return this.pnBusinesses;
    }

    public void setPnBusinesses(Set pnBusinesses) {
        this.pnBusinesses = pnBusinesses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("addressId", getAddressId())
            .toString();
    }

}
