package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnWfRuleAuth implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnWfRuleAuthPK comp_id;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnWorkflowRule pnWorkflowRule;

    /** persistent field */
    private Set pnWfRuleAuthHasGroups;

    /** full constructor */
    public PnWfRuleAuth(net.project.hibernate.model.PnWfRuleAuthPK comp_id, Date crc, String recordStatus, net.project.hibernate.model.PnWorkflowRule pnWorkflowRule, Set pnWfRuleAuthHasGroups) {
        this.comp_id = comp_id;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWorkflowRule = pnWorkflowRule;
        this.pnWfRuleAuthHasGroups = pnWfRuleAuthHasGroups;
    }

    /** default constructor */
    public PnWfRuleAuth() {
    }

    /** minimal constructor */
    public PnWfRuleAuth(net.project.hibernate.model.PnWfRuleAuthPK comp_id, Date crc, String recordStatus, Set pnWfRuleAuthHasGroups) {
        this.comp_id = comp_id;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnWfRuleAuthHasGroups = pnWfRuleAuthHasGroups;
    }

    public net.project.hibernate.model.PnWfRuleAuthPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnWfRuleAuthPK comp_id) {
        this.comp_id = comp_id;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnWorkflowRule getPnWorkflowRule() {
        return this.pnWorkflowRule;
    }

    public void setPnWorkflowRule(net.project.hibernate.model.PnWorkflowRule pnWorkflowRule) {
        this.pnWorkflowRule = pnWorkflowRule;
    }

    public Set getPnWfRuleAuthHasGroups() {
        return this.pnWfRuleAuthHasGroups;
    }

    public void setPnWfRuleAuthHasGroups(Set pnWfRuleAuthHasGroups) {
        this.pnWfRuleAuthHasGroups = pnWfRuleAuthHasGroups;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnWfRuleAuth) ) return false;
        PnWfRuleAuth castOther = (PnWfRuleAuth) other;
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
