package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDiscussionGroup implements Serializable {

    /** identifier field */
    private BigDecimal discussionGroupId;

    /** nullable persistent field */
    private String discussionGroupName;

    /** nullable persistent field */
    private String discussionGroupDescription;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Clob discussionGroupCharterClob;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private Set pnObjectHasDiscussions;

    /** persistent field */
    private Set pnPosts;

    /** persistent field */
    private Set pnDiscussionHistories;

    /** full constructor */
    public PnDiscussionGroup(BigDecimal discussionGroupId, String discussionGroupName, String discussionGroupDescription, String recordStatus, Clob discussionGroupCharterClob, net.project.hibernate.model.PnObject pnObject, Set pnObjectHasDiscussions, Set pnPosts, Set pnDiscussionHistories) {
        this.discussionGroupId = discussionGroupId;
        this.discussionGroupName = discussionGroupName;
        this.discussionGroupDescription = discussionGroupDescription;
        this.recordStatus = recordStatus;
        this.discussionGroupCharterClob = discussionGroupCharterClob;
        this.pnObject = pnObject;
        this.pnObjectHasDiscussions = pnObjectHasDiscussions;
        this.pnPosts = pnPosts;
        this.pnDiscussionHistories = pnDiscussionHistories;
    }

    /** default constructor */
    public PnDiscussionGroup() {
    }

    /** minimal constructor */
    public PnDiscussionGroup(BigDecimal discussionGroupId, String recordStatus, Set pnObjectHasDiscussions, Set pnPosts, Set pnDiscussionHistories) {
        this.discussionGroupId = discussionGroupId;
        this.recordStatus = recordStatus;
        this.pnObjectHasDiscussions = pnObjectHasDiscussions;
        this.pnPosts = pnPosts;
        this.pnDiscussionHistories = pnDiscussionHistories;
    }

    public BigDecimal getDiscussionGroupId() {
        return this.discussionGroupId;
    }

    public void setDiscussionGroupId(BigDecimal discussionGroupId) {
        this.discussionGroupId = discussionGroupId;
    }

    public String getDiscussionGroupName() {
        return this.discussionGroupName;
    }

    public void setDiscussionGroupName(String discussionGroupName) {
        this.discussionGroupName = discussionGroupName;
    }

    public String getDiscussionGroupDescription() {
        return this.discussionGroupDescription;
    }

    public void setDiscussionGroupDescription(String discussionGroupDescription) {
        this.discussionGroupDescription = discussionGroupDescription;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Clob getDiscussionGroupCharterClob() {
        return this.discussionGroupCharterClob;
    }

    public void setDiscussionGroupCharterClob(Clob discussionGroupCharterClob) {
        this.discussionGroupCharterClob = discussionGroupCharterClob;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public Set getPnObjectHasDiscussions() {
        return this.pnObjectHasDiscussions;
    }

    public void setPnObjectHasDiscussions(Set pnObjectHasDiscussions) {
        this.pnObjectHasDiscussions = pnObjectHasDiscussions;
    }

    public Set getPnPosts() {
        return this.pnPosts;
    }

    public void setPnPosts(Set pnPosts) {
        this.pnPosts = pnPosts;
    }

    public Set getPnDiscussionHistories() {
        return this.pnDiscussionHistories;
    }

    public void setPnDiscussionHistories(Set pnDiscussionHistories) {
        this.pnDiscussionHistories = pnDiscussionHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("discussionGroupId", getDiscussionGroupId())
            .toString();
    }

}
