package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPropertySheet implements Serializable {

    /** identifier field */
    private BigDecimal propertySheetId;

    /** nullable persistent field */
    private BigDecimal propertyGroupId;

    /** persistent field */
    private net.project.hibernate.model.PnPropertySheetType pnPropertySheetType;

    /** persistent field */
    private Set pnSpaceHasPropertySheets;

    /** full constructor */
    public PnPropertySheet(BigDecimal propertySheetId, BigDecimal propertyGroupId, net.project.hibernate.model.PnPropertySheetType pnPropertySheetType, Set pnSpaceHasPropertySheets) {
        this.propertySheetId = propertySheetId;
        this.propertyGroupId = propertyGroupId;
        this.pnPropertySheetType = pnPropertySheetType;
        this.pnSpaceHasPropertySheets = pnSpaceHasPropertySheets;
    }

    /** default constructor */
    public PnPropertySheet() {
    }

    /** minimal constructor */
    public PnPropertySheet(BigDecimal propertySheetId, net.project.hibernate.model.PnPropertySheetType pnPropertySheetType, Set pnSpaceHasPropertySheets) {
        this.propertySheetId = propertySheetId;
        this.pnPropertySheetType = pnPropertySheetType;
        this.pnSpaceHasPropertySheets = pnSpaceHasPropertySheets;
    }

    public BigDecimal getPropertySheetId() {
        return this.propertySheetId;
    }

    public void setPropertySheetId(BigDecimal propertySheetId) {
        this.propertySheetId = propertySheetId;
    }

    public BigDecimal getPropertyGroupId() {
        return this.propertyGroupId;
    }

    public void setPropertyGroupId(BigDecimal propertyGroupId) {
        this.propertyGroupId = propertyGroupId;
    }

    public net.project.hibernate.model.PnPropertySheetType getPnPropertySheetType() {
        return this.pnPropertySheetType;
    }

    public void setPnPropertySheetType(net.project.hibernate.model.PnPropertySheetType pnPropertySheetType) {
        this.pnPropertySheetType = pnPropertySheetType;
    }

    public Set getPnSpaceHasPropertySheets() {
        return this.pnSpaceHasPropertySheets;
    }

    public void setPnSpaceHasPropertySheets(Set pnSpaceHasPropertySheets) {
        this.pnSpaceHasPropertySheets = pnSpaceHasPropertySheets;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("propertySheetId", getPropertySheetId())
            .toString();
    }

}
