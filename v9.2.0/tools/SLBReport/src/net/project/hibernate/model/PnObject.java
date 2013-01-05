package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObject implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** persistent field */
    private Date dateCreated;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnMeeting pnMeeting;

    /** nullable persistent field */
    private net.project.hibernate.model.PnCalendar pnCalendar;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPortfolio pnPortfolio;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocContainer pnDocContainer;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPhase pnPhase;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClassDomain pnClassDomain;

    /** nullable persistent field */
    private net.project.hibernate.model.PnFacility pnFacility;

    /** nullable persistent field */
    private net.project.hibernate.model.PnGroup pnGroup;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClass pnClass;

    /** nullable persistent field */
    private net.project.hibernate.model.PnProcess pnProcess;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocVersion pnDocVersion;

    /** nullable persistent field */
    private net.project.hibernate.model.PnGate pnGate;

    /** nullable persistent field */
    private net.project.hibernate.model.PnProjectSpace pnProjectSpace;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDeliverable pnDeliverable;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocument pnDocument;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDocSpace pnDocSpace;

    /** nullable persistent field */
    private net.project.hibernate.model.PnShareable pnShareable;

    /** nullable persistent field */
    private net.project.hibernate.model.PnBusiness pnBusiness;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson2;

    /** nullable persistent field */
    private net.project.hibernate.model.PnAddress pnAddress;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPlan pnPlan;

    /** nullable persistent field */
    private net.project.hibernate.model.PnTask pnTask;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnObjectType pnObjectType;

    /** persistent field */
    private Set pnCustomCodes;

    /** persistent field */
    private Set pnObjectHasDiscussions;

    /** persistent field */
    private Set pnObjectLinksByToObjectId;

    /** persistent field */
    private Set pnObjectLinksByFromObjectId;

    /** persistent field */
    private Set pnAssignments;

    /** persistent field */
    private Set pnPosts;

    /** persistent field */
    private Set pnAgendaItems;

    /** persistent field */
    private Set pnObjectPermissions;

    /** persistent field */
    private Set pnClassDomainValues;

    /** persistent field */
    private Set pnShareablePermissions;

    /** persistent field */
    private Set pnEnvelopeHasObjects;

    /** persistent field */
    private Set pnFormsHistories;

    /** persistent field */
    private Set pnDocContainerHasObjects;

    /** persistent field */
    private Set pnClassFields;

    /** persistent field */
    private Set pnSpaceHasFeaturedMenuitemsBySpaceId;

    /** persistent field */
    private Set pnSpaceHasFeaturedMenuitemsByObjectId;

    public PnObject(String objectType, BigDecimal personId, Date dateCreated, String recordStatus ){
    	PnObjectType pnObjectType = new PnObjectType();
    	pnObjectType.setObjectType(objectType);
    	this.pnObjectType = pnObjectType;
    	PnPerson pnPerson = new PnPerson();
    	pnPerson.setPersonId(personId);
    	this.pnPerson = pnPerson;
    	this.dateCreated = dateCreated;
    	this.recordStatus = recordStatus;    	
    }
    
    /** full constructor */
    public PnObject(BigDecimal objectId, Date dateCreated, String recordStatus, net.project.hibernate.model.PnMeeting pnMeeting, net.project.hibernate.model.PnCalendar pnCalendar, net.project.hibernate.model.PnPortfolio pnPortfolio, net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup, net.project.hibernate.model.PnDocContainer pnDocContainer, net.project.hibernate.model.PnPhase pnPhase, net.project.hibernate.model.PnClassDomain pnClassDomain, net.project.hibernate.model.PnFacility pnFacility, net.project.hibernate.model.PnGroup pnGroup, net.project.hibernate.model.PnClass pnClass, net.project.hibernate.model.PnProcess pnProcess, net.project.hibernate.model.PnDocVersion pnDocVersion, net.project.hibernate.model.PnGate pnGate, net.project.hibernate.model.PnProjectSpace pnProjectSpace, net.project.hibernate.model.PnDeliverable pnDeliverable, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnDocSpace pnDocSpace, net.project.hibernate.model.PnShareable pnShareable, net.project.hibernate.model.PnBusiness pnBusiness, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnAddress pnAddress, net.project.hibernate.model.PnPlan pnPlan, net.project.hibernate.model.PnTask pnTask, net.project.hibernate.model.PnPerson pnPerson2, net.project.hibernate.model.PnObjectType pnObjectType, Set pnCustomCodes, Set pnObjectHasDiscussions, Set pnObjectLinksByToObjectId, Set pnObjectLinksByFromObjectId, Set pnAssignments, Set pnPosts, Set pnAgendaItems, Set pnObjectPermissions, Set pnClassDomainValues, Set pnShareablePermissions, Set pnEnvelopeHasObjects, Set pnFormsHistories, Set pnDocContainerHasObjects, Set pnClassFields, Set pnSpaceHasFeaturedMenuitemsBySpaceId, Set pnSpaceHasFeaturedMenuitemsByObjectId) {
        this.objectId = objectId;
        this.dateCreated = dateCreated;
        this.recordStatus = recordStatus;
        this.pnMeeting = pnMeeting;
        this.pnCalendar = pnCalendar;
        this.pnPortfolio = pnPortfolio;
        this.pnDiscussionGroup = pnDiscussionGroup;
        this.pnDocContainer = pnDocContainer;
        this.pnPhase = pnPhase;
        this.pnClassDomain = pnClassDomain;
        this.pnFacility = pnFacility;
        this.pnGroup = pnGroup;
        this.pnClass = pnClass;
        this.pnProcess = pnProcess;
        this.pnDocVersion = pnDocVersion;
        this.pnGate = pnGate;
        this.pnProjectSpace = pnProjectSpace;
        this.pnDeliverable = pnDeliverable;
        this.pnDocument = pnDocument;
        this.pnDocSpace = pnDocSpace;
        this.pnShareable = pnShareable;
        this.pnBusiness = pnBusiness;
        this.pnPerson2 = pnPerson2;
        this.pnAddress = pnAddress;
        this.pnPlan = pnPlan;
        this.pnTask = pnTask;
        this.pnPerson = pnPerson;
        this.pnObjectType = pnObjectType;
        this.pnCustomCodes = pnCustomCodes;
        this.pnObjectHasDiscussions = pnObjectHasDiscussions;
        this.pnObjectLinksByToObjectId = pnObjectLinksByToObjectId;
        this.pnObjectLinksByFromObjectId = pnObjectLinksByFromObjectId;
        this.pnAssignments = pnAssignments;
        this.pnPosts = pnPosts;
        this.pnAgendaItems = pnAgendaItems;
        this.pnObjectPermissions = pnObjectPermissions;
        this.pnClassDomainValues = pnClassDomainValues;
        this.pnShareablePermissions = pnShareablePermissions;
        this.pnEnvelopeHasObjects = pnEnvelopeHasObjects;
        this.pnFormsHistories = pnFormsHistories;
        this.pnDocContainerHasObjects = pnDocContainerHasObjects;
        this.pnClassFields = pnClassFields;
        this.pnSpaceHasFeaturedMenuitemsBySpaceId = pnSpaceHasFeaturedMenuitemsBySpaceId;
        this.pnSpaceHasFeaturedMenuitemsByObjectId = pnSpaceHasFeaturedMenuitemsByObjectId;
    }

    /** default constructor */
    public PnObject() {
    }

    /** minimal constructor */
    public PnObject(BigDecimal objectId, Date dateCreated, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnObjectType pnObjectType, Set pnCustomCodes, Set pnObjectHasDiscussions, Set pnObjectLinksByToObjectId, Set pnObjectLinksByFromObjectId, Set pnAssignments, Set pnPosts, Set pnAgendaItems, Set pnObjectPermissions, Set pnClassDomainValues, Set pnShareablePermissions, Set pnEnvelopeHasObjects, Set pnFormsHistories, Set pnDocContainerHasObjects, Set pnClassFields, Set pnSpaceHasFeaturedMenuitemsBySpaceId, Set pnSpaceHasFeaturedMenuitemsByObjectId) {
        this.objectId = objectId;
        this.dateCreated = dateCreated;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnObjectType = pnObjectType;
        this.pnCustomCodes = pnCustomCodes;
        this.pnObjectHasDiscussions = pnObjectHasDiscussions;
        this.pnObjectLinksByToObjectId = pnObjectLinksByToObjectId;
        this.pnObjectLinksByFromObjectId = pnObjectLinksByFromObjectId;
        this.pnAssignments = pnAssignments;
        this.pnPosts = pnPosts;
        this.pnAgendaItems = pnAgendaItems;
        this.pnObjectPermissions = pnObjectPermissions;
        this.pnClassDomainValues = pnClassDomainValues;
        this.pnShareablePermissions = pnShareablePermissions;
        this.pnEnvelopeHasObjects = pnEnvelopeHasObjects;
        this.pnFormsHistories = pnFormsHistories;
        this.pnDocContainerHasObjects = pnDocContainerHasObjects;
        this.pnClassFields = pnClassFields;
        this.pnSpaceHasFeaturedMenuitemsBySpaceId = pnSpaceHasFeaturedMenuitemsBySpaceId;
        this.pnSpaceHasFeaturedMenuitemsByObjectId = pnSpaceHasFeaturedMenuitemsByObjectId;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnMeeting getPnMeeting() {
        return this.pnMeeting;
    }

    public void setPnMeeting(net.project.hibernate.model.PnMeeting pnMeeting) {
        this.pnMeeting = pnMeeting;
    }

    public net.project.hibernate.model.PnCalendar getPnCalendar() {
        return this.pnCalendar;
    }

    public void setPnCalendar(net.project.hibernate.model.PnCalendar pnCalendar) {
        this.pnCalendar = pnCalendar;
    }

    public net.project.hibernate.model.PnPortfolio getPnPortfolio() {
        return this.pnPortfolio;
    }

    public void setPnPortfolio(net.project.hibernate.model.PnPortfolio pnPortfolio) {
        this.pnPortfolio = pnPortfolio;
    }

    public net.project.hibernate.model.PnDiscussionGroup getPnDiscussionGroup() {
        return this.pnDiscussionGroup;
    }

    public void setPnDiscussionGroup(net.project.hibernate.model.PnDiscussionGroup pnDiscussionGroup) {
        this.pnDiscussionGroup = pnDiscussionGroup;
    }

    public net.project.hibernate.model.PnDocContainer getPnDocContainer() {
        return this.pnDocContainer;
    }

    public void setPnDocContainer(net.project.hibernate.model.PnDocContainer pnDocContainer) {
        this.pnDocContainer = pnDocContainer;
    }

    public net.project.hibernate.model.PnPhase getPnPhase() {
        return this.pnPhase;
    }

    public void setPnPhase(net.project.hibernate.model.PnPhase pnPhase) {
        this.pnPhase = pnPhase;
    }

    public net.project.hibernate.model.PnClassDomain getPnClassDomain() {
        return this.pnClassDomain;
    }

    public void setPnClassDomain(net.project.hibernate.model.PnClassDomain pnClassDomain) {
        this.pnClassDomain = pnClassDomain;
    }

    public net.project.hibernate.model.PnFacility getPnFacility() {
        return this.pnFacility;
    }

    public void setPnFacility(net.project.hibernate.model.PnFacility pnFacility) {
        this.pnFacility = pnFacility;
    }

    public net.project.hibernate.model.PnGroup getPnGroup() {
        return this.pnGroup;
    }

    public void setPnGroup(net.project.hibernate.model.PnGroup pnGroup) {
        this.pnGroup = pnGroup;
    }

    public net.project.hibernate.model.PnClass getPnClass() {
        return this.pnClass;
    }

    public void setPnClass(net.project.hibernate.model.PnClass pnClass) {
        this.pnClass = pnClass;
    }

    public net.project.hibernate.model.PnProcess getPnProcess() {
        return this.pnProcess;
    }

    public void setPnProcess(net.project.hibernate.model.PnProcess pnProcess) {
        this.pnProcess = pnProcess;
    }

    public net.project.hibernate.model.PnDocVersion getPnDocVersion() {
        return this.pnDocVersion;
    }

    public void setPnDocVersion(net.project.hibernate.model.PnDocVersion pnDocVersion) {
        this.pnDocVersion = pnDocVersion;
    }

    public net.project.hibernate.model.PnGate getPnGate() {
        return this.pnGate;
    }

    public void setPnGate(net.project.hibernate.model.PnGate pnGate) {
        this.pnGate = pnGate;
    }

    public net.project.hibernate.model.PnProjectSpace getPnProjectSpace() {
        return this.pnProjectSpace;
    }

    public void setPnProjectSpace(net.project.hibernate.model.PnProjectSpace pnProjectSpace) {
        this.pnProjectSpace = pnProjectSpace;
    }

    public net.project.hibernate.model.PnDeliverable getPnDeliverable() {
        return this.pnDeliverable;
    }

    public void setPnDeliverable(net.project.hibernate.model.PnDeliverable pnDeliverable) {
        this.pnDeliverable = pnDeliverable;
    }

    public net.project.hibernate.model.PnDocument getPnDocument() {
        return this.pnDocument;
    }

    public void setPnDocument(net.project.hibernate.model.PnDocument pnDocument) {
        this.pnDocument = pnDocument;
    }

    public net.project.hibernate.model.PnDocSpace getPnDocSpace() {
        return this.pnDocSpace;
    }

    public void setPnDocSpace(net.project.hibernate.model.PnDocSpace pnDocSpace) {
        this.pnDocSpace = pnDocSpace;
    }

    public net.project.hibernate.model.PnShareable getPnShareable() {
        return this.pnShareable;
    }

    public void setPnShareable(net.project.hibernate.model.PnShareable pnShareable) {
        this.pnShareable = pnShareable;
    }

    public net.project.hibernate.model.PnBusiness getPnBusiness() {
        return this.pnBusiness;
    }

    public void setPnBusiness(net.project.hibernate.model.PnBusiness pnBusiness) {
        this.pnBusiness = pnBusiness;
    }

    public net.project.hibernate.model.PnPerson getPnPerson2() {
        return this.pnPerson2;
    }

    public void setPnPerson2(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson2 = pnPerson;
    }

    public net.project.hibernate.model.PnAddress getPnAddress() {
        return this.pnAddress;
    }

    public void setPnAddress(net.project.hibernate.model.PnAddress pnAddress) {
        this.pnAddress = pnAddress;
    }

    public net.project.hibernate.model.PnPlan getPnPlan() {
        return this.pnPlan;
    }

    public void setPnPlan(net.project.hibernate.model.PnPlan pnPlan) {
        this.pnPlan = pnPlan;
    }

    public net.project.hibernate.model.PnTask getPnTask() {
        return this.pnTask;
    }

    public void setPnTask(net.project.hibernate.model.PnTask pnTask) {
        this.pnTask = pnTask;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnObjectType getPnObjectType() {
        return this.pnObjectType;
    }

    public void setPnObjectType(net.project.hibernate.model.PnObjectType pnObjectType) {
        this.pnObjectType = pnObjectType;
    }

    public Set getPnCustomCodes() {
        return this.pnCustomCodes;
    }

    public void setPnCustomCodes(Set pnCustomCodes) {
        this.pnCustomCodes = pnCustomCodes;
    }

    public Set getPnObjectHasDiscussions() {
        return this.pnObjectHasDiscussions;
    }

    public void setPnObjectHasDiscussions(Set pnObjectHasDiscussions) {
        this.pnObjectHasDiscussions = pnObjectHasDiscussions;
    }

    public Set getPnObjectLinksByToObjectId() {
        return this.pnObjectLinksByToObjectId;
    }

    public void setPnObjectLinksByToObjectId(Set pnObjectLinksByToObjectId) {
        this.pnObjectLinksByToObjectId = pnObjectLinksByToObjectId;
    }

    public Set getPnObjectLinksByFromObjectId() {
        return this.pnObjectLinksByFromObjectId;
    }

    public void setPnObjectLinksByFromObjectId(Set pnObjectLinksByFromObjectId) {
        this.pnObjectLinksByFromObjectId = pnObjectLinksByFromObjectId;
    }

    public Set getPnAssignments() {
        return this.pnAssignments;
    }

    public void setPnAssignments(Set pnAssignments) {
        this.pnAssignments = pnAssignments;
    }

    public Set getPnPosts() {
        return this.pnPosts;
    }

    public void setPnPosts(Set pnPosts) {
        this.pnPosts = pnPosts;
    }

    public Set getPnAgendaItems() {
        return this.pnAgendaItems;
    }

    public void setPnAgendaItems(Set pnAgendaItems) {
        this.pnAgendaItems = pnAgendaItems;
    }

    public Set getPnObjectPermissions() {
        return this.pnObjectPermissions;
    }

    public void setPnObjectPermissions(Set pnObjectPermissions) {
        this.pnObjectPermissions = pnObjectPermissions;
    }

    public Set getPnClassDomainValues() {
        return this.pnClassDomainValues;
    }

    public void setPnClassDomainValues(Set pnClassDomainValues) {
        this.pnClassDomainValues = pnClassDomainValues;
    }

    public Set getPnShareablePermissions() {
        return this.pnShareablePermissions;
    }

    public void setPnShareablePermissions(Set pnShareablePermissions) {
        this.pnShareablePermissions = pnShareablePermissions;
    }

    public Set getPnEnvelopeHasObjects() {
        return this.pnEnvelopeHasObjects;
    }

    public void setPnEnvelopeHasObjects(Set pnEnvelopeHasObjects) {
        this.pnEnvelopeHasObjects = pnEnvelopeHasObjects;
    }

    public Set getPnFormsHistories() {
        return this.pnFormsHistories;
    }

    public void setPnFormsHistories(Set pnFormsHistories) {
        this.pnFormsHistories = pnFormsHistories;
    }

    public Set getPnDocContainerHasObjects() {
        return this.pnDocContainerHasObjects;
    }

    public void setPnDocContainerHasObjects(Set pnDocContainerHasObjects) {
        this.pnDocContainerHasObjects = pnDocContainerHasObjects;
    }

    public Set getPnClassFields() {
        return this.pnClassFields;
    }

    public void setPnClassFields(Set pnClassFields) {
        this.pnClassFields = pnClassFields;
    }

    public Set getPnSpaceHasFeaturedMenuitemsBySpaceId() {
        return this.pnSpaceHasFeaturedMenuitemsBySpaceId;
    }

    public void setPnSpaceHasFeaturedMenuitemsBySpaceId(Set pnSpaceHasFeaturedMenuitemsBySpaceId) {
        this.pnSpaceHasFeaturedMenuitemsBySpaceId = pnSpaceHasFeaturedMenuitemsBySpaceId;
    }

    public Set getPnSpaceHasFeaturedMenuitemsByObjectId() {
        return this.pnSpaceHasFeaturedMenuitemsByObjectId;
    }

    public void setPnSpaceHasFeaturedMenuitemsByObjectId(Set pnSpaceHasFeaturedMenuitemsByObjectId) {
        this.pnSpaceHasFeaturedMenuitemsByObjectId = pnSpaceHasFeaturedMenuitemsByObjectId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
