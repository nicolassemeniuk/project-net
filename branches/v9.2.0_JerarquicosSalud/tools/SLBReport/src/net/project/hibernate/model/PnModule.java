package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnModule implements Serializable {

    /** identifier field */
    private BigDecimal moduleId;

    /** persistent field */
    private String name;

    /** nullable persistent field */
    private String description;

    /** persistent field */
    private long defaultPermissionActions;

    /** persistent field */
    private Set pnModuleHasObjectTypes;

    /** persistent field */
    private Set pnSpaceHasModules;

    /** full constructor */
    public PnModule(BigDecimal moduleId, String name, String description, long defaultPermissionActions, Set pnModuleHasObjectTypes, Set pnSpaceHasModules) {
        this.moduleId = moduleId;
        this.name = name;
        this.description = description;
        this.defaultPermissionActions = defaultPermissionActions;
        this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
        this.pnSpaceHasModules = pnSpaceHasModules;
    }

    /** default constructor */
    public PnModule() {
    }

    /** minimal constructor */
    public PnModule(BigDecimal moduleId, String name, long defaultPermissionActions, Set pnModuleHasObjectTypes, Set pnSpaceHasModules) {
        this.moduleId = moduleId;
        this.name = name;
        this.defaultPermissionActions = defaultPermissionActions;
        this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
        this.pnSpaceHasModules = pnSpaceHasModules;
    }

    public PnModule(BigDecimal moduleId, long defaultPermissionActions) {
        this.moduleId = moduleId;
        this.defaultPermissionActions = defaultPermissionActions;
    }    

    public PnModule(BigDecimal moduleId) {
        this.moduleId = moduleId;
    } 
    
    public BigDecimal getModuleId() {
        return this.moduleId;
    }

    public void setModuleId(BigDecimal moduleId) {
        this.moduleId = moduleId;
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

    public long getDefaultPermissionActions() {
        return this.defaultPermissionActions;
    }

    public void setDefaultPermissionActions(long defaultPermissionActions) {
        this.defaultPermissionActions = defaultPermissionActions;
    }

    public Set getPnModuleHasObjectTypes() {
        return this.pnModuleHasObjectTypes;
    }

    public void setPnModuleHasObjectTypes(Set pnModuleHasObjectTypes) {
        this.pnModuleHasObjectTypes = pnModuleHasObjectTypes;
    }

    public Set getPnSpaceHasModules() {
        return this.pnSpaceHasModules;
    }

    public void setPnSpaceHasModules(Set pnSpaceHasModules) {
        this.pnSpaceHasModules = pnSpaceHasModules;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("moduleId", getModuleId())
            .toString();
    }

}
