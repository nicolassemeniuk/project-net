package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroupType implements Serializable {

    /** identifier field */
    private BigDecimal groupTypeId;

    /** persistent field */
    private String className;

    /** persistent field */
    private Set pnGroups;

    /** full constructor */
    public PnGroupType(BigDecimal groupTypeId, String className, Set pnGroups) {
        this.groupTypeId = groupTypeId;
        this.className = className;
        this.pnGroups = pnGroups;
    }

    /** default constructor */
    public PnGroupType() {
    }

    public PnGroupType(BigDecimal groupTypeId) {
    	this.groupTypeId = groupTypeId;
    }
    public BigDecimal getGroupTypeId() {
        return this.groupTypeId;
    }

    public void setGroupTypeId(BigDecimal groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set getPnGroups() {
        return this.pnGroups;
    }

    public void setPnGroups(Set pnGroups) {
        this.pnGroups = pnGroups;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("groupTypeId", getGroupTypeId())
            .toString();
    }

}
