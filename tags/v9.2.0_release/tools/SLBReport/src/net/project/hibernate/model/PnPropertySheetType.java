package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPropertySheetType implements Serializable {

    /** identifier field */
    private Integer propertySheetType;

    /** persistent field */
    private String propertySheetName;

    /** nullable persistent field */
    private String propertySheetDesc;

    /** nullable persistent field */
    private String propertiesTableName;

    /** persistent field */
    private Set pnPropertySheets;

    /** full constructor */
    public PnPropertySheetType(Integer propertySheetType, String propertySheetName, String propertySheetDesc, String propertiesTableName, Set pnPropertySheets) {
        this.propertySheetType = propertySheetType;
        this.propertySheetName = propertySheetName;
        this.propertySheetDesc = propertySheetDesc;
        this.propertiesTableName = propertiesTableName;
        this.pnPropertySheets = pnPropertySheets;
    }

    /** default constructor */
    public PnPropertySheetType() {
    }

    /** minimal constructor */
    public PnPropertySheetType(Integer propertySheetType, String propertySheetName, Set pnPropertySheets) {
        this.propertySheetType = propertySheetType;
        this.propertySheetName = propertySheetName;
        this.pnPropertySheets = pnPropertySheets;
    }

    public Integer getPropertySheetType() {
        return this.propertySheetType;
    }

    public void setPropertySheetType(Integer propertySheetType) {
        this.propertySheetType = propertySheetType;
    }

    public String getPropertySheetName() {
        return this.propertySheetName;
    }

    public void setPropertySheetName(String propertySheetName) {
        this.propertySheetName = propertySheetName;
    }

    public String getPropertySheetDesc() {
        return this.propertySheetDesc;
    }

    public void setPropertySheetDesc(String propertySheetDesc) {
        this.propertySheetDesc = propertySheetDesc;
    }

    public String getPropertiesTableName() {
        return this.propertiesTableName;
    }

    public void setPropertiesTableName(String propertiesTableName) {
        this.propertiesTableName = propertiesTableName;
    }

    public Set getPnPropertySheets() {
        return this.pnPropertySheets;
    }

    public void setPnPropertySheets(Set pnPropertySheets) {
        this.pnPropertySheets = pnPropertySheets;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("propertySheetType", getPropertySheetType())
            .toString();
    }

}
