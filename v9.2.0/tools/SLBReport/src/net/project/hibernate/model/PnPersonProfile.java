package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonProfile implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** nullable persistent field */
    private String prefixName;

    /** nullable persistent field */
    private String middleName;

    /** nullable persistent field */
    private String secondLastName;

    /** nullable persistent field */
    private String suffixName;

    /** nullable persistent field */
    private String companyName;

    /** nullable persistent field */
    private String companyDivision;

    /** nullable persistent field */
    private Integer jobDescriptionCode;

    /** persistent field */
    private BigDecimal addressId;

    /** nullable persistent field */
    private String languageCode;

    /** persistent field */
    private String timezoneCode;

    /** persistent field */
    private String personalSpaceName;

    /** nullable persistent field */
    private String verificationCode;

    /** nullable persistent field */
    private String alternateEmail1;

    /** nullable persistent field */
    private String alternateEmail2;

    /** nullable persistent field */
    private String alternateEmail3;

    /** nullable persistent field */
    private String localeCode;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** full constructor */
    public PnPersonProfile(BigDecimal personId, String prefixName, String middleName, String secondLastName, String suffixName, String companyName, String companyDivision, Integer jobDescriptionCode, BigDecimal addressId, String languageCode, String timezoneCode, String personalSpaceName, String verificationCode, String alternateEmail1, String alternateEmail2, String alternateEmail3, String localeCode, net.project.hibernate.model.PnPerson pnPerson) {
        this.personId = personId;
        this.prefixName = prefixName;
        this.middleName = middleName;
        this.secondLastName = secondLastName;
        this.suffixName = suffixName;
        this.companyName = companyName;
        this.companyDivision = companyDivision;
        this.jobDescriptionCode = jobDescriptionCode;
        this.addressId = addressId;
        this.languageCode = languageCode;
        this.timezoneCode = timezoneCode;
        this.personalSpaceName = personalSpaceName;
        this.verificationCode = verificationCode;
        this.alternateEmail1 = alternateEmail1;
        this.alternateEmail2 = alternateEmail2;
        this.alternateEmail3 = alternateEmail3;
        this.localeCode = localeCode;
        this.pnPerson = pnPerson;
    }

    /** default constructor */
    public PnPersonProfile() {
    }

    /** minimal constructor */
    public PnPersonProfile(BigDecimal personId, BigDecimal addressId, String timezoneCode, String personalSpaceName) {
        this.personId = personId;
        this.addressId = addressId;
        this.timezoneCode = timezoneCode;
        this.personalSpaceName = personalSpaceName;
    }
    
    public PnPersonProfile(BigDecimal personId, String prefixName, String middleName, String secondLastName,
    		String suffixName, String localeCode, String languageCode, String timezoneCode, String personalSpaceName, 
    		String verificationCode, BigDecimal addressId, String alternateEmail1, String alternateEmail2, String alternateEmail3) {
        this.personId = personId;
        this.prefixName = prefixName;
        this.middleName = middleName;
        this.secondLastName = secondLastName;
        this.suffixName = suffixName;
        this.localeCode = localeCode;
        this.addressId = addressId;
        this.languageCode = languageCode;
        this.timezoneCode = timezoneCode;
        this.personalSpaceName = personalSpaceName;
        this.verificationCode = verificationCode;
        this.alternateEmail1 = alternateEmail1;
        this.alternateEmail2 = alternateEmail2;
        this.alternateEmail3 = alternateEmail3;
    }


    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getPrefixName() {
        return this.prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondLastName() {
        return this.secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getSuffixName() {
        return this.suffixName;
    }

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyDivision() {
        return this.companyDivision;
    }

    public void setCompanyDivision(String companyDivision) {
        this.companyDivision = companyDivision;
    }

    public Integer getJobDescriptionCode() {
        return this.jobDescriptionCode;
    }

    public void setJobDescriptionCode(Integer jobDescriptionCode) {
        this.jobDescriptionCode = jobDescriptionCode;
    }

    public BigDecimal getAddressId() {
        return this.addressId;
    }

    public void setAddressId(BigDecimal addressId) {
        this.addressId = addressId;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTimezoneCode() {
        return this.timezoneCode;
    }

    public void setTimezoneCode(String timezoneCode) {
        this.timezoneCode = timezoneCode;
    }

    public String getPersonalSpaceName() {
        return this.personalSpaceName;
    }

    public void setPersonalSpaceName(String personalSpaceName) {
        this.personalSpaceName = personalSpaceName;
    }

    public String getVerificationCode() {
        return this.verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getAlternateEmail1() {
        return this.alternateEmail1;
    }

    public void setAlternateEmail1(String alternateEmail1) {
        this.alternateEmail1 = alternateEmail1;
    }

    public String getAlternateEmail2() {
        return this.alternateEmail2;
    }

    public void setAlternateEmail2(String alternateEmail2) {
        this.alternateEmail2 = alternateEmail2;
    }

    public String getAlternateEmail3() {
        return this.alternateEmail3;
    }

    public void setAlternateEmail3(String alternateEmail3) {
        this.alternateEmail3 = alternateEmail3;
    }

    public String getLocaleCode() {
        return this.localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .toString();
    }

}
