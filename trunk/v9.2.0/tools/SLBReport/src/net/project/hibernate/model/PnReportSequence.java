package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnReportSequence implements Serializable {

    /** identifier field */
    private String reportType;

    /** nullable persistent field */
    private BigDecimal sequence;

    /** full constructor */
    public PnReportSequence(String reportType, BigDecimal sequence) {
        this.reportType = reportType;
        this.sequence = sequence;
    }

    /** default constructor */
    public PnReportSequence() {
    }

    /** minimal constructor */
    public PnReportSequence(String reportType) {
        this.reportType = reportType;
    }

    public String getReportType() {
        return this.reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public BigDecimal getSequence() {
        return this.sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("reportType", getReportType())
            .toString();
    }

}
