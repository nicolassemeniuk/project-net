package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocument implements Serializable {

    /** identifier field */
    private BigDecimal docId;

    /** nullable persistent field */
    private String docName;

    /** nullable persistent field */
    private String docDescription;

    /** persistent field */
    private BigDecimal currentVersionId;

    /** nullable persistent field */
    private BigDecimal docStatusId;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private BigDecimal oldStorageId;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnDocType pnDocType;

    /** persistent field */
    private Set pnProjectSpaces;

    /** persistent field */
    private Set pnDocHistories;

    /** persistent field */
    private Set pnDocCheckoutLocations;

    /** persistent field */
    private Set pnDocVersions;

    /** full constructor */
    public PnDocument(BigDecimal docId, String docName, String docDescription, BigDecimal currentVersionId, BigDecimal docStatusId, Date crc, String recordStatus, BigDecimal oldStorageId, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnDocType pnDocType, Set pnProjectSpaces, Set pnDocHistories, Set pnDocCheckoutLocations, Set pnDocVersions) {
        this.docId = docId;
        this.docName = docName;
        this.docDescription = docDescription;
        this.currentVersionId = currentVersionId;
        this.docStatusId = docStatusId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.oldStorageId = oldStorageId;
        this.pnObject = pnObject;
        this.pnDocType = pnDocType;
        this.pnProjectSpaces = pnProjectSpaces;
        this.pnDocHistories = pnDocHistories;
        this.pnDocCheckoutLocations = pnDocCheckoutLocations;
        this.pnDocVersions = pnDocVersions;
    }

    /** default constructor */
    public PnDocument() {
    }

    /** minimal constructor */
    public PnDocument(BigDecimal docId, BigDecimal currentVersionId, Date crc, String recordStatus, net.project.hibernate.model.PnDocType pnDocType, Set pnProjectSpaces, Set pnDocHistories, Set pnDocCheckoutLocations, Set pnDocVersions) {
        this.docId = docId;
        this.currentVersionId = currentVersionId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnDocType = pnDocType;
        this.pnProjectSpaces = pnProjectSpaces;
        this.pnDocHistories = pnDocHistories;
        this.pnDocCheckoutLocations = pnDocCheckoutLocations;
        this.pnDocVersions = pnDocVersions;
    }

    public BigDecimal getDocId() {
        return this.docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public String getDocName() {
        return this.docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocDescription() {
        return this.docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    public BigDecimal getCurrentVersionId() {
        return this.currentVersionId;
    }

    public void setCurrentVersionId(BigDecimal currentVersionId) {
        this.currentVersionId = currentVersionId;
    }

    public BigDecimal getDocStatusId() {
        return this.docStatusId;
    }

    public void setDocStatusId(BigDecimal docStatusId) {
        this.docStatusId = docStatusId;
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

    public BigDecimal getOldStorageId() {
        return this.oldStorageId;
    }

    public void setOldStorageId(BigDecimal oldStorageId) {
        this.oldStorageId = oldStorageId;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnDocType getPnDocType() {
        return this.pnDocType;
    }

    public void setPnDocType(net.project.hibernate.model.PnDocType pnDocType) {
        this.pnDocType = pnDocType;
    }

    public Set getPnProjectSpaces() {
        return this.pnProjectSpaces;
    }

    public void setPnProjectSpaces(Set pnProjectSpaces) {
        this.pnProjectSpaces = pnProjectSpaces;
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

    public Set getPnDocVersions() {
        return this.pnDocVersions;
    }

    public void setPnDocVersions(Set pnDocVersions) {
        this.pnDocVersions = pnDocVersions;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docId", getDocId())
            .toString();
    }

}
