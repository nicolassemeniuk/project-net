package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSecurityAction implements Serializable {

    /** identifier field */
    private BigDecimal actionId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private BigDecimal bitMask;

    /** persistent field */
    private Set pnObjectTypeSupportsActions;

    /** full constructor */
    public PnSecurityAction(BigDecimal actionId, String name, String description, BigDecimal bitMask, Set pnObjectTypeSupportsActions) {
        this.actionId = actionId;
        this.name = name;
        this.description = description;
        this.bitMask = bitMask;
        this.pnObjectTypeSupportsActions = pnObjectTypeSupportsActions;
    }

    /** default constructor */
    public PnSecurityAction() {
    }

    /** minimal constructor */
    public PnSecurityAction(BigDecimal actionId, String name, BigDecimal bitMask, Set pnObjectTypeSupportsActions) {
        this.actionId = actionId;
        this.name = name;
        this.bitMask = bitMask;
        this.pnObjectTypeSupportsActions = pnObjectTypeSupportsActions;
    }

    public BigDecimal getActionId() {
        return this.actionId;
    }

    public void setActionId(BigDecimal actionId) {
        this.actionId = actionId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBitMask() {
        return this.bitMask;
    }

    public void setBitMask(BigDecimal bitMask) {
        this.bitMask = bitMask;
    }

    public Set getPnObjectTypeSupportsActions() {
        return this.pnObjectTypeSupportsActions;
    }

    public void setPnObjectTypeSupportsActions(Set pnObjectTypeSupportsActions) {
        this.pnObjectTypeSupportsActions = pnObjectTypeSupportsActions;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("actionId", getActionId())
            .toString();
    }

}
