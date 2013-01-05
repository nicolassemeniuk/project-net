package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGroup implements Serializable {

    /** identifier field */
    private BigDecimal groupId;

    /** persistent field */
    private String groupName;

    /** nullable persistent field */
    private String groupDesc;

    /** persistent field */
    private int isPrincipal;

    /** persistent field */
    private int isSystemGroup;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnGroupType pnGroupType;

    /** persistent field */
    private Set pnSpaceHasGroups;

    /** persistent field */
    private Set pnGroupHasPersons;

    /** persistent field */
    private Set pnWorkflowStepHasGroups;

    /** persistent field */
    private Set pnDefaultObjectPermissions;

    /** persistent field */
    private Set pnObjectPermissions;

    /** persistent field */
    private Set pnPagePermissions;

    /** persistent field */
    private Set pnGroupHistories;

    /** persistent field */
    private Set pnModulePermissions;

    /** persistent field */
    private Set pnGroupHasGroupsByMemberGroupId;

    /** persistent field */
    private Set pnGroupHasGroupsByGroupId;

    /** full constructor */
    public PnGroup(BigDecimal groupId, String groupName, String groupDesc, int isPrincipal, int isSystemGroup, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnGroupType pnGroupType, Set pnSpaceHasGroups, Set pnGroupHasPersons, Set pnWorkflowStepHasGroups, Set pnDefaultObjectPermissions, Set pnObjectPermissions, Set pnPagePermissions, Set pnGroupHistories, Set pnModulePermissions, Set pnGroupHasGroupsByMemberGroupId, Set pnGroupHasGroupsByGroupId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.isPrincipal = isPrincipal;
        this.isSystemGroup = isSystemGroup;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnPerson = pnPerson;
        this.pnGroupType = pnGroupType;
        this.pnSpaceHasGroups = pnSpaceHasGroups;
        this.pnGroupHasPersons = pnGroupHasPersons;
        this.pnWorkflowStepHasGroups = pnWorkflowStepHasGroups;
        this.pnDefaultObjectPermissions = pnDefaultObjectPermissions;
        this.pnObjectPermissions = pnObjectPermissions;
        this.pnPagePermissions = pnPagePermissions;
        this.pnGroupHistories = pnGroupHistories;
        this.pnModulePermissions = pnModulePermissions;
        this.pnGroupHasGroupsByMemberGroupId = pnGroupHasGroupsByMemberGroupId;
        this.pnGroupHasGroupsByGroupId = pnGroupHasGroupsByGroupId;
    }

    /** default constructor */
    public PnGroup() {
    }

    /** minimal constructor */
    public PnGroup(BigDecimal groupId, String groupName, int isPrincipal, int isSystemGroup, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnGroupType pnGroupType, Set pnSpaceHasGroups, Set pnGroupHasPersons, Set pnWorkflowStepHasGroups, Set pnDefaultObjectPermissions, Set pnObjectPermissions, Set pnPagePermissions, Set pnGroupHistories, Set pnModulePermissions, Set pnGroupHasGroupsByMemberGroupId, Set pnGroupHasGroupsByGroupId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.isPrincipal = isPrincipal;
        this.isSystemGroup = isSystemGroup;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnGroupType = pnGroupType;
        this.pnSpaceHasGroups = pnSpaceHasGroups;
        this.pnGroupHasPersons = pnGroupHasPersons;
        this.pnWorkflowStepHasGroups = pnWorkflowStepHasGroups;
        this.pnDefaultObjectPermissions = pnDefaultObjectPermissions;
        this.pnObjectPermissions = pnObjectPermissions;
        this.pnPagePermissions = pnPagePermissions;
        this.pnGroupHistories = pnGroupHistories;
        this.pnModulePermissions = pnModulePermissions;
        this.pnGroupHasGroupsByMemberGroupId = pnGroupHasGroupsByMemberGroupId;
        this.pnGroupHasGroupsByGroupId = pnGroupHasGroupsByGroupId;
    }

    public PnGroup(BigDecimal groupId, String groupName, String groupDesc, int isPrincipal, int isSystemGroup, String recordStatus,  BigDecimal groupTypeId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.isPrincipal = isPrincipal;
        this.isSystemGroup = isSystemGroup;
        this.recordStatus = recordStatus;
        this.pnGroupType = new PnGroupType(groupTypeId);
    }
    
    public BigDecimal getGroupId() {
        return this.groupId;
    }

    public void setGroupId(BigDecimal groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDesc() {
        return this.groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public int getIsPrincipal() {
        return this.isPrincipal;
    }

    public void setIsPrincipal(int isPrincipal) {
        this.isPrincipal = isPrincipal;
    }

    public int getIsSystemGroup() {
        return this.isSystemGroup;
    }

    public void setIsSystemGroup(int isSystemGroup) {
        this.isSystemGroup = isSystemGroup;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnGroupType getPnGroupType() {
        return this.pnGroupType;
    }

    public void setPnGroupType(net.project.hibernate.model.PnGroupType pnGroupType) {
        this.pnGroupType = pnGroupType;
    }

    public Set getPnSpaceHasGroups() {
        return this.pnSpaceHasGroups;
    }

    public void setPnSpaceHasGroups(Set pnSpaceHasGroups) {
        this.pnSpaceHasGroups = pnSpaceHasGroups;
    }

    public Set getPnGroupHasPersons() {
        return this.pnGroupHasPersons;
    }

    public void setPnGroupHasPersons(Set pnGroupHasPersons) {
        this.pnGroupHasPersons = pnGroupHasPersons;
    }

    public Set getPnWorkflowStepHasGroups() {
        return this.pnWorkflowStepHasGroups;
    }

    public void setPnWorkflowStepHasGroups(Set pnWorkflowStepHasGroups) {
        this.pnWorkflowStepHasGroups = pnWorkflowStepHasGroups;
    }

    public Set getPnDefaultObjectPermissions() {
        return this.pnDefaultObjectPermissions;
    }

    public void setPnDefaultObjectPermissions(Set pnDefaultObjectPermissions) {
        this.pnDefaultObjectPermissions = pnDefaultObjectPermissions;
    }

    public Set getPnObjectPermissions() {
        return this.pnObjectPermissions;
    }

    public void setPnObjectPermissions(Set pnObjectPermissions) {
        this.pnObjectPermissions = pnObjectPermissions;
    }

    public Set getPnPagePermissions() {
        return this.pnPagePermissions;
    }

    public void setPnPagePermissions(Set pnPagePermissions) {
        this.pnPagePermissions = pnPagePermissions;
    }

    public Set getPnGroupHistories() {
        return this.pnGroupHistories;
    }

    public void setPnGroupHistories(Set pnGroupHistories) {
        this.pnGroupHistories = pnGroupHistories;
    }

    public Set getPnModulePermissions() {
        return this.pnModulePermissions;
    }

    public void setPnModulePermissions(Set pnModulePermissions) {
        this.pnModulePermissions = pnModulePermissions;
    }

    public Set getPnGroupHasGroupsByMemberGroupId() {
        return this.pnGroupHasGroupsByMemberGroupId;
    }

    public void setPnGroupHasGroupsByMemberGroupId(Set pnGroupHasGroupsByMemberGroupId) {
        this.pnGroupHasGroupsByMemberGroupId = pnGroupHasGroupsByMemberGroupId;
    }

    public Set getPnGroupHasGroupsByGroupId() {
        return this.pnGroupHasGroupsByGroupId;
    }

    public void setPnGroupHasGroupsByGroupId(Set pnGroupHasGroupsByGroupId) {
        this.pnGroupHasGroupsByGroupId = pnGroupHasGroupsByGroupId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("groupId", getGroupId())
            .toString();
    }

}
