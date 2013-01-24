package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpaceHasDirectoryField implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnSpaceHasDirectoryFieldPK comp_id;

    /** nullable persistent field */
    private Integer rowNum;

    /** nullable persistent field */
    private Integer columnNum;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDirectoryField pnDirectoryField;

    /** persistent field */
    private net.project.hibernate.model.PnDirectory pnDirectory;

    /** full constructor */
    public PnSpaceHasDirectoryField(net.project.hibernate.model.PnSpaceHasDirectoryFieldPK comp_id, Integer rowNum, Integer columnNum, net.project.hibernate.model.PnDirectoryField pnDirectoryField, net.project.hibernate.model.PnDirectory pnDirectory) {
        this.comp_id = comp_id;
        this.rowNum = rowNum;
        this.columnNum = columnNum;
        this.pnDirectoryField = pnDirectoryField;
        this.pnDirectory = pnDirectory;
    }

    /** default constructor */
    public PnSpaceHasDirectoryField() {
    }

    /** minimal constructor */
    public PnSpaceHasDirectoryField(net.project.hibernate.model.PnSpaceHasDirectoryFieldPK comp_id, net.project.hibernate.model.PnDirectory pnDirectory) {
        this.comp_id = comp_id;
        this.pnDirectory = pnDirectory;
    }

    public net.project.hibernate.model.PnSpaceHasDirectoryFieldPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnSpaceHasDirectoryFieldPK comp_id) {
        this.comp_id = comp_id;
    }

    public Integer getRowNum() {
        return this.rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getColumnNum() {
        return this.columnNum;
    }

    public void setColumnNum(Integer columnNum) {
        this.columnNum = columnNum;
    }

    public net.project.hibernate.model.PnDirectoryField getPnDirectoryField() {
        return this.pnDirectoryField;
    }

    public void setPnDirectoryField(net.project.hibernate.model.PnDirectoryField pnDirectoryField) {
        this.pnDirectoryField = pnDirectoryField;
    }

    public net.project.hibernate.model.PnDirectory getPnDirectory() {
        return this.pnDirectory;
    }

    public void setPnDirectory(net.project.hibernate.model.PnDirectory pnDirectory) {
        this.pnDirectory = pnDirectory;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnSpaceHasDirectoryField) ) return false;
        PnSpaceHasDirectoryField castOther = (PnSpaceHasDirectoryField) other;
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
