package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBusinessSpace implements Serializable {

    /** identifier field */
    private BigDecimal businessSpaceId;

    /** nullable persistent field */
    private BigDecimal spaceType;

    /** nullable persistent field */
    private BigDecimal completePortfolioId;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private int includesEveryone;

    /** persistent field */
    private net.project.hibernate.model.PnBusiness pnBusiness;

    /** full constructor */
    public PnBusinessSpace(BigDecimal businessSpaceId, BigDecimal spaceType, BigDecimal completePortfolioId, String recordStatus, int includesEveryone, net.project.hibernate.model.PnBusiness pnBusiness) {
        this.businessSpaceId = businessSpaceId;
        this.spaceType = spaceType;
        this.completePortfolioId = completePortfolioId;
        this.recordStatus = recordStatus;
        this.includesEveryone = includesEveryone;
        this.pnBusiness = pnBusiness;
    }

    /** default constructor */
    public PnBusinessSpace() {
    }

    /** minimal constructor */
    public PnBusinessSpace(BigDecimal businessSpaceId, String recordStatus, int includesEveryone, net.project.hibernate.model.PnBusiness pnBusiness) {
        this.businessSpaceId = businessSpaceId;
        this.recordStatus = recordStatus;
        this.includesEveryone = includesEveryone;
        this.pnBusiness = pnBusiness;
    }

    public BigDecimal getBusinessSpaceId() {
        return this.businessSpaceId;
    }

    public void setBusinessSpaceId(BigDecimal businessSpaceId) {
        this.businessSpaceId = businessSpaceId;
    }

    public BigDecimal getSpaceType() {
        return this.spaceType;
    }

    public void setSpaceType(BigDecimal spaceType) {
        this.spaceType = spaceType;
    }

    public BigDecimal getCompletePortfolioId() {
        return this.completePortfolioId;
    }

    public void setCompletePortfolioId(BigDecimal completePortfolioId) {
        this.completePortfolioId = completePortfolioId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public int getIncludesEveryone() {
        return this.includesEveryone;
    }

    public void setIncludesEveryone(int includesEveryone) {
        this.includesEveryone = includesEveryone;
    }

    public net.project.hibernate.model.PnBusiness getPnBusiness() {
        return this.pnBusiness;
    }

    public void setPnBusiness(net.project.hibernate.model.PnBusiness pnBusiness) {
        this.pnBusiness = pnBusiness;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("businessSpaceId", getBusinessSpaceId())
            .toString();
    }

}
