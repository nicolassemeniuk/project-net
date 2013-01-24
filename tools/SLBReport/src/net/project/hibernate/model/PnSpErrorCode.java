package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpErrorCode implements Serializable {

    /** identifier field */
    private BigDecimal errorCode;

    /** persistent field */
    private String errorName;

    /** nullable persistent field */
    private String errorDescription;

    /** full constructor */
    public PnSpErrorCode(BigDecimal errorCode, String errorName, String errorDescription) {
        this.errorCode = errorCode;
        this.errorName = errorName;
        this.errorDescription = errorDescription;
    }

    /** default constructor */
    public PnSpErrorCode() {
    }

    /** minimal constructor */
    public PnSpErrorCode(BigDecimal errorCode, String errorName) {
        this.errorCode = errorCode;
        this.errorName = errorName;
    }

    public BigDecimal getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(BigDecimal errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorName() {
        return this.errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("errorCode", getErrorCode())
            .toString();
    }

}
