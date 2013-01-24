package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTimezoneLookup implements Serializable {

    /** identifier field */
    private String timezoneCode;

    /** nullable persistent field */
    private String timezoneDescription;

    /** persistent field */
    private String gmtOffset;

    /** full constructor */
    public PnTimezoneLookup(String timezoneCode, String timezoneDescription, String gmtOffset) {
        this.timezoneCode = timezoneCode;
        this.timezoneDescription = timezoneDescription;
        this.gmtOffset = gmtOffset;
    }

    /** default constructor */
    public PnTimezoneLookup() {
    }

    /** minimal constructor */
    public PnTimezoneLookup(String timezoneCode, String gmtOffset) {
        this.timezoneCode = timezoneCode;
        this.gmtOffset = gmtOffset;
    }

    public String getTimezoneCode() {
        return this.timezoneCode;
    }

    public void setTimezoneCode(String timezoneCode) {
        this.timezoneCode = timezoneCode;
    }

    public String getTimezoneDescription() {
        return this.timezoneDescription;
    }

    public void setTimezoneDescription(String timezoneDescription) {
        this.timezoneDescription = timezoneDescription;
    }

    public String getGmtOffset() {
        return this.gmtOffset;
    }

    public void setGmtOffset(String gmtOffset) {
        this.gmtOffset = gmtOffset;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("timezoneCode", getTimezoneCode())
            .toString();
    }

}
