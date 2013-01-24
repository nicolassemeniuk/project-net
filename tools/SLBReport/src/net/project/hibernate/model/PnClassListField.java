package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassListField implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassListFieldPK comp_id;

    /** nullable persistent field */
    private Integer fieldWidth;

    /** nullable persistent field */
    private Integer fieldOrder;

    /** persistent field */
    private int wrapMode;

    /** nullable persistent field */
    private Integer isSubfield;

    /** nullable persistent field */
    private Integer isListField;

    /** persistent field */
    private int isSortField;

    /** nullable persistent field */
    private Integer sortOrder;

    /** nullable persistent field */
    private Integer sortAscending;

    /** nullable persistent field */
    private Integer isCalculateTotal;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClassList pnClassList;

    /** full constructor */
    public PnClassListField(net.project.hibernate.model.PnClassListFieldPK comp_id, Integer fieldWidth, Integer fieldOrder, int wrapMode, Integer isSubfield, Integer isListField, int isSortField, Integer sortOrder, Integer sortAscending, Integer isCalculateTotal, net.project.hibernate.model.PnClassList pnClassList) {
        this.comp_id = comp_id;
        this.fieldWidth = fieldWidth;
        this.fieldOrder = fieldOrder;
        this.wrapMode = wrapMode;
        this.isSubfield = isSubfield;
        this.isListField = isListField;
        this.isSortField = isSortField;
        this.sortOrder = sortOrder;
        this.sortAscending = sortAscending;
        this.isCalculateTotal = isCalculateTotal;
        this.pnClassList = pnClassList;
    }

    /** default constructor */
    public PnClassListField() {
    }

    /** minimal constructor */
    public PnClassListField(net.project.hibernate.model.PnClassListFieldPK comp_id, int wrapMode, int isSortField) {
        this.comp_id = comp_id;
        this.wrapMode = wrapMode;
        this.isSortField = isSortField;
    }

    public net.project.hibernate.model.PnClassListFieldPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassListFieldPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getFieldWidth() {
        return this.fieldWidth;
    }

    public void setFieldWidth(Integer fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public Integer getFieldOrder() {
        return this.fieldOrder;
    }

    public void setFieldOrder(Integer fieldOrder) {
        this.fieldOrder = fieldOrder;
    }

    public int getWrapMode() {
        return this.wrapMode;
    }

    public void setWrapMode(int wrapMode) {
        this.wrapMode = wrapMode;
    }

    public Integer getIsSubfield() {
        return this.isSubfield;
    }

    public void setIsSubfield(Integer isSubfield) {
        this.isSubfield = isSubfield;
    }

    public Integer getIsListField() {
        return this.isListField;
    }

    public void setIsListField(Integer isListField) {
        this.isListField = isListField;
    }

    public int getIsSortField() {
        return this.isSortField;
    }

    public void setIsSortField(int isSortField) {
        this.isSortField = isSortField;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortAscending() {
        return this.sortAscending;
    }

    public void setSortAscending(Integer sortAscending) {
        this.sortAscending = sortAscending;
    }

    public Integer getIsCalculateTotal() {
        return this.isCalculateTotal;
    }

    public void setIsCalculateTotal(Integer isCalculateTotal) {
        this.isCalculateTotal = isCalculateTotal;
    }

    public net.project.hibernate.model.PnClassList getPnClassList() {
        return this.pnClassList;
    }

    public void setPnClassList(net.project.hibernate.model.PnClassList pnClassList) {
        this.pnClassList = pnClassList;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassListField) ) return false;
        PnClassListField castOther = (PnClassListField) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
