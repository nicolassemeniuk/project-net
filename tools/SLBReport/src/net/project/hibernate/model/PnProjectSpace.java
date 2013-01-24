package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnProjectSpace implements Serializable {

    /** identifier field */
    private BigDecimal projectId;

    /** nullable persistent field */
    private String projectDesc;

    /** persistent field */
    private String projectName;

    /** nullable persistent field */
    private BigDecimal statusCodeId;

    /** nullable persistent field */
    private BigDecimal colorCodeId;

    /** persistent field */
    private int isSubproject;

    /** nullable persistent field */
    private Integer percentComplete;

    /** nullable persistent field */
    private Date startDate;

    /** nullable persistent field */
    private Date endDate;

    /** nullable persistent field */
    private Date dateModified;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private String defaultCurrencyCode;

    /** nullable persistent field */
    private String sponsorDesc;

    /** persistent field */
    private BigDecimal improvementCodeId;

    /** nullable persistent field */
    private String currentStatusDescription;

    /** nullable persistent field */
    private BigDecimal financialStatusColorCodeId;

    /** persistent field */
    private BigDecimal financialStatusImpCodeId;

    /** nullable persistent field */
    private BigDecimal budgetedTotalCostValue;

    /** nullable persistent field */
    private String budgetedTotalCostCc;

    /** nullable persistent field */
    private BigDecimal currentEstTotalCostValue;

    /** nullable persistent field */
    private String currentEstTotalCostCc;

    /** nullable persistent field */
    private BigDecimal actualToDateCostValue;

    /** nullable persistent field */
    private String actualToDateCostCc;

    /** nullable persistent field */
    private BigDecimal estimatedRoiCostValue;

    /** nullable persistent field */
    private String estimatedRoiCostCc;

    /** nullable persistent field */
    private String costCenter;

    /** nullable persistent field */
    private BigDecimal scheduleStatusColorCodeId;

    /** persistent field */
    private BigDecimal scheduleStatusImpCodeId;

    /** nullable persistent field */
    private BigDecimal resourceStatusColorCodeId;

    /** persistent field */
    private BigDecimal resourceStatusImpCodeId;

    /** nullable persistent field */
    private BigDecimal priorityCodeId;

    /** nullable persistent field */
    private BigDecimal riskRatingCodeId;

    /** persistent field */
    private BigDecimal visibilityId;

    /** persistent field */
    private String percentCalculationMethod;

    /** persistent field */
    private int includesEveryone;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnDocument pnDocument;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private Set helpFeedbacks;

    /** full constructor */
    public PnProjectSpace(BigDecimal projectId, String projectDesc, String projectName, BigDecimal statusCodeId, BigDecimal colorCodeId, int isSubproject, Integer percentComplete, Date startDate, Date endDate, Date dateModified, Date crc, String recordStatus, String defaultCurrencyCode, String sponsorDesc, BigDecimal improvementCodeId, String currentStatusDescription, BigDecimal financialStatusColorCodeId, BigDecimal financialStatusImpCodeId, BigDecimal budgetedTotalCostValue, String budgetedTotalCostCc, BigDecimal currentEstTotalCostValue, String currentEstTotalCostCc, BigDecimal actualToDateCostValue, String actualToDateCostCc, BigDecimal estimatedRoiCostValue, String estimatedRoiCostCc, String costCenter, BigDecimal scheduleStatusColorCodeId, BigDecimal scheduleStatusImpCodeId, BigDecimal resourceStatusColorCodeId, BigDecimal resourceStatusImpCodeId, BigDecimal priorityCodeId, BigDecimal riskRatingCodeId, BigDecimal visibilityId, String percentCalculationMethod, int includesEveryone, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnPerson pnPerson, Set helpFeedbacks) {
        this.projectId = projectId;
        this.projectDesc = projectDesc;
        this.projectName = projectName;
        this.statusCodeId = statusCodeId;
        this.colorCodeId = colorCodeId;
        this.isSubproject = isSubproject;
        this.percentComplete = percentComplete;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateModified = dateModified;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.defaultCurrencyCode = defaultCurrencyCode;
        this.sponsorDesc = sponsorDesc;
        this.improvementCodeId = improvementCodeId;
        this.currentStatusDescription = currentStatusDescription;
        this.financialStatusColorCodeId = financialStatusColorCodeId;
        this.financialStatusImpCodeId = financialStatusImpCodeId;
        this.budgetedTotalCostValue = budgetedTotalCostValue;
        this.budgetedTotalCostCc = budgetedTotalCostCc;
        this.currentEstTotalCostValue = currentEstTotalCostValue;
        this.currentEstTotalCostCc = currentEstTotalCostCc;
        this.actualToDateCostValue = actualToDateCostValue;
        this.actualToDateCostCc = actualToDateCostCc;
        this.estimatedRoiCostValue = estimatedRoiCostValue;
        this.estimatedRoiCostCc = estimatedRoiCostCc;
        this.costCenter = costCenter;
        this.scheduleStatusColorCodeId = scheduleStatusColorCodeId;
        this.scheduleStatusImpCodeId = scheduleStatusImpCodeId;
        this.resourceStatusColorCodeId = resourceStatusColorCodeId;
        this.resourceStatusImpCodeId = resourceStatusImpCodeId;
        this.priorityCodeId = priorityCodeId;
        this.riskRatingCodeId = riskRatingCodeId;
        this.visibilityId = visibilityId;
        this.percentCalculationMethod = percentCalculationMethod;
        this.includesEveryone = includesEveryone;
        this.pnObject = pnObject;
        this.pnDocument = pnDocument;
        this.pnPerson = pnPerson;
        this.helpFeedbacks = helpFeedbacks;
    }

    /** default constructor */
    public PnProjectSpace() {
    }

    /** minimal constructor */
    public PnProjectSpace(BigDecimal projectId, String projectName, int isSubproject, Date crc, String recordStatus, String defaultCurrencyCode, BigDecimal improvementCodeId, BigDecimal financialStatusImpCodeId, BigDecimal scheduleStatusImpCodeId, BigDecimal resourceStatusImpCodeId, BigDecimal visibilityId, String percentCalculationMethod, int includesEveryone, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnPerson pnPerson, Set helpFeedbacks) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.isSubproject = isSubproject;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.defaultCurrencyCode = defaultCurrencyCode;
        this.improvementCodeId = improvementCodeId;
        this.financialStatusImpCodeId = financialStatusImpCodeId;
        this.scheduleStatusImpCodeId = scheduleStatusImpCodeId;
        this.resourceStatusImpCodeId = resourceStatusImpCodeId;
        this.visibilityId = visibilityId;
        this.percentCalculationMethod = percentCalculationMethod;
        this.includesEveryone = includesEveryone;
        this.pnDocument = pnDocument;
        this.pnPerson = pnPerson;
        this.helpFeedbacks = helpFeedbacks;
    }

    public BigDecimal getProjectId() {
        return this.projectId;
    }

    public void setProjectId(BigDecimal projectId) {
        this.projectId = projectId;
    }

    public String getProjectDesc() {
        return this.projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getStatusCodeId() {
        return this.statusCodeId;
    }

    public void setStatusCodeId(BigDecimal statusCodeId) {
        this.statusCodeId = statusCodeId;
    }

    public BigDecimal getColorCodeId() {
        return this.colorCodeId;
    }

    public void setColorCodeId(BigDecimal colorCodeId) {
        this.colorCodeId = colorCodeId;
    }

    public int getIsSubproject() {
        return this.isSubproject;
    }

    public void setIsSubproject(int isSubproject) {
        this.isSubproject = isSubproject;
    }

    public Integer getPercentComplete() {
        return this.percentComplete;
    }

    public void setPercentComplete(Integer percentComplete) {
        this.percentComplete = percentComplete;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
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

    public String getDefaultCurrencyCode() {
        return this.defaultCurrencyCode;
    }

    public void setDefaultCurrencyCode(String defaultCurrencyCode) {
        this.defaultCurrencyCode = defaultCurrencyCode;
    }

    public String getSponsorDesc() {
        return this.sponsorDesc;
    }

    public void setSponsorDesc(String sponsorDesc) {
        this.sponsorDesc = sponsorDesc;
    }

    public BigDecimal getImprovementCodeId() {
        return this.improvementCodeId;
    }

    public void setImprovementCodeId(BigDecimal improvementCodeId) {
        this.improvementCodeId = improvementCodeId;
    }

    public String getCurrentStatusDescription() {
        return this.currentStatusDescription;
    }

    public void setCurrentStatusDescription(String currentStatusDescription) {
        this.currentStatusDescription = currentStatusDescription;
    }

    public BigDecimal getFinancialStatusColorCodeId() {
        return this.financialStatusColorCodeId;
    }

    public void setFinancialStatusColorCodeId(BigDecimal financialStatusColorCodeId) {
        this.financialStatusColorCodeId = financialStatusColorCodeId;
    }

    public BigDecimal getFinancialStatusImpCodeId() {
        return this.financialStatusImpCodeId;
    }

    public void setFinancialStatusImpCodeId(BigDecimal financialStatusImpCodeId) {
        this.financialStatusImpCodeId = financialStatusImpCodeId;
    }

    public BigDecimal getBudgetedTotalCostValue() {
        return this.budgetedTotalCostValue;
    }

    public void setBudgetedTotalCostValue(BigDecimal budgetedTotalCostValue) {
        this.budgetedTotalCostValue = budgetedTotalCostValue;
    }

    public String getBudgetedTotalCostCc() {
        return this.budgetedTotalCostCc;
    }

    public void setBudgetedTotalCostCc(String budgetedTotalCostCc) {
        this.budgetedTotalCostCc = budgetedTotalCostCc;
    }

    public BigDecimal getCurrentEstTotalCostValue() {
        return this.currentEstTotalCostValue;
    }

    public void setCurrentEstTotalCostValue(BigDecimal currentEstTotalCostValue) {
        this.currentEstTotalCostValue = currentEstTotalCostValue;
    }

    public String getCurrentEstTotalCostCc() {
        return this.currentEstTotalCostCc;
    }

    public void setCurrentEstTotalCostCc(String currentEstTotalCostCc) {
        this.currentEstTotalCostCc = currentEstTotalCostCc;
    }

    public BigDecimal getActualToDateCostValue() {
        return this.actualToDateCostValue;
    }

    public void setActualToDateCostValue(BigDecimal actualToDateCostValue) {
        this.actualToDateCostValue = actualToDateCostValue;
    }

    public String getActualToDateCostCc() {
        return this.actualToDateCostCc;
    }

    public void setActualToDateCostCc(String actualToDateCostCc) {
        this.actualToDateCostCc = actualToDateCostCc;
    }

    public BigDecimal getEstimatedRoiCostValue() {
        return this.estimatedRoiCostValue;
    }

    public void setEstimatedRoiCostValue(BigDecimal estimatedRoiCostValue) {
        this.estimatedRoiCostValue = estimatedRoiCostValue;
    }

    public String getEstimatedRoiCostCc() {
        return this.estimatedRoiCostCc;
    }

    public void setEstimatedRoiCostCc(String estimatedRoiCostCc) {
        this.estimatedRoiCostCc = estimatedRoiCostCc;
    }

    public String getCostCenter() {
        return this.costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public BigDecimal getScheduleStatusColorCodeId() {
        return this.scheduleStatusColorCodeId;
    }

    public void setScheduleStatusColorCodeId(BigDecimal scheduleStatusColorCodeId) {
        this.scheduleStatusColorCodeId = scheduleStatusColorCodeId;
    }

    public BigDecimal getScheduleStatusImpCodeId() {
        return this.scheduleStatusImpCodeId;
    }

    public void setScheduleStatusImpCodeId(BigDecimal scheduleStatusImpCodeId) {
        this.scheduleStatusImpCodeId = scheduleStatusImpCodeId;
    }

    public BigDecimal getResourceStatusColorCodeId() {
        return this.resourceStatusColorCodeId;
    }

    public void setResourceStatusColorCodeId(BigDecimal resourceStatusColorCodeId) {
        this.resourceStatusColorCodeId = resourceStatusColorCodeId;
    }

    public BigDecimal getResourceStatusImpCodeId() {
        return this.resourceStatusImpCodeId;
    }

    public void setResourceStatusImpCodeId(BigDecimal resourceStatusImpCodeId) {
        this.resourceStatusImpCodeId = resourceStatusImpCodeId;
    }

    public BigDecimal getPriorityCodeId() {
        return this.priorityCodeId;
    }

    public void setPriorityCodeId(BigDecimal priorityCodeId) {
        this.priorityCodeId = priorityCodeId;
    }

    public BigDecimal getRiskRatingCodeId() {
        return this.riskRatingCodeId;
    }

    public void setRiskRatingCodeId(BigDecimal riskRatingCodeId) {
        this.riskRatingCodeId = riskRatingCodeId;
    }

    public BigDecimal getVisibilityId() {
        return this.visibilityId;
    }

    public void setVisibilityId(BigDecimal visibilityId) {
        this.visibilityId = visibilityId;
    }

    public String getPercentCalculationMethod() {
        return this.percentCalculationMethod;
    }

    public void setPercentCalculationMethod(String percentCalculationMethod) {
        this.percentCalculationMethod = percentCalculationMethod;
    }

    public int getIncludesEveryone() {
        return this.includesEveryone;
    }

    public void setIncludesEveryone(int includesEveryone) {
        this.includesEveryone = includesEveryone;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnDocument getPnDocument() {
        return this.pnDocument;
    }

    public void setPnDocument(net.project.hibernate.model.PnDocument pnDocument) {
        this.pnDocument = pnDocument;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public Set getHelpFeedbacks() {
        return this.helpFeedbacks;
    }

    public void setHelpFeedbacks(Set helpFeedbacks) {
        this.helpFeedbacks = helpFeedbacks;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("projectId", getProjectId())
            .toString();
    }

}
