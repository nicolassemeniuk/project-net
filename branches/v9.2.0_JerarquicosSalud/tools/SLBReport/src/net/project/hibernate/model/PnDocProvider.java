package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocProvider implements Serializable {

    /** identifier field */
    private BigDecimal docProviderId;

    /** nullable persistent field */
    private String docProviderName;

    /** nullable persistent field */
    private String docProviderDescription;

    /** persistent field */
    private int isDefault;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnDocProviderType pnDocProviderType;

    /** persistent field */
    private Set pnDocProviderHasDocSpaces;

    /** persistent field */
    private Set pnSpaceHasDocProviders;

    /** full constructor */
    public PnDocProvider(BigDecimal docProviderId, String docProviderName, String docProviderDescription, int isDefault, Date crc, String recordStatus, net.project.hibernate.model.PnDocProviderType pnDocProviderType, Set pnDocProviderHasDocSpaces, Set pnSpaceHasDocProviders) {
        this.docProviderId = docProviderId;
        this.docProviderName = docProviderName;
        this.docProviderDescription = docProviderDescription;
        this.isDefault = isDefault;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnDocProviderType = pnDocProviderType;
        this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
        this.pnSpaceHasDocProviders = pnSpaceHasDocProviders;
    }

    /** default constructor */
    public PnDocProvider() {
    }

    public PnDocProvider(BigDecimal docProviderId) {
   	 this.docProviderId = docProviderId;
   }
    
    /** minimal constructor */
    public PnDocProvider(BigDecimal docProviderId, int isDefault, Date crc, String recordStatus, net.project.hibernate.model.PnDocProviderType pnDocProviderType, Set pnDocProviderHasDocSpaces, Set pnSpaceHasDocProviders) {
        this.docProviderId = docProviderId;
        this.isDefault = isDefault;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnDocProviderType = pnDocProviderType;
        this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
        this.pnSpaceHasDocProviders = pnSpaceHasDocProviders;
    }

    public BigDecimal getDocProviderId() {
        return this.docProviderId;
    }

    public void setDocProviderId(BigDecimal docProviderId) {
        this.docProviderId = docProviderId;
    }

    public String getDocProviderName() {
        return this.docProviderName;
    }

    public void setDocProviderName(String docProviderName) {
        this.docProviderName = docProviderName;
    }

    public String getDocProviderDescription() {
        return this.docProviderDescription;
    }

    public void setDocProviderDescription(String docProviderDescription) {
        this.docProviderDescription = docProviderDescription;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnDocProviderType getPnDocProviderType() {
        return this.pnDocProviderType;
    }

    public void setPnDocProviderType(net.project.hibernate.model.PnDocProviderType pnDocProviderType) {
        this.pnDocProviderType = pnDocProviderType;
    }

    public Set getPnDocProviderHasDocSpaces() {
        return this.pnDocProviderHasDocSpaces;
    }

    public void setPnDocProviderHasDocSpaces(Set pnDocProviderHasDocSpaces) {
        this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
    }

    public Set getPnSpaceHasDocProviders() {
        return this.pnSpaceHasDocProviders;
    }

    public void setPnSpaceHasDocProviders(Set pnSpaceHasDocProviders) {
        this.pnSpaceHasDocProviders = pnSpaceHasDocProviders;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docProviderId", getDocProviderId())
            .toString();
    }

}
