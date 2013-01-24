package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTmpDocPathInfo implements Serializable {

    /** identifier field */
    private BigDecimal collectionId;

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private String objectName;

    /** identifier field */
    private BigDecimal parentId;

    /** identifier field */
    private String parentName;

    /** identifier field */
    private Integer isRoot;

    /** full constructor */
    public PnTmpDocPathInfo(BigDecimal collectionId, BigDecimal objectId, String objectName, BigDecimal parentId, String parentName, Integer isRoot) {
        this.collectionId = collectionId;
        this.objectId = objectId;
        this.objectName = objectName;
        this.parentId = parentId;
        this.parentName = parentName;
        this.isRoot = isRoot;
    }

    /** default constructor */
    public PnTmpDocPathInfo() {
    }

    public BigDecimal getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(BigDecimal collectionId) {
        this.collectionId = collectionId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public BigDecimal getParentId() {
        return this.parentId;
    }

    public void setParentId(BigDecimal parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return this.parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getIsRoot() {
        return this.isRoot;
    }

    public void setIsRoot(Integer isRoot) {
        this.isRoot = isRoot;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("collectionId", getCollectionId())
            .append("objectId", getObjectId())
            .append("objectName", getObjectName())
            .append("parentId", getParentId())
            .append("parentName", getParentName())
            .append("isRoot", getIsRoot())
            .toString();
    }

}
