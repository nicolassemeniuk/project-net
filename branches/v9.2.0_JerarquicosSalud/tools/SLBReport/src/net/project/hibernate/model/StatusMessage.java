package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class StatusMessage implements Serializable {

    /** identifier field */
    private Integer messageId;

    /** persistent field */
    private String title;

    /** persistent field */
    private String message;

    /** persistent field */
    private String activeIndicator;

    /** persistent field */
    private Date timestamp;

    /** full constructor */
    public StatusMessage(Integer messageId, String title, String message, String activeIndicator, Date timestamp) {
        this.messageId = messageId;
        this.title = title;
        this.message = message;
        this.activeIndicator = activeIndicator;
        this.timestamp = timestamp;
    }

    /** default constructor */
    public StatusMessage() {
    }

    public Integer getMessageId() {
        return this.messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActiveIndicator() {
        return this.activeIndicator;
    }

    public void setActiveIndicator(String activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("messageId", getMessageId())
            .toString();
    }

}
