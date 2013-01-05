package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNew implements Serializable {

    /** identifier field */
    private BigDecimal newsId;

    /** nullable persistent field */
    private String topic;

    /** persistent field */
    private BigDecimal priorityId;

    /** nullable persistent field */
    private BigDecimal postedById;

    /** nullable persistent field */
    private Date postedDatetime;

    /** persistent field */
    private BigDecimal createdById;

    /** persistent field */
    private Date createdDatetime;

    /** nullable persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private Date modifiedDatetime;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private BigDecimal notificationId;

    /** nullable persistent field */
    private Clob messageClob;

    /** persistent field */
    private Set pnSpaceHasNews;

    /** persistent field */
    private Set pnNewsHistories;

    /** full constructor */
    public PnNew(BigDecimal newsId, String topic, BigDecimal priorityId, BigDecimal postedById, Date postedDatetime, BigDecimal createdById, Date createdDatetime, BigDecimal modifiedById, Date modifiedDatetime, Date crc, String recordStatus, BigDecimal notificationId, Clob messageClob, Set pnSpaceHasNews, Set pnNewsHistories) {
        this.newsId = newsId;
        this.topic = topic;
        this.priorityId = priorityId;
        this.postedById = postedById;
        this.postedDatetime = postedDatetime;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.modifiedById = modifiedById;
        this.modifiedDatetime = modifiedDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.notificationId = notificationId;
        this.messageClob = messageClob;
        this.pnSpaceHasNews = pnSpaceHasNews;
        this.pnNewsHistories = pnNewsHistories;
    }

    /** default constructor */
    public PnNew() {
    }

    /** minimal constructor */
    public PnNew(BigDecimal newsId, BigDecimal priorityId, BigDecimal createdById, Date createdDatetime, Date crc, String recordStatus, Set pnSpaceHasNews, Set pnNewsHistories) {
        this.newsId = newsId;
        this.priorityId = priorityId;
        this.createdById = createdById;
        this.createdDatetime = createdDatetime;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnSpaceHasNews = pnSpaceHasNews;
        this.pnNewsHistories = pnNewsHistories;
    }

    public BigDecimal getNewsId() {
        return this.newsId;
    }

    public void setNewsId(BigDecimal newsId) {
        this.newsId = newsId;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public BigDecimal getPriorityId() {
        return this.priorityId;
    }

    public void setPriorityId(BigDecimal priorityId) {
        this.priorityId = priorityId;
    }

    public BigDecimal getPostedById() {
        return this.postedById;
    }

    public void setPostedById(BigDecimal postedById) {
        this.postedById = postedById;
    }

    public Date getPostedDatetime() {
        return this.postedDatetime;
    }

    public void setPostedDatetime(Date postedDatetime) {
        this.postedDatetime = postedDatetime;
    }

    public BigDecimal getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(BigDecimal createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDatetime() {
        return this.createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
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

    public BigDecimal getNotificationId() {
        return this.notificationId;
    }

    public void setNotificationId(BigDecimal notificationId) {
        this.notificationId = notificationId;
    }

    public Clob getMessageClob() {
        return this.messageClob;
    }

    public void setMessageClob(Clob messageClob) {
        this.messageClob = messageClob;
    }

    public Set getPnSpaceHasNews() {
        return this.pnSpaceHasNews;
    }

    public void setPnSpaceHasNews(Set pnSpaceHasNews) {
        this.pnSpaceHasNews = pnSpaceHasNews;
    }

    public Set getPnNewsHistories() {
        return this.pnNewsHistories;
    }

    public void setPnNewsHistories(Set pnNewsHistories) {
        this.pnNewsHistories = pnNewsHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("newsId", getNewsId())
            .toString();
    }

}
