package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectType implements Serializable {

    /** identifier field */
    private String objectType;

    /** nullable persistent field */
    private String masterTableName;

    /** nullable persistent field */
    private String objectTypeDesc;

    /** nullable persistent field */
    private String parentObjectType;

    /** persistent field */
    private long defaultPermissionActions;

    /** persistent field */
    private int isSecurable;

    /** persistent field */
    private int isWorkflowable;

    /** nullable persistent field */
    private net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType;

    /** persistent field */
    private Set pnModuleHasObjectTypes;

    /** persistent field */
    private Set pnEventTypes;

    /** persistent field */
    private Set pnDefaultObjectPermissions;

    /** persistent field */
    private Set pnObjectTypeSupportsActions;

    /** persistent field */
    private Set pnObjects;

    /** persistent field */
    private Set pnWorkflowHasObjectTypes;

    /** full constructor */
    public PnObjectType(String objectType, String masterTableName, String objectTypeDesc, String parentObjectType, long defaultPermissionActions, int isSecurable, int isWorkflowable, net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType, Set pnModuleHasObjectTypes, Set pnEventTypes, Set pnDefaultObjectPermissions, Set pnObjectTypeSupportsActions, Set pnObjects, Set pnWorkflowHasObjectTypes) {
        this.objectType = objectType;
        this.masterTableName = masterTableName;
        this.objectTypeDesc = objectTypeDesc;
        this.parentObjectType = parentObjectType;
        this.defaultPermissionActions = defaultPermissionActions;
        this.isSecurable = isSecurable;
        this.isWorkflowable = isWorkflowable;
        this.pnNotificationObjectType = pnNotificationObjectType;
        this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
        this.pnEventTypes = pnEventTypes;
        this.pnDefaultObjectPermissions = pnDefaultObjectPermissions;
        this.pnObjectTypeSupportsActions = pnObjectTypeSupportsActions;
        this.pnObjects = pnObjects;
        this.pnWorkflowHasObjectTypes = pnWorkflowHasObjectTypes;
    }

    /** default constructor */
    public PnObjectType() {
    }
    
    public PnObjectType(String objectType) {
    	this.objectType = objectType;
    }
    
    public PnObjectType(String objectType, long defaultPermissionActions) {
    	this.objectType = objectType;
    	this.defaultPermissionActions = defaultPermissionActions;
    }    

    /** minimal constructor */
    public PnObjectType(String objectType, long defaultPermissionActions, int isSecurable, int isWorkflowable, Set pnModuleHasObjectTypes, Set pnEventTypes, Set pnDefaultObjectPermissions, Set pnObjectTypeSupportsActions, Set pnObjects, Set pnWorkflowHasObjectTypes) {
        this.objectType = objectType;
        this.defaultPermissionActions = defaultPermissionActions;
        this.isSecurable = isSecurable;
        this.isWorkflowable = isWorkflowable;
        this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
        this.pnEventTypes = pnEventTypes;
        this.pnDefaultObjectPermissions = pnDefaultObjectPermissions;
        this.pnObjectTypeSupportsActions = pnObjectTypeSupportsActions;
        this.pnObjects = pnObjects;
        this.pnWorkflowHasObjectTypes = pnWorkflowHasObjectTypes;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getMasterTableName() {
        return this.masterTableName;
    }

    public void setMasterTableName(String masterTableName) {
        this.masterTableName = masterTableName;
    }

    public String getObjectTypeDesc() {
        return this.objectTypeDesc;
    }

    public void setObjectTypeDesc(String objectTypeDesc) {
        this.objectTypeDesc = objectTypeDesc;
    }

    public String getParentObjectType() {
        return this.parentObjectType;
    }

    public void setParentObjectType(String parentObjectType) {
        this.parentObjectType = parentObjectType;
    }

    public long getDefaultPermissionActions() {
        return this.defaultPermissionActions;
    }

    public void setDefaultPermissionActions(long defaultPermissionActions) {
        this.defaultPermissionActions = defaultPermissionActions;
    }

    public int getIsSecurable() {
        return this.isSecurable;
    }

    public void setIsSecurable(int isSecurable) {
        this.isSecurable = isSecurable;
    }

    public int getIsWorkflowable() {
        return this.isWorkflowable;
    }

    public void setIsWorkflowable(int isWorkflowable) {
        this.isWorkflowable = isWorkflowable;
    }

    public net.project.hibernate.model.PnNotificationObjectType getPnNotificationObjectType() {
        return this.pnNotificationObjectType;
    }

    public void setPnNotificationObjectType(net.project.hibernate.model.PnNotificationObjectType pnNotificationObjectType) {
        this.pnNotificationObjectType = pnNotificationObjectType;
    }

    public Set getPnModuleHasObjectTypes() {
        return this.pnModuleHasObjectTypes;
    }

    public void setPnModuleHasObjectTypes(Set pnModuleHasObjectTypes) {
        this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
    }

    public Set getPnEventTypes() {
        return this.pnEventTypes;
    }

    public void setPnEventTypes(Set pnEventTypes) {
        this.pnEventTypes = pnEventTypes;
    }

    public Set getPnDefaultObjectPermissions() {
        return this.pnDefaultObjectPermissions;
    }

    public void setPnDefaultObjectPermissions(Set pnDefaultObjectPermissions) {
        this.pnDefaultObjectPermissions = pnDefaultObjectPermissions;
    }

    public Set getPnObjectTypeSupportsActions() {
        return this.pnObjectTypeSupportsActions;
    }

    public void setPnObjectTypeSupportsActions(Set pnObjectTypeSupportsActions) {
        this.pnObjectTypeSupportsActions = pnObjectTypeSupportsActions;
    }

    public Set getPnObjects() {
        return this.pnObjects;
    }

    public void setPnObjects(Set pnObjects) {
        this.pnObjects = pnObjects;
    }

    public Set getPnWorkflowHasObjectTypes() {
        return this.pnWorkflowHasObjectTypes;
    }

    public void setPnWorkflowHasObjectTypes(Set pnWorkflowHasObjectTypes) {
        this.pnWorkflowHasObjectTypes = pnWorkflowHasObjectTypes;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectType", getObjectType())
            .toString();
    }

}
