package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDirectory implements Serializable {

    /** identifier field */
    private BigDecimal directoryId;

    /** nullable persistent field */
    private String directoryName;

    /** nullable persistent field */
    private String directoryDesc;

    /** nullable persistent field */
    private BigDecimal displayClassId;

    /** nullable persistent field */
    private BigDecimal directoryTypeId;

    /** nullable persistent field */
    private String directoryHost;

    /** nullable persistent field */
    private String directoryVendor;

    /** nullable persistent field */
    private String bindUsername;

    /** nullable persistent field */
    private String directoryUrl;

    /** nullable persistent field */
    private String bindPassword;

    /** nullable persistent field */
    private String searchRoot;

    /** nullable persistent field */
    private String directoryPort;

    /** nullable persistent field */
    private String directorySecurePort;

    /** nullable persistent field */
    private String directoryCipherKey;

    /** nullable persistent field */
    private Integer connectSecure;

    /** persistent field */
    private int isDefault;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private Set pnSpaceHasDirectories;

    /** persistent field */
    private Set pnDirectoryFields;

    /** persistent field */
    private Set pnSpaceHasDirectoryFields;

    /** persistent field */
    private Set pnDirectoryHasPersons;

    /** full constructor */
    public PnDirectory(BigDecimal directoryId, String directoryName, String directoryDesc, BigDecimal displayClassId, BigDecimal directoryTypeId, String directoryHost, String directoryVendor, String bindUsername, String directoryUrl, String bindPassword, String searchRoot, String directoryPort, String directorySecurePort, String directoryCipherKey, Integer connectSecure, int isDefault, String recordStatus, Set pnSpaceHasDirectories, Set pnDirectoryFields, Set pnSpaceHasDirectoryFields, Set pnDirectoryHasPersons) {
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.directoryDesc = directoryDesc;
        this.displayClassId = displayClassId;
        this.directoryTypeId = directoryTypeId;
        this.directoryHost = directoryHost;
        this.directoryVendor = directoryVendor;
        this.bindUsername = bindUsername;
        this.directoryUrl = directoryUrl;
        this.bindPassword = bindPassword;
        this.searchRoot = searchRoot;
        this.directoryPort = directoryPort;
        this.directorySecurePort = directorySecurePort;
        this.directoryCipherKey = directoryCipherKey;
        this.connectSecure = connectSecure;
        this.isDefault = isDefault;
        this.recordStatus = recordStatus;
        this.pnSpaceHasDirectories = pnSpaceHasDirectories;
        this.pnDirectoryFields = pnDirectoryFields;
        this.pnSpaceHasDirectoryFields = pnSpaceHasDirectoryFields;
        this.pnDirectoryHasPersons = pnDirectoryHasPersons;
    }

    public PnDirectory(BigDecimal directoryId, int isDefault) {
        this.directoryId = directoryId;
        this.isDefault = isDefault;
    }
    
    /** default constructor */
    public PnDirectory() {
    }

    /** minimal constructor */
    public PnDirectory(BigDecimal directoryId, int isDefault, String recordStatus, Set pnSpaceHasDirectories, Set pnDirectoryFields, Set pnSpaceHasDirectoryFields, Set pnDirectoryHasPersons) {
        this.directoryId = directoryId;
        this.isDefault = isDefault;
        this.recordStatus = recordStatus;
        this.pnSpaceHasDirectories = pnSpaceHasDirectories;
        this.pnDirectoryFields = pnDirectoryFields;
        this.pnSpaceHasDirectoryFields = pnSpaceHasDirectoryFields;
        this.pnDirectoryHasPersons = pnDirectoryHasPersons;
    }

    public BigDecimal getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(BigDecimal directoryId) {
        this.directoryId = directoryId;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryDesc() {
        return this.directoryDesc;
    }

    public void setDirectoryDesc(String directoryDesc) {
        this.directoryDesc = directoryDesc;
    }

    public BigDecimal getDisplayClassId() {
        return this.displayClassId;
    }

    public void setDisplayClassId(BigDecimal displayClassId) {
        this.displayClassId = displayClassId;
    }

    public BigDecimal getDirectoryTypeId() {
        return this.directoryTypeId;
    }

    public void setDirectoryTypeId(BigDecimal directoryTypeId) {
        this.directoryTypeId = directoryTypeId;
    }

    public String getDirectoryHost() {
        return this.directoryHost;
    }

    public void setDirectoryHost(String directoryHost) {
        this.directoryHost = directoryHost;
    }

    public String getDirectoryVendor() {
        return this.directoryVendor;
    }

    public void setDirectoryVendor(String directoryVendor) {
        this.directoryVendor = directoryVendor;
    }

    public String getBindUsername() {
        return this.bindUsername;
    }

    public void setBindUsername(String bindUsername) {
        this.bindUsername = bindUsername;
    }

    public String getDirectoryUrl() {
        return this.directoryUrl;
    }

    public void setDirectoryUrl(String directoryUrl) {
        this.directoryUrl = directoryUrl;
    }

    public String getBindPassword() {
        return this.bindPassword;
    }

    public void setBindPassword(String bindPassword) {
        this.bindPassword = bindPassword;
    }

    public String getSearchRoot() {
        return this.searchRoot;
    }

    public void setSearchRoot(String searchRoot) {
        this.searchRoot = searchRoot;
    }

    public String getDirectoryPort() {
        return this.directoryPort;
    }

    public void setDirectoryPort(String directoryPort) {
        this.directoryPort = directoryPort;
    }

    public String getDirectorySecurePort() {
        return this.directorySecurePort;
    }

    public void setDirectorySecurePort(String directorySecurePort) {
        this.directorySecurePort = directorySecurePort;
    }

    public String getDirectoryCipherKey() {
        return this.directoryCipherKey;
    }

    public void setDirectoryCipherKey(String directoryCipherKey) {
        this.directoryCipherKey = directoryCipherKey;
    }

    public Integer getConnectSecure() {
        return this.connectSecure;
    }

    public void setConnectSecure(Integer connectSecure) {
        this.connectSecure = connectSecure;
    }

    public int getIsDefault() {
        return this.isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set getPnSpaceHasDirectories() {
        return this.pnSpaceHasDirectories;
    }

    public void setPnSpaceHasDirectories(Set pnSpaceHasDirectories) {
        this.pnSpaceHasDirectories = pnSpaceHasDirectories;
    }

    public Set getPnDirectoryFields() {
        return this.pnDirectoryFields;
    }

    public void setPnDirectoryFields(Set pnDirectoryFields) {
        this.pnDirectoryFields = pnDirectoryFields;
    }

    public Set getPnSpaceHasDirectoryFields() {
        return this.pnSpaceHasDirectoryFields;
    }

    public void setPnSpaceHasDirectoryFields(Set pnSpaceHasDirectoryFields) {
        this.pnSpaceHasDirectoryFields = pnSpaceHasDirectoryFields;
    }

    public Set getPnDirectoryHasPersons() {
        return this.pnDirectoryHasPersons;
    }

    public void setPnDirectoryHasPersons(Set pnDirectoryHasPersons) {
        this.pnDirectoryHasPersons = pnDirectoryHasPersons;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("directoryId", getDirectoryId())
            .toString();
    }

}
