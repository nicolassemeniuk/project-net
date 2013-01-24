package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocContainer implements Serializable {

    /** identifier field */
    private BigDecimal docContainerId;

    /** nullable persistent field */
    private String containerName;

    /** nullable persistent field */
    private String containerDescription;

    /** nullable persistent field */
    private Date dateModified;

    /** persistent field */
    private int isHidden;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private Set pnDocSpaceHasContainers;

    /** persistent field */
    private Set pnDocContainerHasObjects;

    /** full constructor */
    public PnDocContainer(BigDecimal docContainerId, String containerName, String containerDescription, Date dateModified, int isHidden, Date crc, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnPerson pnPerson, Set pnDocSpaceHasContainers, Set pnDocContainerHasObjects) {
        this.docContainerId = docContainerId;
        this.containerName = containerName;
        this.containerDescription = containerDescription;
        this.dateModified = dateModified;
        this.isHidden = isHidden;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnPerson = pnPerson;
        this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
        this.pnDocContainerHasObjects = pnDocContainerHasObjects;
    }

    /** default constructor */
    public PnDocContainer() {
    }

    public PnDocContainer(BigDecimal docContainerId, String containerName, String containerDescription,  Date dateModified, BigDecimal modifiedById, int isHidden, Date crc, String recordStatus) {
        this.docContainerId = docContainerId;
        this.containerName = containerName;
        this.containerDescription = containerDescription;
        this.dateModified = dateModified;
        this.pnPerson = new PnPerson(modifiedById);
        this.isHidden = isHidden;
        this.crc = crc;
        this.recordStatus = recordStatus;        
    }
    
    /** minimal constructor */
    public PnDocContainer(BigDecimal docContainerId, int isHidden, Date crc, String recordStatus, net.project.hibernate.model.PnPerson pnPerson, Set pnDocSpaceHasContainers, Set pnDocContainerHasObjects) {
        this.docContainerId = docContainerId;
        this.isHidden = isHidden;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnPerson = pnPerson;
        this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
        this.pnDocContainerHasObjects = pnDocContainerHasObjects;
    }

    public BigDecimal getDocContainerId() {
        return this.docContainerId;
    }

    public void setDocContainerId(BigDecimal docContainerId) {
        this.docContainerId = docContainerId;
    }

    public String getContainerName() {
        return this.containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerDescription() {
        return this.containerDescription;
    }

    public void setContainerDescription(String containerDescription) {
        this.containerDescription = containerDescription;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public int getIsHidden() {
        return this.isHidden;
    }

    public void setIsHidden(int isHidden) {
        this.isHidden = isHidden;
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

    public Set getPnDocSpaceHasContainers() {
        return this.pnDocSpaceHasContainers;
    }

    public void setPnDocSpaceHasContainers(Set pnDocSpaceHasContainers) {
        this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
    }

    public Set getPnDocContainerHasObjects() {
        return this.pnDocContainerHasObjects;
    }

    public void setPnDocContainerHasObjects(Set pnDocContainerHasObjects) {
        this.pnDocContainerHasObjects = pnDocContainerHasObjects;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docContainerId", getDocContainerId())
            .toString();
    }

}
