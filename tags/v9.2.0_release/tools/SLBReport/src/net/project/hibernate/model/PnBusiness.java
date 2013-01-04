package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBusiness implements Serializable {

    /** identifier field */
    private BigDecimal businessId;

    /** nullable persistent field */
    private String businessName;

    /** nullable persistent field */
    private String businessDesc;

    /** nullable persistent field */
    private String businessType;

    /** nullable persistent field */
    private BigDecimal logoImageId;

    /** persistent field */
    private int isLocal;

    /** nullable persistent field */
    private BigDecimal remoteHostId;

    /** nullable persistent field */
    private BigDecimal remoteBusinessId;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private int isMaster;

    /** nullable persistent field */
    private BigDecimal businessCategoryId;

    /** nullable persistent field */
    private BigDecimal brandId;

    /** nullable persistent field */
    private BigDecimal billingAccountId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnAddress pnAddress;

    /** persistent field */
    private Set pnBusinessSpaces;

    /** full constructor */
    public PnBusiness(BigDecimal businessId, String businessName, String businessDesc, String businessType, BigDecimal logoImageId, int isLocal, BigDecimal remoteHostId, BigDecimal remoteBusinessId, String recordStatus, int isMaster, BigDecimal businessCategoryId, BigDecimal brandId, BigDecimal billingAccountId, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnAddress pnAddress, Set pnBusinessSpaces) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.businessDesc = businessDesc;
        this.businessType = businessType;
        this.logoImageId = logoImageId;
        this.isLocal = isLocal;
        this.remoteHostId = remoteHostId;
        this.remoteBusinessId = remoteBusinessId;
        this.recordStatus = recordStatus;
        this.isMaster = isMaster;
        this.businessCategoryId = businessCategoryId;
        this.brandId = brandId;
        this.billingAccountId = billingAccountId;
        this.pnObject = pnObject;
        this.pnAddress = pnAddress;
        this.pnBusinessSpaces = pnBusinessSpaces;
    }

    /** default constructor */
    public PnBusiness() {
    }

    /** minimal constructor */
    public PnBusiness(BigDecimal businessId, int isLocal, String recordStatus, int isMaster, net.project.hibernate.model.PnAddress pnAddress, Set pnBusinessSpaces) {
        this.businessId = businessId;
        this.isLocal = isLocal;
        this.recordStatus = recordStatus;
        this.isMaster = isMaster;
        this.pnAddress = pnAddress;
        this.pnBusinessSpaces = pnBusinessSpaces;
    }

    public BigDecimal getBusinessId() {
        return this.businessId;
    }

    public void setBusinessId(BigDecimal businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return this.businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessDesc() {
        return this.businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public BigDecimal getLogoImageId() {
        return this.logoImageId;
    }

    public void setLogoImageId(BigDecimal logoImageId) {
        this.logoImageId = logoImageId;
    }

    public int getIsLocal() {
        return this.isLocal;
    }

    public void setIsLocal(int isLocal) {
        this.isLocal = isLocal;
    }

    public BigDecimal getRemoteHostId() {
        return this.remoteHostId;
    }

    public void setRemoteHostId(BigDecimal remoteHostId) {
        this.remoteHostId = remoteHostId;
    }

    public BigDecimal getRemoteBusinessId() {
        return this.remoteBusinessId;
    }

    public void setRemoteBusinessId(BigDecimal remoteBusinessId) {
        this.remoteBusinessId = remoteBusinessId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getIsMaster() {
        return this.isMaster;
    }

    public void setIsMaster(int isMaster) {
        this.isMaster = isMaster;
    }

    public BigDecimal getBusinessCategoryId() {
        return this.businessCategoryId;
    }

    public void setBusinessCategoryId(BigDecimal businessCategoryId) {
        this.businessCategoryId = businessCategoryId;
    }

    public BigDecimal getBrandId() {
        return this.brandId;
    }

    public void setBrandId(BigDecimal brandId) {
        this.brandId = brandId;
    }

    public BigDecimal getBillingAccountId() {
        return this.billingAccountId;
    }

    public void setBillingAccountId(BigDecimal billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnAddress getPnAddress() {
        return this.pnAddress;
    }

    public void setPnAddress(net.project.hibernate.model.PnAddress pnAddress) {
        this.pnAddress = pnAddress;
    }

    public Set getPnBusinessSpaces() {
        return this.pnBusinessSpaces;
    }

    public void setPnBusinessSpaces(Set pnBusinessSpaces) {
        this.pnBusinessSpaces = pnBusinessSpaces;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("businessId", getBusinessId())
            .toString();
    }

}
