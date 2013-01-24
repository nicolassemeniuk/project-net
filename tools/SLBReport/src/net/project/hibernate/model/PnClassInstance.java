package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassInstance implements Serializable {

    /** identifier field */
    private BigDecimal classInstanceId;

    /** persistent field */
    private BigDecimal classId;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private BigDecimal spaceId;

    /** full constructor */
    public PnClassInstance(BigDecimal classInstanceId, BigDecimal classId, Date crc, String recordStatus, BigDecimal spaceId) {
        this.classInstanceId = classInstanceId;
        this.classId = classId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.spaceId = spaceId;
    }

    /** default constructor */
    public PnClassInstance() {
    }

    public BigDecimal getClassInstanceId() {
        return this.classInstanceId;
    }

    public void setClassInstanceId(BigDecimal classInstanceId) {
        this.classInstanceId = classInstanceId;
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public BigDecimal getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(BigDecimal spaceId) {
        this.spaceId = spaceId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classInstanceId", getClassInstanceId())
            .toString();
    }

}
