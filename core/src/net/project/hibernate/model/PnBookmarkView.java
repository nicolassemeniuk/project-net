package net.project.hibernate.model;
// Generated Jun 13, 2009 11:41:49 PM by Hibernate Tools 3.2.4.GA


import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnBookmarkView generated by hbm2java
 */
@Entity
@Table(name="PN_BOOKMARK_VIEW"
)
public class PnBookmarkView  implements java.io.Serializable {

     private BigDecimal bookmarkId;
     
     private String name;
     
     private String description;
     
     private String url;
     
     private BigDecimal statusId;
     
     private String status;
     
     private BigDecimal ownerId;
     
     private String owner;
     
     private String comments;
     
     private Date modifiedDate;
     
     private BigDecimal modifiedById;
     
     private String modifiedBy;
     
     private BigDecimal containerId;
     
     private BigDecimal ownerSpaceId;
     
     private String recordStatus;
     
     private Date crc;
     
     private BigDecimal hasLinks;

    public PnBookmarkView() {
    }

    public PnBookmarkView(BigDecimal bookmarkId, String name, String description, String url, BigDecimal statusId, String status, BigDecimal ownerId, String owner, String comments, Date modifiedDate, BigDecimal modifiedById, String modifiedBy, BigDecimal containerId, BigDecimal ownerSpaceId, String recordStatus, Date crc, BigDecimal hasLinks) {
       this.bookmarkId = bookmarkId;
       this.name = name;
       this.description = description;
       this.url = url;
       this.statusId = statusId;
       this.status = status;
       this.ownerId = ownerId;
       this.owner = owner;
       this.comments = comments;
       this.modifiedDate = modifiedDate;
       this.modifiedById = modifiedById;
       this.modifiedBy = modifiedBy;
       this.containerId = containerId;
       this.ownerSpaceId = ownerSpaceId;
       this.recordStatus = recordStatus;
       this.crc = crc;
       this.hasLinks = hasLinks;
    }
   

    
    @Column(name="BOOKMARK_ID", nullable=false, length=20)
    public BigDecimal getBookmarkId() {
        return this.bookmarkId;
    }
    
    public void setBookmarkId(BigDecimal bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    
    @Column(name="NAME", nullable=false, length=240)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="DESCRIPTION", nullable=false, length=500)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    
    @Column(name="URL", nullable=false, length=500)
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    
    @Column(name="STATUS_ID", nullable=false, length=20)
    public BigDecimal getStatusId() {
        return this.statusId;
    }
    
    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    
    @Column(name="STATUS", nullable=false, length=80)
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    
    @Column(name="OWNER_ID", nullable=false, length=20)
    public BigDecimal getOwnerId() {
        return this.ownerId;
    }
    
    public void setOwnerId(BigDecimal ownerId) {
        this.ownerId = ownerId;
    }

    
    @Column(name="OWNER", nullable=false, length=4000)
    public String getOwner() {
        return this.owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }

    
    @Column(name="COMMENTS", nullable=false, length=500)
    public String getComments() {
        return this.comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="MODIFIED_DATE", nullable=false, length=7)
    public Date getModifiedDate() {
        return this.modifiedDate;
    }
    
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    
    @Column(name="MODIFIED_BY_ID", nullable=false, length=20)
    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }
    
    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    
    @Column(name="MODIFIED_BY", nullable=false, length=4000)
    public String getModifiedBy() {
        return this.modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    
    @Column(name="CONTAINER_ID", nullable=false, length=20)
    public BigDecimal getContainerId() {
        return this.containerId;
    }
    
    public void setContainerId(BigDecimal containerId) {
        this.containerId = containerId;
    }

    
    @Column(name="OWNER_SPACE_ID", nullable=false, length=22)
    public BigDecimal getOwnerSpaceId() {
        return this.ownerSpaceId;
    }
    
    public void setOwnerSpaceId(BigDecimal ownerSpaceId) {
        this.ownerSpaceId = ownerSpaceId;
    }

    
    @Column(name="RECORD_STATUS", nullable=false, length=1)
    public String getRecordStatus() {
        return this.recordStatus;
    }
    
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="CRC", nullable=false, length=7)
    public Date getCrc() {
        return this.crc;
    }
    
    public void setCrc(Date crc) {
        this.crc = crc;
    }

    
    @Column(name="HAS_LINKS", nullable=false, length=22)
    public BigDecimal getHasLinks() {
        return this.hasLinks;
    }
    
    public void setHasLinks(BigDecimal hasLinks) {
        this.hasLinks = hasLinks;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("bookmarkId", getBookmarkId())
            .append("name", getName())
            .append("description", getDescription())
            .append("url", getUrl())
            .append("statusId", getStatusId())
            .append("status", getStatus())
            .append("ownerId", getOwnerId())
            .append("owner", getOwner())
            .append("comments", getComments())
            .append("modifiedDate", getModifiedDate())
            .append("modifiedById", getModifiedById())
            .append("modifiedBy", getModifiedBy())
            .append("containerId", getContainerId())
            .append("ownerSpaceId", getOwnerSpaceId())
            .append("recordStatus", getRecordStatus())
            .append("crc", getCrc())
            .append("hasLinks", getHasLinks())
            .toString();
    }

}


