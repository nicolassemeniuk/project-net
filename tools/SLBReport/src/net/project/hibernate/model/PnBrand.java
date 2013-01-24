package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBrand implements Serializable {

    /** identifier field */
    private BigDecimal brandId;

    /** persistent field */
    private String brandAbbrv;

    /** persistent field */
    private String brandName;

    /** nullable persistent field */
    private String brandDesc;

    /** persistent field */
    private int isSystemDefault;

    /** nullable persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnLanguage pnLanguage;

    /** persistent field */
    private Set pnBrandHasHosts;

    /** full constructor */
    public PnBrand(BigDecimal brandId, String brandAbbrv, String brandName, String brandDesc, int isSystemDefault, String recordStatus, net.project.hibernate.model.PnLanguage pnLanguage, Set pnBrandHasHosts) {
        this.brandId = brandId;
        this.brandAbbrv = brandAbbrv;
        this.brandName = brandName;
        this.brandDesc = brandDesc;
        this.isSystemDefault = isSystemDefault;
        this.recordStatus = recordStatus;
        this.pnLanguage = pnLanguage;
        this.pnBrandHasHosts = pnBrandHasHosts;
    }

    /** default constructor */
    public PnBrand() {
    }

    /** minimal constructor */
    public PnBrand(BigDecimal brandId, String brandAbbrv, String brandName, int isSystemDefault, net.project.hibernate.model.PnLanguage pnLanguage, Set pnBrandHasHosts) {
        this.brandId = brandId;
        this.brandAbbrv = brandAbbrv;
        this.brandName = brandName;
        this.isSystemDefault = isSystemDefault;
        this.pnLanguage = pnLanguage;
        this.pnBrandHasHosts = pnBrandHasHosts;
    }

    public BigDecimal getBrandId() {
        return this.brandId;
    }

    public void setBrandId(BigDecimal brandId) {
        this.brandId = brandId;
    }

    public String getBrandAbbrv() {
        return this.brandAbbrv;
    }

    public void setBrandAbbrv(String brandAbbrv) {
        this.brandAbbrv = brandAbbrv;
    }

    public String getBrandName() {
        return this.brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getBrandDesc() {
        return this.brandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        this.brandDesc = brandDesc;
    }

    public int getIsSystemDefault() {
        return this.isSystemDefault;
    }

    public void setIsSystemDefault(int isSystemDefault) {
        this.isSystemDefault = isSystemDefault;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnLanguage getPnLanguage() {
        return this.pnLanguage;
    }

    public void setPnLanguage(net.project.hibernate.model.PnLanguage pnLanguage) {
        this.pnLanguage = pnLanguage;
    }

    public Set getPnBrandHasHosts() {
        return this.pnBrandHasHosts;
    }

    public void setPnBrandHasHosts(Set pnBrandHasHosts) {
        this.pnBrandHasHosts = pnBrandHasHosts;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("brandId", getBrandId())
            .toString();
    }

}
