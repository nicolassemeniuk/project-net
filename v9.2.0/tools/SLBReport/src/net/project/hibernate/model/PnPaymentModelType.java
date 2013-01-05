package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPaymentModelType implements Serializable {

    /** identifier field */
    private BigDecimal modelTypeId;

    /** persistent field */
    private String className;

    /** persistent field */
    private String description;

    /** persistent field */
    private Set pnPaymentModels;

    /** full constructor */
    public PnPaymentModelType(BigDecimal modelTypeId, String className, String description, Set pnPaymentModels) {
        this.modelTypeId = modelTypeId;
        this.className = className;
        this.description = description;
        this.pnPaymentModels = pnPaymentModels;
    }

    /** default constructor */
    public PnPaymentModelType() {
    }

    public BigDecimal getModelTypeId() {
        return this.modelTypeId;
    }

    public void setModelTypeId(BigDecimal modelTypeId) {
        this.modelTypeId = modelTypeId;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set getPnPaymentModels() {
        return this.pnPaymentModels;
    }

    public void setPnPaymentModels(Set pnPaymentModels) {
        this.pnPaymentModels = pnPaymentModels;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("modelTypeId", getModelTypeId())
            .toString();
    }

}
