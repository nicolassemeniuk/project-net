package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocProviderType implements Serializable {

    /** identifier field */
    private BigDecimal docProviderTypeId;

    /** nullable persistent field */
    private String docProviderTypeName;

    /** nullable persistent field */
    private String docProviderTypeDesc;

    /** persistent field */
    private Set pnDocProviders;

    /** full constructor */
    public PnDocProviderType(BigDecimal docProviderTypeId, String docProviderTypeName, String docProviderTypeDesc, Set pnDocProviders) {
        this.docProviderTypeId = docProviderTypeId;
        this.docProviderTypeName = docProviderTypeName;
        this.docProviderTypeDesc = docProviderTypeDesc;
        this.pnDocProviders = pnDocProviders;
    }

    /** default constructor */
    public PnDocProviderType() {
    }

    /** minimal constructor */
    public PnDocProviderType(BigDecimal docProviderTypeId, Set pnDocProviders) {
        this.docProviderTypeId = docProviderTypeId;
        this.pnDocProviders = pnDocProviders;
    }

    public BigDecimal getDocProviderTypeId() {
        return this.docProviderTypeId;
    }

    public void setDocProviderTypeId(BigDecimal docProviderTypeId) {
        this.docProviderTypeId = docProviderTypeId;
    }

    public String getDocProviderTypeName() {
        return this.docProviderTypeName;
    }

    public void setDocProviderTypeName(String docProviderTypeName) {
        this.docProviderTypeName = docProviderTypeName;
    }

    public String getDocProviderTypeDesc() {
        return this.docProviderTypeDesc;
    }

    public void setDocProviderTypeDesc(String docProviderTypeDesc) {
        this.docProviderTypeDesc = docProviderTypeDesc;
    }

    public Set getPnDocProviders() {
        return this.pnDocProviders;
    }

    public void setPnDocProviders(Set pnDocProviders) {
        this.pnDocProviders = pnDocProviders;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docProviderTypeId", getDocProviderTypeId())
            .toString();
    }

}
