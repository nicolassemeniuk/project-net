package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnViewDefaultSettingPK implements Serializable {

    /** identifier field */
    private BigDecimal contextId;

    /** identifier field */
    private BigDecimal scenarioId;

    /** full constructor */
    public PnViewDefaultSettingPK(BigDecimal contextId, BigDecimal scenarioId) {
        this.contextId = contextId;
        this.scenarioId = scenarioId;
    }

    /** default constructor */
    public PnViewDefaultSettingPK() {
    }

    public BigDecimal getContextId() {
        return this.contextId;
    }

    public void setContextId(BigDecimal contextId) {
        this.contextId = contextId;
    }

    public BigDecimal getScenarioId() {
        return this.scenarioId;
    }

    public void setScenarioId(BigDecimal scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("contextId", getContextId())
            .append("scenarioId", getScenarioId())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnViewDefaultSettingPK) ) return false;
        PnViewDefaultSettingPK castOther = (PnViewDefaultSettingPK) other;
        return new EqualsBuilder()
            .append(this.getContextId(), castOther.getContextId())
            .append(this.getScenarioId(), castOther.getScenarioId())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getContextId())
            .append(getScenarioId())
            .toHashCode();
    }

}
