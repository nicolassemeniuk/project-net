package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLanguage implements Serializable {

    /** identifier field */
    private String languageCode;

    /** persistent field */
    private String languageName;

    /** persistent field */
    private String characterSet;

    /** persistent field */
    private int isActive;

    /** persistent field */
    private Set pnProperties;

    /** persistent field */
    private Set pnBrandSupportsLanguages;

    /** persistent field */
    private Set pnBrands;

    /** full constructor */
    public PnLanguage(String languageCode, String languageName, String characterSet, int isActive, Set pnProperties, Set pnBrandSupportsLanguages, Set pnBrands) {
        this.languageCode = languageCode;
        this.languageName = languageName;
        this.characterSet = characterSet;
        this.isActive = isActive;
        this.pnProperties = pnProperties;
        this.pnBrandSupportsLanguages = pnBrandSupportsLanguages;
        this.pnBrands = pnBrands;
    }

    /** default constructor */
    public PnLanguage() {
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getCharacterSet() {
        return this.characterSet;
    }

    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }

    public int getIsActive() {
        return this.isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Set getPnProperties() {
        return this.pnProperties;
    }

    public void setPnProperties(Set pnProperties) {
        this.pnProperties = pnProperties;
    }

    public Set getPnBrandSupportsLanguages() {
        return this.pnBrandSupportsLanguages;
    }

    public void setPnBrandSupportsLanguages(Set pnBrandSupportsLanguages) {
        this.pnBrandSupportsLanguages = pnBrandSupportsLanguages;
    }

    public Set getPnBrands() {
        return this.pnBrands;
    }

    public void setPnBrands(Set pnBrands) {
        this.pnBrands = pnBrands;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("languageCode", getLanguageCode())
            .toString();
    }

}
