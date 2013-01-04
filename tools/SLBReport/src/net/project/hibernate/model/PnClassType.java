package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassType implements Serializable {

    /** identifier field */
    private BigDecimal classTypeId;

    /** persistent field */
    private String classTypeName;

    /** nullable persistent field */
    private String classTypeDesc;

    /** persistent field */
    private Set pnClasses;

    /** persistent field */
    private Set pnClassTypeElements;

    /** full constructor */
    public PnClassType(BigDecimal classTypeId, String classTypeName, String classTypeDesc, Set pnClasses, Set pnClassTypeElements) {
        this.classTypeId = classTypeId;
        this.classTypeName = classTypeName;
        this.classTypeDesc = classTypeDesc;
        this.pnClasses = pnClasses;
        this.pnClassTypeElements = pnClassTypeElements;
    }

    /** default constructor */
    public PnClassType() {
    }

    /** minimal constructor */
    public PnClassType(BigDecimal classTypeId, String classTypeName, Set pnClasses, Set pnClassTypeElements) {
        this.classTypeId = classTypeId;
        this.classTypeName = classTypeName;
        this.pnClasses = pnClasses;
        this.pnClassTypeElements = pnClassTypeElements;
    }

    public BigDecimal getClassTypeId() {
        return this.classTypeId;
    }

    public void setClassTypeId(BigDecimal classTypeId) {
        this.classTypeId = classTypeId;
    }

    public String getClassTypeName() {
        return this.classTypeName;
    }

    public void setClassTypeName(String classTypeName) {
        this.classTypeName = classTypeName;
    }

    public String getClassTypeDesc() {
        return this.classTypeDesc;
    }

    public void setClassTypeDesc(String classTypeDesc) {
        this.classTypeDesc = classTypeDesc;
    }

    public Set getPnClasses() {
        return this.pnClasses;
    }

    public void setPnClasses(Set pnClasses) {
        this.pnClasses = pnClasses;
    }

    public Set getPnClassTypeElements() {
        return this.pnClassTypeElements;
    }

    public void setPnClassTypeElements(Set pnClassTypeElements) {
        this.pnClassTypeElements = pnClassTypeElements;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classTypeId", getClassTypeId())
            .toString();
    }

}
