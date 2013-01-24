package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPlanVersion implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPlanVersionPK comp_id;

    /** persistent field */
    private String planName;

    /** nullable persistent field */
    private String planDesc;

    /** nullable persistent field */
    private Date dateStart;

    /** nullable persistent field */
    private Date dateEnd;

    /** nullable persistent field */
    private BigDecimal autocalculateTaskEndpoints;

    /** nullable persistent field */
    private BigDecimal overallocationWarning;

    /** nullable persistent field */
    private BigDecimal defaultCalendarId;

    /** nullable persistent field */
    private String timezoneId;

    /** nullable persistent field */
    private Date baselineStart;

    /** nullable persistent field */
    private Date baselineEnd;

    /** nullable persistent field */
    private Date modifiedDate;

    /** nullable persistent field */
    private BigDecimal modifiedBy;

    /** nullable persistent field */
    private BigDecimal baselineId;

    /** persistent field */
    private BigDecimal defaultTaskCalcTypeId;

    /** nullable persistent field */
    private Date earliestStartDate;

    /** nullable persistent field */
    private Date earliestFinishDate;

    /** nullable persistent field */
    private Date latestStartDate;

    /** nullable persistent field */
    private Date latestFinishDate;

    /** nullable persistent field */
    private String constraintTypeId;

    /** nullable persistent field */
    private Date constraintDate;

    /** full constructor */
    public PnPlanVersion(net.project.hibernate.model.PnPlanVersionPK comp_id, String planName, String planDesc, Date dateStart, Date dateEnd, BigDecimal autocalculateTaskEndpoints, BigDecimal overallocationWarning, BigDecimal defaultCalendarId, String timezoneId, Date baselineStart, Date baselineEnd, Date modifiedDate, BigDecimal modifiedBy, BigDecimal baselineId, BigDecimal defaultTaskCalcTypeId, Date earliestStartDate, Date earliestFinishDate, Date latestStartDate, Date latestFinishDate, String constraintTypeId, Date constraintDate) {
        this.comp_id = comp_id;
        this.planName = planName;
        this.planDesc = planDesc;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.autocalculateTaskEndpoints = autocalculateTaskEndpoints;
        this.overallocationWarning = overallocationWarning;
        this.defaultCalendarId = defaultCalendarId;
        this.timezoneId = timezoneId;
        this.baselineStart = baselineStart;
        this.baselineEnd = baselineEnd;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.baselineId = baselineId;
        this.defaultTaskCalcTypeId = defaultTaskCalcTypeId;
        this.earliestStartDate = earliestStartDate;
        this.earliestFinishDate = earliestFinishDate;
        this.latestStartDate = latestStartDate;
        this.latestFinishDate = latestFinishDate;
        this.constraintTypeId = constraintTypeId;
        this.constraintDate = constraintDate;
    }

    /** default constructor */
    public PnPlanVersion() {
    }

    /** minimal constructor */
    public PnPlanVersion(net.project.hibernate.model.PnPlanVersionPK comp_id, String planName, BigDecimal defaultTaskCalcTypeId) {
        this.comp_id = comp_id;
        this.planName = planName;
        this.defaultTaskCalcTypeId = defaultTaskCalcTypeId;
    }

    public net.project.hibernate.model.PnPlanVersionPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPlanVersionPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getPlanName() {
        return this.planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDesc() {
        return this.planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }

    public Date getDateStart() {
        return this.dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return this.dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public BigDecimal getAutocalculateTaskEndpoints() {
        return this.autocalculateTaskEndpoints;
    }

    public void setAutocalculateTaskEndpoints(BigDecimal autocalculateTaskEndpoints) {
        this.autocalculateTaskEndpoints = autocalculateTaskEndpoints;
    }

    public BigDecimal getOverallocationWarning() {
        return this.overallocationWarning;
    }

    public void setOverallocationWarning(BigDecimal overallocationWarning) {
        this.overallocationWarning = overallocationWarning;
    }

    public BigDecimal getDefaultCalendarId() {
        return this.defaultCalendarId;
    }

    public void setDefaultCalendarId(BigDecimal defaultCalendarId) {
        this.defaultCalendarId = defaultCalendarId;
    }

    public String getTimezoneId() {
        return this.timezoneId;
    }

    public void setTimezoneId(String timezoneId) {
        this.timezoneId = timezoneId;
    }

    public Date getBaselineStart() {
        return this.baselineStart;
    }

    public void setBaselineStart(Date baselineStart) {
        this.baselineStart = baselineStart;
    }

    public Date getBaselineEnd() {
        return this.baselineEnd;
    }

    public void setBaselineEnd(Date baselineEnd) {
        this.baselineEnd = baselineEnd;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public BigDecimal getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedBy(BigDecimal modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public BigDecimal getBaselineId() {
        return this.baselineId;
    }

    public void setBaselineId(BigDecimal baselineId) {
        this.baselineId = baselineId;
    }

    public BigDecimal getDefaultTaskCalcTypeId() {
        return this.defaultTaskCalcTypeId;
    }

    public void setDefaultTaskCalcTypeId(BigDecimal defaultTaskCalcTypeId) {
        this.defaultTaskCalcTypeId = defaultTaskCalcTypeId;
    }

    public Date getEarliestStartDate() {
        return this.earliestStartDate;
    }

    public void setEarliestStartDate(Date earliestStartDate) {
        this.earliestStartDate = earliestStartDate;
    }

    public Date getEarliestFinishDate() {
        return this.earliestFinishDate;
    }

    public void setEarliestFinishDate(Date earliestFinishDate) {
        this.earliestFinishDate = earliestFinishDate;
    }

    public Date getLatestStartDate() {
        return this.latestStartDate;
    }

    public void setLatestStartDate(Date latestStartDate) {
        this.latestStartDate = latestStartDate;
    }

    public Date getLatestFinishDate() {
        return this.latestFinishDate;
    }

    public void setLatestFinishDate(Date latestFinishDate) {
        this.latestFinishDate = latestFinishDate;
    }

    public String getConstraintTypeId() {
        return this.constraintTypeId;
    }

    public void setConstraintTypeId(String constraintTypeId) {
        this.constraintTypeId = constraintTypeId;
    }

    public Date getConstraintDate() {
        return this.constraintDate;
    }

    public void setConstraintDate(Date constraintDate) {
        this.constraintDate = constraintDate;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPlanVersion) ) return false;
        PnPlanVersion castOther = (PnPlanVersion) other;
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
