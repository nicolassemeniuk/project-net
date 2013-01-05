package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDirectoryField implements Serializable {

    /** identifier field */
    private BigDecimal directoryFieldId;

    /** persistent field */
    private int isLocallyStored;

    /** nullable persistent field */
    private String directoryFieldName;

    /** nullable persistent field */
    private String pnPersonColumnName;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnDirectory pnDirectory;

    /** persistent field */
    private Set pnSpaceHasDirectoryFields;

    /** full constructor */
    public PnDirectoryField(BigDecimal directoryFieldId, int isLocallyStored, String directoryFieldName, String pnPersonColumnName, String recordStatus, net.project.hibernate.model.PnDirectory pnDirectory, Set pnSpaceHasDirectoryFields) {
        this.directoryFieldId = directoryFieldId;
        this.isLocallyStored = isLocallyStored;
        this.directoryFieldName = directoryFieldName;
        this.pnPersonColumnName = pnPersonColumnName;
        this.recordStatus = recordStatus;
        this.pnDirectory = pnDirectory;
        this.pnSpaceHasDirectoryFields = pnSpaceHasDirectoryFields;
    }

    /** default constructor */
    public PnDirectoryField() {
    }

    /** minimal constructor */
    public PnDirectoryField(BigDecimal directoryFieldId, int isLocallyStored, String recordStatus, net.project.hibernate.model.PnDirectory pnDirectory, Set pnSpaceHasDirectoryFields) {
        this.directoryFieldId = directoryFieldId;
        this.isLocallyStored = isLocallyStored;
        this.recordStatus = recordStatus;
        this.pnDirectory = pnDirectory;
        this.pnSpaceHasDirectoryFields = pnSpaceHasDirectoryFields;
    }

    public BigDecimal getDirectoryFieldId() {
        return this.directoryFieldId;
    }

    public void setDirectoryFieldId(BigDecimal directoryFieldId) {
        this.directoryFieldId = directoryFieldId;
    }

    public int getIsLocallyStored() {
        return this.isLocallyStored;
    }

    public void setIsLocallyStored(int isLocallyStored) {
        this.isLocallyStored = isLocallyStored;
    }

    public String getDirectoryFieldName() {
        return this.directoryFieldName;
    }

    public void setDirectoryFieldName(String directoryFieldName) {
        this.directoryFieldName = directoryFieldName;
    }

    public String getPnPersonColumnName() {
        return this.pnPersonColumnName;
    }

    public void setPnPersonColumnName(String pnPersonColumnName) {
        this.pnPersonColumnName = pnPersonColumnName;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnDirectory getPnDirectory() {
        return this.pnDirectory;
    }

    public void setPnDirectory(net.project.hibernate.model.PnDirectory pnDirectory) {
        this.pnDirectory = pnDirectory;
    }

    public Set getPnSpaceHasDirectoryFields() {
        return this.pnSpaceHasDirectoryFields;
    }

    public void setPnSpaceHasDirectoryFields(Set pnSpaceHasDirectoryFields) {
        this.pnSpaceHasDirectoryFields = pnSpaceHasDirectoryFields;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("directoryFieldId", getDirectoryFieldId())
            .toString();
    }

}
