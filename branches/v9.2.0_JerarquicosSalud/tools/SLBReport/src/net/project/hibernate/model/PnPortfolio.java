package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPortfolio implements Serializable {

    /** identifier field */
    private BigDecimal portfolioId;

    /** persistent field */
    private String portfolioName;

    /** nullable persistent field */
    private String portfolioDesc;

    /** nullable persistent field */
    private String portfolioType;

    /** nullable persistent field */
    private String contentType;

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private Set pnPortfolioHasConfigurations;

    /** persistent field */
    private Set pnPortfolioHasSpaces;

    /** persistent field */
    private Set pnSpaceHasPortfolios;

    /** full constructor */
    public PnPortfolio(BigDecimal portfolioId, String portfolioName, String portfolioDesc, String portfolioType, String contentType, String recordStatus, net.project.hibernate.model.PnObject pnObject, Set pnPortfolioHasConfigurations, Set pnPortfolioHasSpaces, Set pnSpaceHasPortfolios) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.portfolioDesc = portfolioDesc;
        this.portfolioType = portfolioType;
        this.contentType = contentType;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnPortfolioHasConfigurations = pnPortfolioHasConfigurations;
        this.pnPortfolioHasSpaces = pnPortfolioHasSpaces;
        this.pnSpaceHasPortfolios = pnSpaceHasPortfolios;
    }

    /** default constructor */
    public PnPortfolio() {
    }

    /** minimal constructor */
    public PnPortfolio(BigDecimal portfolioId, String portfolioName, Set pnPortfolioHasConfigurations, Set pnPortfolioHasSpaces, Set pnSpaceHasPortfolios) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.pnPortfolioHasConfigurations = pnPortfolioHasConfigurations;
        this.pnPortfolioHasSpaces = pnPortfolioHasSpaces;
        this.pnSpaceHasPortfolios = pnSpaceHasPortfolios;
    }

    public PnPortfolio(BigDecimal portfolioId, String portfolioName, String portfolioDesc) {
        this.portfolioId = portfolioId;
        this.portfolioName = portfolioName;
        this.portfolioDesc = portfolioDesc;
    }
    
    public BigDecimal getPortfolioId() {
        return this.portfolioId;
    }

    public void setPortfolioId(BigDecimal portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return this.portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getPortfolioDesc() {
        return this.portfolioDesc;
    }

    public void setPortfolioDesc(String portfolioDesc) {
        this.portfolioDesc = portfolioDesc;
    }

    public String getPortfolioType() {
        return this.portfolioType;
    }

    public void setPortfolioType(String portfolioType) {
        this.portfolioType = portfolioType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public Set getPnPortfolioHasConfigurations() {
        return this.pnPortfolioHasConfigurations;
    }

    public void setPnPortfolioHasConfigurations(Set pnPortfolioHasConfigurations) {
        this.pnPortfolioHasConfigurations = pnPortfolioHasConfigurations;
    }

    public Set getPnPortfolioHasSpaces() {
        return this.pnPortfolioHasSpaces;
    }

    public void setPnPortfolioHasSpaces(Set pnPortfolioHasSpaces) {
        this.pnPortfolioHasSpaces = pnPortfolioHasSpaces;
    }

    public Set getPnSpaceHasPortfolios() {
        return this.pnSpaceHasPortfolios;
    }

    public void setPnSpaceHasPortfolios(Set pnSpaceHasPortfolios) {
        this.pnSpaceHasPortfolios = pnSpaceHasPortfolios;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("portfolioId", getPortfolioId())
            .toString();
    }

}
