package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnCodeType implements Serializable {

    /** identifier field */
    private BigDecimal codeTypeId;

    /** nullable persistent field */
    private String codeTypeName;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private Set pnGlobalCodes;

    /** persistent field */
    private Set pnCustomCodes;

    /** full constructor */
    public PnCodeType(BigDecimal codeTypeId, String codeTypeName, String description, Set pnGlobalCodes, Set pnCustomCodes) {
        this.codeTypeId = codeTypeId;
        this.codeTypeName = codeTypeName;
        this.description = description;
        this.pnGlobalCodes = pnGlobalCodes;
        this.pnCustomCodes = pnCustomCodes;
    }

    /** default constructor */
    public PnCodeType() {
    }

    /** minimal constructor */
    public PnCodeType(BigDecimal codeTypeId, Set pnGlobalCodes, Set pnCustomCodes) {
        this.codeTypeId = codeTypeId;
        this.pnGlobalCodes = pnGlobalCodes;
        this.pnCustomCodes = pnCustomCodes;
    }

    public BigDecimal getCodeTypeId() {
        return this.codeTypeId;
    }

    public void setCodeTypeId(BigDecimal codeTypeId) {
        this.codeTypeId = codeTypeId;
    }

    public String getCodeTypeName() {
        return this.codeTypeName;
    }

    public void setCodeTypeName(String codeTypeName) {
        this.codeTypeName = codeTypeName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set getPnGlobalCodes() {
        return this.pnGlobalCodes;
    }

    public void setPnGlobalCodes(Set pnGlobalCodes) {
        this.pnGlobalCodes = pnGlobalCodes;
    }

    public Set getPnCustomCodes() {
        return this.pnCustomCodes;
    }

    public void setPnCustomCodes(Set pnCustomCodes) {
        this.pnCustomCodes = pnCustomCodes;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("codeTypeId", getCodeTypeId())
            .toString();
    }

}
