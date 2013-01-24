package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTimeFormat implements Serializable {

    /** identifier field */
    private BigDecimal timeFormatId;

    /** persistent field */
    private String formatString;

    /** persistent field */
    private String display;

    /** nullable persistent field */
    private String example;

    /** full constructor */
    public PnTimeFormat(BigDecimal timeFormatId, String formatString, String display, String example) {
        this.timeFormatId = timeFormatId;
        this.formatString = formatString;
        this.display = display;
        this.example = example;
    }

    /** default constructor */
    public PnTimeFormat() {
    }

    /** minimal constructor */
    public PnTimeFormat(BigDecimal timeFormatId, String formatString, String display) {
        this.timeFormatId = timeFormatId;
        this.formatString = formatString;
        this.display = display;
    }

    public BigDecimal getTimeFormatId() {
        return this.timeFormatId;
    }

    public void setTimeFormatId(BigDecimal timeFormatId) {
        this.timeFormatId = timeFormatId;
    }

    public String getFormatString() {
        return this.formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public String getDisplay() {
        return this.display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getExample() {
        return this.example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("timeFormatId", getTimeFormatId())
            .toString();
    }

}
