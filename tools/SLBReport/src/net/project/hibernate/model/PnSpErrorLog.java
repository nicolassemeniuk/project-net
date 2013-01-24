package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpErrorLog implements Serializable {

    /** identifier field */
    private Date timestamp;

    /** identifier field */
    private String storedProcName;

    /** identifier field */
    private BigDecimal errorCode;

    /** identifier field */
    private String errorMsg;

    /** full constructor */
    public PnSpErrorLog(Date timestamp, String storedProcName, BigDecimal errorCode, String errorMsg) {
        this.timestamp = timestamp;
        this.storedProcName = storedProcName;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /** default constructor */
    public PnSpErrorLog() {
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStoredProcName() {
        return this.storedProcName;
    }

    public void setStoredProcName(String storedProcName) {
        this.storedProcName = storedProcName;
    }

    public BigDecimal getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(BigDecimal errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("timestamp", getTimestamp())
            .append("storedProcName", getStoredProcName())
            .append("errorCode", getErrorCode())
            .append("errorMsg", getErrorMsg())
            .toString();
    }

}
