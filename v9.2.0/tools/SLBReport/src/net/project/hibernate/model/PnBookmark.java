package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBookmark implements Serializable {

    /** identifier field */
    private BigDecimal bookmarkId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private String url;

    /** nullable persistent field */
    private BigDecimal statusId;

    /** nullable persistent field */
    private String comments;

    /** nullable persistent field */
    private Date modifiedDate;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByOwnerId;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByModifiedById;

    /** full constructor */
    public PnBookmark(BigDecimal bookmarkId, String name, String description, String url, BigDecimal statusId, String comments, Date modifiedDate, String recordStatus, Date crc, net.project.hibernate.model.PnPerson pnPersonByOwnerId, net.project.hibernate.model.PnPerson pnPersonByModifiedById) {
        this.bookmarkId = bookmarkId;
        this.name = name;
        this.description = description;
        this.url = url;
        this.statusId = statusId;
        this.comments = comments;
        this.modifiedDate = modifiedDate;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.pnPersonByOwnerId = pnPersonByOwnerId;
        this.pnPersonByModifiedById = pnPersonByModifiedById;
    }

    /** default constructor */
    public PnBookmark() {
    }

    /** minimal constructor */
    public PnBookmark(BigDecimal bookmarkId, String name, String url, String recordStatus, Date crc, net.project.hibernate.model.PnPerson pnPersonByOwnerId, net.project.hibernate.model.PnPerson pnPersonByModifiedById) {
        this.bookmarkId = bookmarkId;
        this.name = name;
        this.url = url;
        this.recordStatus = recordStatus;
        this.crc = crc;
        this.pnPersonByOwnerId = pnPersonByOwnerId;
        this.pnPersonByModifiedById = pnPersonByModifiedById;
    }

    public BigDecimal getBookmarkId() {
        return this.bookmarkId;
    }

    public void setBookmarkId(BigDecimal bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigDecimal getStatusId() {
        return this.statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getModifiedDate() {
        return this.modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByOwnerId() {
        return this.pnPersonByOwnerId;
    }

    public void setPnPersonByOwnerId(net.project.hibernate.model.PnPerson pnPersonByOwnerId) {
        this.pnPersonByOwnerId = pnPersonByOwnerId;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByModifiedById() {
        return this.pnPersonByModifiedById;
    }

    public void setPnPersonByModifiedById(net.project.hibernate.model.PnPerson pnPersonByModifiedById) {
        this.pnPersonByModifiedById = pnPersonByModifiedById;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("bookmarkId", getBookmarkId())
            .toString();
    }

}
