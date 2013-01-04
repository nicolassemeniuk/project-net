package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocHandler implements Serializable {

    /** identifier field */
    private BigDecimal docFormatId;

    /** identifier field */
    private String action;

    /** identifier field */
    private String actionHandler;

    /** identifier field */
    private Integer isDefault;

    /** full constructor */
    public PnDocHandler(BigDecimal docFormatId, String action, String actionHandler, Integer isDefault) {
        this.docFormatId = docFormatId;
        this.action = action;
        this.actionHandler = actionHandler;
        this.isDefault = isDefault;
    }

    /** default constructor */
    public PnDocHandler() {
    }

    public BigDecimal getDocFormatId() {
        return this.docFormatId;
    }

    public void setDocFormatId(BigDecimal docFormatId) {
        this.docFormatId = docFormatId;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionHandler() {
        return this.actionHandler;
    }

    public void setActionHandler(String actionHandler) {
        this.actionHandler = actionHandler;
    }

    public Integer getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docFormatId", getDocFormatId())
            .append("action", getAction())
            .append("actionHandler", getActionHandler())
            .append("isDefault", getIsDefault())
            .toString();
    }

}
