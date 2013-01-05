package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnJavaErrorLog implements Serializable {

    /** identifier field */
    private Date errorDate;

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private String errorName;

    /** identifier field */
    private String errorMessage;

    /** identifier field */
    private String stackTrace;

    /** identifier field */
    private String severity;

    /** full constructor */
    public PnJavaErrorLog(Date errorDate, BigDecimal personId, String errorName, String errorMessage, String stackTrace, String severity) {
        this.errorDate = errorDate;
        this.personId = personId;
        this.errorName = errorName;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        this.severity = severity;
    }

    /** default constructor */
    public PnJavaErrorLog() {
    }

    public Date getErrorDate() {
        return this.errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getErrorName() {
        return this.errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getSeverity() {
        return this.severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("errorDate", getErrorDate())
            .append("personId", getPersonId())
            .append("errorName", getErrorName())
            .append("errorMessage", getErrorMessage())
            .append("stackTrace", getStackTrace())
            .append("severity", getSeverity())
            .toString();
    }

}
