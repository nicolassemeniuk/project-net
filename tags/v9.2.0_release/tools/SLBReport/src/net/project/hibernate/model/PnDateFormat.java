package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDateFormat implements Serializable {

    /** identifier field */
    private BigDecimal dateFormatId;

    /** persistent field */
    private String formatString;

    /** persistent field */
    private String display;

    /** nullable persistent field */
    private String example;

    /** full constructor */
    public PnDateFormat(BigDecimal dateFormatId, String formatString, String display, String example) {
        this.dateFormatId = dateFormatId;
        this.formatString = formatString;
        this.display = display;
        this.example = example;
    }

    /** default constructor */
    public PnDateFormat() {
    }

    /** minimal constructor */
    public PnDateFormat(BigDecimal dateFormatId, String formatString, String display) {
        this.dateFormatId = dateFormatId;
        this.formatString = formatString;
        this.display = display;
    }

    public BigDecimal getDateFormatId() {
        return this.dateFormatId;
    }

    public void setDateFormatId(BigDecimal dateFormatId) {
        this.dateFormatId = dateFormatId;
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
            .append("dateFormatId", getDateFormatId())
            .toString();
    }

}
