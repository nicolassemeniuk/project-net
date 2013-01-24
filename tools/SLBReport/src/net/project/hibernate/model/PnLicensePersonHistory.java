package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLicensePersonHistory implements Serializable {

    /** identifier field */
    private BigDecimal historyId;

    /** persistent field */
    private BigDecimal licenseId;

    /** persistent field */
    private BigDecimal personId;

    /** persistent field */
    private Date createdDatetime;

    /** full constructor */
    public PnLicensePersonHistory(BigDecimal historyId, BigDecimal licenseId, BigDecimal personId, Date createdDatetime) {
        this.historyId = historyId;
        this.licenseId = licenseId;
        this.personId = personId;
        this.createdDatetime = createdDatetime;
    }

    /** default constructor */
    public PnLicensePersonHistory() {
    }

    public BigDecimal getHistoryId() {
        return this.historyId;
    }

    public void setHistoryId(BigDecimal historyId) {
        this.historyId = historyId;
    }

    public BigDecimal getLicenseId() {
        return this.licenseId;
    }

    public void setLicenseId(BigDecimal licenseId) {
        this.licenseId = licenseId;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("historyId", getHistoryId())
            .toString();
    }

}
