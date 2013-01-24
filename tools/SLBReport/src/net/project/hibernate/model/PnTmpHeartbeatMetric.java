package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTmpHeartbeatMetric implements Serializable {

    /** identifier field */
    private BigDecimal collectionId;

    /** nullable persistent field */
    private String moduleName;

    /** nullable persistent field */
    private Integer total;

    /** nullable persistent field */
    private Integer totalOpen;

    /** nullable persistent field */
    private Integer totalClosed;

    /** nullable persistent field */
    private Integer totalClosedLastWeek;

    /** full constructor */
    public PnTmpHeartbeatMetric(BigDecimal collectionId, String moduleName, Integer total, Integer totalOpen, Integer totalClosed, Integer totalClosedLastWeek) {
        this.collectionId = collectionId;
        this.moduleName = moduleName;
        this.total = total;
        this.totalOpen = totalOpen;
        this.totalClosed = totalClosed;
        this.totalClosedLastWeek = totalClosedLastWeek;
    }

    /** default constructor */
    public PnTmpHeartbeatMetric() {
    }

    /** minimal constructor */
    public PnTmpHeartbeatMetric(BigDecimal collectionId) {
        this.collectionId = collectionId;
    }

    public BigDecimal getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(BigDecimal collectionId) {
        this.collectionId = collectionId;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalOpen() {
        return this.totalOpen;
    }

    public void setTotalOpen(Integer totalOpen) {
        this.totalOpen = totalOpen;
    }

    public Integer getTotalClosed() {
        return this.totalClosed;
    }

    public void setTotalClosed(Integer totalClosed) {
        this.totalClosed = totalClosed;
    }

    public Integer getTotalClosedLastWeek() {
        return this.totalClosedLastWeek;
    }

    public void setTotalClosedLastWeek(Integer totalClosedLastWeek) {
        this.totalClosedLastWeek = totalClosedLastWeek;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("collectionId", getCollectionId())
            .toString();
    }

}
