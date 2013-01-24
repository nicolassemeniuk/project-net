package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPerson implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** persistent field */
    private String email;

    /** persistent field */
    private String firstName;

    /** persistent field */
    private String lastName;

    /** persistent field */
    private String displayName;

    /** persistent field */
    private String userStatus;

    /** persistent field */
    private BigDecimal membershipPortfolioId;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date createdDate;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonNotificationPref pnPersonNotificationPref;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonNotificationAddress pnPersonNotificationAddress;

    /** nullable persistent field */
    private net.project.hibernate.model.PnUser pnUser;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonHasLicense pnPersonHasLicense;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonSurvey pnPersonSurvey;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPersonProfile pnPersonProfile;

    /** persistent field */
    private Set pnMeetings;

    /** persistent field */
    private Set pnGroupHasPersons;

    /** persistent field */
    private Set pnPersonHasProfCerts;

    /** persistent field */
    private Set pnInvitedUsers;

    /** persistent field */
    private Set pnPersonSkillCommentsByAddedBy;

    /** persistent field */
    private Set pnPersonSkillCommentsByPersonId;

    /** persistent field */
    private Set pnDocContainers;

    /** persistent field */
    private Set pnPostHistories;

    /** persistent field */
    private Set pnPosts;

    /** persistent field */
    private Set pnGroupHistories;

    /** persistent field */
    private Set pnAgendaItems;

    /** persistent field */
    private Set pnGroups;

    /** persistent field */
    private Set pnBookmarksByOwnerId;

    /** persistent field */
    private Set pnBookmarksByModifiedById;

    /** persistent field */
    private Set pnPersonAllocations;

    /** persistent field */
    private Set pnScheduledSubscriptions;

    /** persistent field */
    private Set pnDirectoryHasPersons;

    /** persistent field */
    private Set pnDocConfigurations;

    /** persistent field */
    private Set pnDiscussionHistories;

    /** persistent field */
    private Set pnObjects;

    /** persistent field */
    private Set pnTaskHistories;

    /** persistent field */
    private Set pnDocVersionsByDocAuthorId;

    /** persistent field */
    private Set pnDocVersionsByModifiedById;

    /** persistent field */
    private Set pnDocVersionsByCheckedOutById;

    /** persistent field */
    private Set pnPersonHasStateRegs;

    /** persistent field */
    private Set pnCalEventHasAttendees;

    /** persistent field */
    private Set pnBills;

    /** persistent field */
    private Set pnPersonProperties;

    /** persistent field */
    private Set pnProjectSpaces;

    /** persistent field */
    private Set pnFormsHistories;

    /** persistent field */
    private Set pnLicenses;

    /** persistent field */
    private Set pnNewsHistories;

    /** persistent field */
    private Set helpFeedbacks;

    /** persistent field */
    private Set pnLoginHistories;

    /** persistent field */
    private Set pnWorkingtimeCalendars;

    /** persistent field */
    private Set pnPostReaders;

    /** persistent field */
    private Set pnLicensePurchasers;

    /** persistent field */
    private Set pnPersonHasDisciplines;

    /** persistent field */
    private Set pnDocHistories;

    /** persistent field */
    private Set pnDocCheckoutLocations;

    /** persistent field */
    private Set pnPersonAuthenticators;

    /** persistent field */
    private Set pnSpaceHasPersons;

    /** full constructor */
    public PnPerson(BigDecimal personId, String email, String firstName, String lastName, String displayName, String userStatus, BigDecimal membershipPortfolioId, String recordStatus, Date createdDate, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnPersonNotificationPref pnPersonNotificationPref, net.project.hibernate.model.PnPersonNotificationAddress pnPersonNotificationAddress, net.project.hibernate.model.PnUser pnUser, net.project.hibernate.model.PnPersonHasLicense pnPersonHasLicense, net.project.hibernate.model.PnPersonSurvey pnPersonSurvey, net.project.hibernate.model.PnPersonProfile pnPersonProfile, Set pnMeetings, Set pnGroupHasPersons, Set pnPersonHasProfCerts, Set pnInvitedUsers, Set pnPersonSkillCommentsByAddedBy, Set pnPersonSkillCommentsByPersonId, Set pnDocContainers, Set pnPostHistories, Set pnPosts, Set pnGroupHistories, Set pnAgendaItems, Set pnGroups, Set pnBookmarksByOwnerId, Set pnBookmarksByModifiedById, Set pnPersonAllocations, Set pnScheduledSubscriptions, Set pnDirectoryHasPersons, Set pnDocConfigurations, Set pnDiscussionHistories, Set pnObjects, Set pnTaskHistories, Set pnDocVersionsByDocAuthorId, Set pnDocVersionsByModifiedById, Set pnDocVersionsByCheckedOutById, Set pnPersonHasStateRegs, Set pnCalEventHasAttendees, Set pnBills, Set pnPersonProperties, Set pnProjectSpaces, Set pnFormsHistories, Set pnLicenses, Set pnNewsHistories, Set helpFeedbacks, Set pnLoginHistories, Set pnWorkingtimeCalendars, Set pnPostReaders, Set pnLicensePurchasers, Set pnPersonHasDisciplines, Set pnDocHistories, Set pnDocCheckoutLocations, Set pnPersonAuthenticators, Set pnSpaceHasPersons) {
        this.personId = personId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.userStatus = userStatus;
        this.membershipPortfolioId = membershipPortfolioId;
        this.recordStatus = recordStatus;
        this.createdDate = createdDate;
        this.pnObject = pnObject;
        this.pnPersonNotificationPref = pnPersonNotificationPref;
        this.pnPersonNotificationAddress = pnPersonNotificationAddress;
        this.pnUser = pnUser;
        this.pnPersonHasLicense = pnPersonHasLicense;
        this.pnPersonSurvey = pnPersonSurvey;
        this.pnPersonProfile = pnPersonProfile;
        this.pnMeetings = pnMeetings;
        this.pnGroupHasPersons = pnGroupHasPersons;
        this.pnPersonHasProfCerts = pnPersonHasProfCerts;
        this.pnInvitedUsers = pnInvitedUsers;
        this.pnPersonSkillCommentsByAddedBy = pnPersonSkillCommentsByAddedBy;
        this.pnPersonSkillCommentsByPersonId = pnPersonSkillCommentsByPersonId;
        this.pnDocContainers = pnDocContainers;
        this.pnPostHistories = pnPostHistories;
        this.pnPosts = pnPosts;
        this.pnGroupHistories = pnGroupHistories;
        this.pnAgendaItems = pnAgendaItems;
        this.pnGroups = pnGroups;
        this.pnBookmarksByOwnerId = pnBookmarksByOwnerId;
        this.pnBookmarksByModifiedById = pnBookmarksByModifiedById;
        this.pnPersonAllocations = pnPersonAllocations;
        this.pnScheduledSubscriptions = pnScheduledSubscriptions;
        this.pnDirectoryHasPersons = pnDirectoryHasPersons;
        this.pnDocConfigurations = pnDocConfigurations;
        this.pnDiscussionHistories = pnDiscussionHistories;
        this.pnObjects = pnObjects;
        this.pnTaskHistories = pnTaskHistories;
        this.pnDocVersionsByDocAuthorId = pnDocVersionsByDocAuthorId;
        this.pnDocVersionsByModifiedById = pnDocVersionsByModifiedById;
        this.pnDocVersionsByCheckedOutById = pnDocVersionsByCheckedOutById;
        this.pnPersonHasStateRegs = pnPersonHasStateRegs;
        this.pnCalEventHasAttendees = pnCalEventHasAttendees;
        this.pnBills = pnBills;
        this.pnPersonProperties = pnPersonProperties;
        this.pnProjectSpaces = pnProjectSpaces;
        this.pnFormsHistories = pnFormsHistories;
        this.pnLicenses = pnLicenses;
        this.pnNewsHistories = pnNewsHistories;
        this.helpFeedbacks = helpFeedbacks;
        this.pnLoginHistories = pnLoginHistories;
        this.pnWorkingtimeCalendars = pnWorkingtimeCalendars;
        this.pnPostReaders = pnPostReaders;
        this.pnLicensePurchasers = pnLicensePurchasers;
        this.pnPersonHasDisciplines = pnPersonHasDisciplines;
        this.pnDocHistories = pnDocHistories;
        this.pnDocCheckoutLocations = pnDocCheckoutLocations;
        this.pnPersonAuthenticators = pnPersonAuthenticators;
        this.pnSpaceHasPersons = pnSpaceHasPersons;
    }

    /** default constructor */
    public PnPerson() {
    }

    public PnPerson(BigDecimal personId) {
    	this.personId = personId;
    }
    
    /** minimal constructor */
    public PnPerson(BigDecimal personId, String email, String firstName, String lastName, String displayName, String userStatus, BigDecimal membershipPortfolioId, String recordStatus, Set pnMeetings, Set pnGroupHasPersons, Set pnPersonHasProfCerts, Set pnInvitedUsers, Set pnPersonSkillCommentsByAddedBy, Set pnPersonSkillCommentsByPersonId, Set pnDocContainers, Set pnPostHistories, Set pnPosts, Set pnGroupHistories, Set pnAgendaItems, Set pnGroups, Set pnBookmarksByOwnerId, Set pnBookmarksByModifiedById, Set pnPersonAllocations, Set pnScheduledSubscriptions, Set pnDirectoryHasPersons, Set pnDocConfigurations, Set pnDiscussionHistories, Set pnObjects, Set pnTaskHistories, Set pnDocVersionsByDocAuthorId, Set pnDocVersionsByModifiedById, Set pnDocVersionsByCheckedOutById, Set pnPersonHasStateRegs, Set pnCalEventHasAttendees, Set pnBills, Set pnPersonProperties, Set pnProjectSpaces, Set pnFormsHistories, Set pnLicenses, Set pnNewsHistories, Set helpFeedbacks, Set pnLoginHistories, Set pnWorkingtimeCalendars, Set pnPostReaders, Set pnLicensePurchasers, Set pnPersonHasDisciplines, Set pnDocHistories, Set pnDocCheckoutLocations, Set pnPersonAuthenticators, Set pnSpaceHasPersons) {
        this.personId = personId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.userStatus = userStatus;
        this.membershipPortfolioId = membershipPortfolioId;
        this.recordStatus = recordStatus;
        this.pnMeetings = pnMeetings;
        this.pnGroupHasPersons = pnGroupHasPersons;
        this.pnPersonHasProfCerts = pnPersonHasProfCerts;
        this.pnInvitedUsers = pnInvitedUsers;
        this.pnPersonSkillCommentsByAddedBy = pnPersonSkillCommentsByAddedBy;
        this.pnPersonSkillCommentsByPersonId = pnPersonSkillCommentsByPersonId;
        this.pnDocContainers = pnDocContainers;
        this.pnPostHistories = pnPostHistories;
        this.pnPosts = pnPosts;
        this.pnGroupHistories = pnGroupHistories;
        this.pnAgendaItems = pnAgendaItems;
        this.pnGroups = pnGroups;
        this.pnBookmarksByOwnerId = pnBookmarksByOwnerId;
        this.pnBookmarksByModifiedById = pnBookmarksByModifiedById;
        this.pnPersonAllocations = pnPersonAllocations;
        this.pnScheduledSubscriptions = pnScheduledSubscriptions;
        this.pnDirectoryHasPersons = pnDirectoryHasPersons;
        this.pnDocConfigurations = pnDocConfigurations;
        this.pnDiscussionHistories = pnDiscussionHistories;
        this.pnObjects = pnObjects;
        this.pnTaskHistories = pnTaskHistories;
        this.pnDocVersionsByDocAuthorId = pnDocVersionsByDocAuthorId;
        this.pnDocVersionsByModifiedById = pnDocVersionsByModifiedById;
        this.pnDocVersionsByCheckedOutById = pnDocVersionsByCheckedOutById;
        this.pnPersonHasStateRegs = pnPersonHasStateRegs;
        this.pnCalEventHasAttendees = pnCalEventHasAttendees;
        this.pnBills = pnBills;
        this.pnPersonProperties = pnPersonProperties;
        this.pnProjectSpaces = pnProjectSpaces;
        this.pnFormsHistories = pnFormsHistories;
        this.pnLicenses = pnLicenses;
        this.pnNewsHistories = pnNewsHistories;
        this.helpFeedbacks = helpFeedbacks;
        this.pnLoginHistories = pnLoginHistories;
        this.pnWorkingtimeCalendars = pnWorkingtimeCalendars;
        this.pnPostReaders = pnPostReaders;
        this.pnLicensePurchasers = pnLicensePurchasers;
        this.pnPersonHasDisciplines = pnPersonHasDisciplines;
        this.pnDocHistories = pnDocHistories;
        this.pnDocCheckoutLocations = pnDocCheckoutLocations;
        this.pnPersonAuthenticators = pnPersonAuthenticators;
        this.pnSpaceHasPersons = pnSpaceHasPersons;
    }
    
    public PnPerson(BigDecimal personId, String email, String firstName, String lastName, String displayName, String userStatus, BigDecimal membershipPortfolioId, Date createdDate, String recordStatus) {
        this.personId = personId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.userStatus = userStatus;
        this.membershipPortfolioId = membershipPortfolioId;
        this.recordStatus = recordStatus;
        this.createdDate = createdDate;        
    }
    
    public PnPerson(BigDecimal personId,  String firstName, String lastName, String displayName) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserStatus() {
        return this.userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public BigDecimal getMembershipPortfolioId() {
        return this.membershipPortfolioId;
    }

    public void setMembershipPortfolioId(BigDecimal membershipPortfolioId) {
        this.membershipPortfolioId = membershipPortfolioId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnPersonNotificationPref getPnPersonNotificationPref() {
        return this.pnPersonNotificationPref;
    }

    public void setPnPersonNotificationPref(net.project.hibernate.model.PnPersonNotificationPref pnPersonNotificationPref) {
        this.pnPersonNotificationPref = pnPersonNotificationPref;
    }

    public net.project.hibernate.model.PnPersonNotificationAddress getPnPersonNotificationAddress() {
        return this.pnPersonNotificationAddress;
    }

    public void setPnPersonNotificationAddress(net.project.hibernate.model.PnPersonNotificationAddress pnPersonNotificationAddress) {
        this.pnPersonNotificationAddress = pnPersonNotificationAddress;
    }

    public net.project.hibernate.model.PnUser getPnUser() {
        return this.pnUser;
    }

    public void setPnUser(net.project.hibernate.model.PnUser pnUser) {
        this.pnUser = pnUser;
    }

    public net.project.hibernate.model.PnPersonHasLicense getPnPersonHasLicense() {
        return this.pnPersonHasLicense;
    }

    public void setPnPersonHasLicense(net.project.hibernate.model.PnPersonHasLicense pnPersonHasLicense) {
        this.pnPersonHasLicense = pnPersonHasLicense;
    }

    public net.project.hibernate.model.PnPersonSurvey getPnPersonSurvey() {
        return this.pnPersonSurvey;
    }

    public void setPnPersonSurvey(net.project.hibernate.model.PnPersonSurvey pnPersonSurvey) {
        this.pnPersonSurvey = pnPersonSurvey;
    }

    public net.project.hibernate.model.PnPersonProfile getPnPersonProfile() {
        return this.pnPersonProfile;
    }

    public void setPnPersonProfile(net.project.hibernate.model.PnPersonProfile pnPersonProfile) {
        this.pnPersonProfile = pnPersonProfile;
    }

    public Set getPnMeetings() {
        return this.pnMeetings;
    }

    public void setPnMeetings(Set pnMeetings) {
        this.pnMeetings = pnMeetings;
    }

    public Set getPnGroupHasPersons() {
        return this.pnGroupHasPersons;
    }

    public void setPnGroupHasPersons(Set pnGroupHasPersons) {
        this.pnGroupHasPersons = pnGroupHasPersons;
    }

    public Set getPnPersonHasProfCerts() {
        return this.pnPersonHasProfCerts;
    }

    public void setPnPersonHasProfCerts(Set pnPersonHasProfCerts) {
        this.pnPersonHasProfCerts = pnPersonHasProfCerts;
    }

    public Set getPnInvitedUsers() {
        return this.pnInvitedUsers;
    }

    public void setPnInvitedUsers(Set pnInvitedUsers) {
        this.pnInvitedUsers = pnInvitedUsers;
    }

    public Set getPnPersonSkillCommentsByAddedBy() {
        return this.pnPersonSkillCommentsByAddedBy;
    }

    public void setPnPersonSkillCommentsByAddedBy(Set pnPersonSkillCommentsByAddedBy) {
        this.pnPersonSkillCommentsByAddedBy = pnPersonSkillCommentsByAddedBy;
    }

    public Set getPnPersonSkillCommentsByPersonId() {
        return this.pnPersonSkillCommentsByPersonId;
    }

    public void setPnPersonSkillCommentsByPersonId(Set pnPersonSkillCommentsByPersonId) {
        this.pnPersonSkillCommentsByPersonId = pnPersonSkillCommentsByPersonId;
    }

    public Set getPnDocContainers() {
        return this.pnDocContainers;
    }

    public void setPnDocContainers(Set pnDocContainers) {
        this.pnDocContainers = pnDocContainers;
    }

    public Set getPnPostHistories() {
        return this.pnPostHistories;
    }

    public void setPnPostHistories(Set pnPostHistories) {
        this.pnPostHistories = pnPostHistories;
    }

    public Set getPnPosts() {
        return this.pnPosts;
    }

    public void setPnPosts(Set pnPosts) {
        this.pnPosts = pnPosts;
    }

    public Set getPnGroupHistories() {
        return this.pnGroupHistories;
    }

    public void setPnGroupHistories(Set pnGroupHistories) {
        this.pnGroupHistories = pnGroupHistories;
    }

    public Set getPnAgendaItems() {
        return this.pnAgendaItems;
    }

    public void setPnAgendaItems(Set pnAgendaItems) {
        this.pnAgendaItems = pnAgendaItems;
    }

    public Set getPnGroups() {
        return this.pnGroups;
    }

    public void setPnGroups(Set pnGroups) {
        this.pnGroups = pnGroups;
    }

    public Set getPnBookmarksByOwnerId() {
        return this.pnBookmarksByOwnerId;
    }

    public void setPnBookmarksByOwnerId(Set pnBookmarksByOwnerId) {
        this.pnBookmarksByOwnerId = pnBookmarksByOwnerId;
    }

    public Set getPnBookmarksByModifiedById() {
        return this.pnBookmarksByModifiedById;
    }

    public void setPnBookmarksByModifiedById(Set pnBookmarksByModifiedById) {
        this.pnBookmarksByModifiedById = pnBookmarksByModifiedById;
    }

    public Set getPnPersonAllocations() {
        return this.pnPersonAllocations;
    }

    public void setPnPersonAllocations(Set pnPersonAllocations) {
        this.pnPersonAllocations = pnPersonAllocations;
    }

    public Set getPnScheduledSubscriptions() {
        return this.pnScheduledSubscriptions;
    }

    public void setPnScheduledSubscriptions(Set pnScheduledSubscriptions) {
        this.pnScheduledSubscriptions = pnScheduledSubscriptions;
    }

    public Set getPnDirectoryHasPersons() {
        return this.pnDirectoryHasPersons;
    }

    public void setPnDirectoryHasPersons(Set pnDirectoryHasPersons) {
        this.pnDirectoryHasPersons = pnDirectoryHasPersons;
    }

    public Set getPnDocConfigurations() {
        return this.pnDocConfigurations;
    }

    public void setPnDocConfigurations(Set pnDocConfigurations) {
        this.pnDocConfigurations = pnDocConfigurations;
    }

    public Set getPnDiscussionHistories() {
        return this.pnDiscussionHistories;
    }

    public void setPnDiscussionHistories(Set pnDiscussionHistories) {
        this.pnDiscussionHistories = pnDiscussionHistories;
    }

    public Set getPnObjects() {
        return this.pnObjects;
    }

    public void setPnObjects(Set pnObjects) {
        this.pnObjects = pnObjects;
    }

    public Set getPnTaskHistories() {
        return this.pnTaskHistories;
    }

    public void setPnTaskHistories(Set pnTaskHistories) {
        this.pnTaskHistories = pnTaskHistories;
    }

    public Set getPnDocVersionsByDocAuthorId() {
        return this.pnDocVersionsByDocAuthorId;
    }

    public void setPnDocVersionsByDocAuthorId(Set pnDocVersionsByDocAuthorId) {
        this.pnDocVersionsByDocAuthorId = pnDocVersionsByDocAuthorId;
    }

    public Set getPnDocVersionsByModifiedById() {
        return this.pnDocVersionsByModifiedById;
    }

    public void setPnDocVersionsByModifiedById(Set pnDocVersionsByModifiedById) {
        this.pnDocVersionsByModifiedById = pnDocVersionsByModifiedById;
    }

    public Set getPnDocVersionsByCheckedOutById() {
        return this.pnDocVersionsByCheckedOutById;
    }

    public void setPnDocVersionsByCheckedOutById(Set pnDocVersionsByCheckedOutById) {
        this.pnDocVersionsByCheckedOutById = pnDocVersionsByCheckedOutById;
    }

    public Set getPnPersonHasStateRegs() {
        return this.pnPersonHasStateRegs;
    }

    public void setPnPersonHasStateRegs(Set pnPersonHasStateRegs) {
        this.pnPersonHasStateRegs = pnPersonHasStateRegs;
    }

    public Set getPnCalEventHasAttendees() {
        return this.pnCalEventHasAttendees;
    }

    public void setPnCalEventHasAttendees(Set pnCalEventHasAttendees) {
        this.pnCalEventHasAttendees = pnCalEventHasAttendees;
    }

    public Set getPnBills() {
        return this.pnBills;
    }

    public void setPnBills(Set pnBills) {
        this.pnBills = pnBills;
    }

    public Set getPnPersonProperties() {
        return this.pnPersonProperties;
    }

    public void setPnPersonProperties(Set pnPersonProperties) {
        this.pnPersonProperties = pnPersonProperties;
    }

    public Set getPnProjectSpaces() {
        return this.pnProjectSpaces;
    }

    public void setPnProjectSpaces(Set pnProjectSpaces) {
        this.pnProjectSpaces = pnProjectSpaces;
    }

    public Set getPnFormsHistories() {
        return this.pnFormsHistories;
    }

    public void setPnFormsHistories(Set pnFormsHistories) {
        this.pnFormsHistories = pnFormsHistories;
    }

    public Set getPnLicenses() {
        return this.pnLicenses;
    }

    public void setPnLicenses(Set pnLicenses) {
        this.pnLicenses = pnLicenses;
    }

    public Set getPnNewsHistories() {
        return this.pnNewsHistories;
    }

    public void setPnNewsHistories(Set pnNewsHistories) {
        this.pnNewsHistories = pnNewsHistories;
    }

    public Set getHelpFeedbacks() {
        return this.helpFeedbacks;
    }

    public void setHelpFeedbacks(Set helpFeedbacks) {
        this.helpFeedbacks = helpFeedbacks;
    }

    public Set getPnLoginHistories() {
        return this.pnLoginHistories;
    }

    public void setPnLoginHistories(Set pnLoginHistories) {
        this.pnLoginHistories = pnLoginHistories;
    }

    public Set getPnWorkingtimeCalendars() {
        return this.pnWorkingtimeCalendars;
    }

    public void setPnWorkingtimeCalendars(Set pnWorkingtimeCalendars) {
        this.pnWorkingtimeCalendars = pnWorkingtimeCalendars;
    }

    public Set getPnPostReaders() {
        return this.pnPostReaders;
    }

    public void setPnPostReaders(Set pnPostReaders) {
        this.pnPostReaders = pnPostReaders;
    }

    public Set getPnLicensePurchasers() {
        return this.pnLicensePurchasers;
    }

    public void setPnLicensePurchasers(Set pnLicensePurchasers) {
        this.pnLicensePurchasers = pnLicensePurchasers;
    }

    public Set getPnPersonHasDisciplines() {
        return this.pnPersonHasDisciplines;
    }

    public void setPnPersonHasDisciplines(Set pnPersonHasDisciplines) {
        this.pnPersonHasDisciplines = pnPersonHasDisciplines;
    }

    public Set getPnDocHistories() {
        return this.pnDocHistories;
    }

    public void setPnDocHistories(Set pnDocHistories) {
        this.pnDocHistories = pnDocHistories;
    }

    public Set getPnDocCheckoutLocations() {
        return this.pnDocCheckoutLocations;
    }

    public void setPnDocCheckoutLocations(Set pnDocCheckoutLocations) {
        this.pnDocCheckoutLocations = pnDocCheckoutLocations;
    }

    public Set getPnPersonAuthenticators() {
        return this.pnPersonAuthenticators;
    }

    public void setPnPersonAuthenticators(Set pnPersonAuthenticators) {
        this.pnPersonAuthenticators = pnPersonAuthenticators;
    }

    public Set getPnSpaceHasPersons() {
        return this.pnSpaceHasPersons;
    }

    public void setPnSpaceHasPersons(Set pnSpaceHasPersons) {
        this.pnSpaceHasPersons = pnSpaceHasPersons;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .toString();
    }

}
