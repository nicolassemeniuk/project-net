package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnTmpDocument implements Serializable {

    /** identifier field */
    private BigDecimal tmpDocId;

    /** nullable persistent field */
    private BigDecimal docId;

    /** nullable persistent field */
    private BigDecimal docVersionId;

    /** nullable persistent field */
    private String docName;

    /** nullable persistent field */
    private String docDescription;

    /** nullable persistent field */
    private BigDecimal currentVersionId;

    /** nullable persistent field */
    private String docVersionName;

    /** nullable persistent field */
    private String verDocDescription;

    /** nullable persistent field */
    private String sourceFileName;

    /** nullable persistent field */
    private BigDecimal modifiedById;

    /** nullable persistent field */
    private Date dateModified;

    /** nullable persistent field */
    private Integer isCheckedOut;

    /** nullable persistent field */
    private BigDecimal checkedOutById;

    /** nullable persistent field */
    private Date dateCheckedOut;

    /** nullable persistent field */
    private String docComment;

    /** nullable persistent field */
    private String verRecordStatus;

    /** nullable persistent field */
    private BigDecimal docContentId;

    /** nullable persistent field */
    private BigDecimal docFormatId;

    /** nullable persistent field */
    private Integer displaySequence;

    /** nullable persistent field */
    private String contentRecordStatus;

    /** nullable persistent field */
    private BigDecimal docVersionNum;

    /** nullable persistent field */
    private String docVersionLabel;

    /** nullable persistent field */
    private BigDecimal fileSize;

    /** nullable persistent field */
    private String fileHandle;

    /** nullable persistent field */
    private BigDecimal docHistoryId;

    /** nullable persistent field */
    private BigDecimal actionId;

    /** nullable persistent field */
    private BigDecimal actionById;

    /** nullable persistent field */
    private String actionComment;

    /** nullable persistent field */
    private String actionDesc;

    /** nullable persistent field */
    private BigDecimal docContainerId;

    /** nullable persistent field */
    private Date checkoutDue;

    /** nullable persistent field */
    private BigDecimal docStatusId;

    /** nullable persistent field */
    private BigDecimal docTypeId;

    /** nullable persistent field */
    private BigDecimal docAuthorId;

    /** nullable persistent field */
    private String shortFileName;

    /** nullable persistent field */
    private Integer repositoryId;

    /** nullable persistent field */
    private String recordStatus;

    /** full constructor */
    public PnTmpDocument(BigDecimal tmpDocId, BigDecimal docId, BigDecimal docVersionId, String docName, String docDescription, BigDecimal currentVersionId, String docVersionName, String verDocDescription, String sourceFileName, BigDecimal modifiedById, Date dateModified, Integer isCheckedOut, BigDecimal checkedOutById, Date dateCheckedOut, String docComment, String verRecordStatus, BigDecimal docContentId, BigDecimal docFormatId, Integer displaySequence, String contentRecordStatus, BigDecimal docVersionNum, String docVersionLabel, BigDecimal fileSize, String fileHandle, BigDecimal docHistoryId, BigDecimal actionId, BigDecimal actionById, String actionComment, String actionDesc, BigDecimal docContainerId, Date checkoutDue, BigDecimal docStatusId, BigDecimal docTypeId, BigDecimal docAuthorId, String shortFileName, Integer repositoryId, String recordStatus) {
        this.tmpDocId = tmpDocId;
        this.docId = docId;
        this.docVersionId = docVersionId;
        this.docName = docName;
        this.docDescription = docDescription;
        this.currentVersionId = currentVersionId;
        this.docVersionName = docVersionName;
        this.verDocDescription = verDocDescription;
        this.sourceFileName = sourceFileName;
        this.modifiedById = modifiedById;
        this.dateModified = dateModified;
        this.isCheckedOut = isCheckedOut;
        this.checkedOutById = checkedOutById;
        this.dateCheckedOut = dateCheckedOut;
        this.docComment = docComment;
        this.verRecordStatus = verRecordStatus;
        this.docContentId = docContentId;
        this.docFormatId = docFormatId;
        this.displaySequence = displaySequence;
        this.contentRecordStatus = contentRecordStatus;
        this.docVersionNum = docVersionNum;
        this.docVersionLabel = docVersionLabel;
        this.fileSize = fileSize;
        this.fileHandle = fileHandle;
        this.docHistoryId = docHistoryId;
        this.actionId = actionId;
        this.actionById = actionById;
        this.actionComment = actionComment;
        this.actionDesc = actionDesc;
        this.docContainerId = docContainerId;
        this.checkoutDue = checkoutDue;
        this.docStatusId = docStatusId;
        this.docTypeId = docTypeId;
        this.docAuthorId = docAuthorId;
        this.shortFileName = shortFileName;
        this.repositoryId = repositoryId;
        this.recordStatus = recordStatus;
    }

    /** default constructor */
    public PnTmpDocument() {
    }

    /** minimal constructor */
    public PnTmpDocument(BigDecimal tmpDocId) {
        this.tmpDocId = tmpDocId;
    }

    public BigDecimal getTmpDocId() {
        return this.tmpDocId;
    }

    public void setTmpDocId(BigDecimal tmpDocId) {
        this.tmpDocId = tmpDocId;
    }

    public BigDecimal getDocId() {
        return this.docId;
    }

    public void setDocId(BigDecimal docId) {
        this.docId = docId;
    }

    public BigDecimal getDocVersionId() {
        return this.docVersionId;
    }

    public void setDocVersionId(BigDecimal docVersionId) {
        this.docVersionId = docVersionId;
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

    public String getDocVersionName() {
        return this.docVersionName;
    }

    public void setDocVersionName(String docVersionName) {
        this.docVersionName = docVersionName;
    }

    public String getVerDocDescription() {
        return this.verDocDescription;
    }

    public void setVerDocDescription(String verDocDescription) {
        this.verDocDescription = verDocDescription;
    }

    public String getSourceFileName() {
        return this.sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public BigDecimal getModifiedById() {
        return this.modifiedById;
    }

    public void setModifiedById(BigDecimal modifiedById) {
        this.modifiedById = modifiedById;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getIsCheckedOut() {
        return this.isCheckedOut;
    }

    public void setIsCheckedOut(Integer isCheckedOut) {
        this.isCheckedOut = isCheckedOut;
    }

    public BigDecimal getCheckedOutById() {
        return this.checkedOutById;
    }

    public void setCheckedOutById(BigDecimal checkedOutById) {
        this.checkedOutById = checkedOutById;
    }

    public Date getDateCheckedOut() {
        return this.dateCheckedOut;
    }

    public void setDateCheckedOut(Date dateCheckedOut) {
        this.dateCheckedOut = dateCheckedOut;
    }

    public String getDocComment() {
        return this.docComment;
    }

    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }

    public String getVerRecordStatus() {
        return this.verRecordStatus;
    }

    public void setVerRecordStatus(String verRecordStatus) {
        this.verRecordStatus = verRecordStatus;
    }

    public BigDecimal getDocContentId() {
        return this.docContentId;
    }

    public void setDocContentId(BigDecimal docContentId) {
        this.docContentId = docContentId;
    }

    public BigDecimal getDocFormatId() {
        return this.docFormatId;
    }

    public void setDocFormatId(BigDecimal docFormatId) {
        this.docFormatId = docFormatId;
    }

    public Integer getDisplaySequence() {
        return this.displaySequence;
    }

    public void setDisplaySequence(Integer displaySequence) {
        this.displaySequence = displaySequence;
    }

    public String getContentRecordStatus() {
        return this.contentRecordStatus;
    }

    public void setContentRecordStatus(String contentRecordStatus) {
        this.contentRecordStatus = contentRecordStatus;
    }

    public BigDecimal getDocVersionNum() {
        return this.docVersionNum;
    }

    public void setDocVersionNum(BigDecimal docVersionNum) {
        this.docVersionNum = docVersionNum;
    }

    public String getDocVersionLabel() {
        return this.docVersionLabel;
    }

    public void setDocVersionLabel(String docVersionLabel) {
        this.docVersionLabel = docVersionLabel;
    }

    public BigDecimal getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(BigDecimal fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHandle() {
        return this.fileHandle;
    }

    public void setFileHandle(String fileHandle) {
        this.fileHandle = fileHandle;
    }

    public BigDecimal getDocHistoryId() {
        return this.docHistoryId;
    }

    public void setDocHistoryId(BigDecimal docHistoryId) {
        this.docHistoryId = docHistoryId;
    }

    public BigDecimal getActionId() {
        return this.actionId;
    }

    public void setActionId(BigDecimal actionId) {
        this.actionId = actionId;
    }

    public BigDecimal getActionById() {
        return this.actionById;
    }

    public void setActionById(BigDecimal actionById) {
        this.actionById = actionById;
    }

    public String getActionComment() {
        return this.actionComment;
    }

    public void setActionComment(String actionComment) {
        this.actionComment = actionComment;
    }

    public String getActionDesc() {
        return this.actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public BigDecimal getDocContainerId() {
        return this.docContainerId;
    }

    public void setDocContainerId(BigDecimal docContainerId) {
        this.docContainerId = docContainerId;
    }

    public Date getCheckoutDue() {
        return this.checkoutDue;
    }

    public void setCheckoutDue(Date checkoutDue) {
        this.checkoutDue = checkoutDue;
    }

    public BigDecimal getDocStatusId() {
        return this.docStatusId;
    }

    public void setDocStatusId(BigDecimal docStatusId) {
        this.docStatusId = docStatusId;
    }

    public BigDecimal getDocTypeId() {
        return this.docTypeId;
    }

    public void setDocTypeId(BigDecimal docTypeId) {
        this.docTypeId = docTypeId;
    }

    public BigDecimal getDocAuthorId() {
        return this.docAuthorId;
    }

    public void setDocAuthorId(BigDecimal docAuthorId) {
        this.docAuthorId = docAuthorId;
    }

    public String getShortFileName() {
        return this.shortFileName;
    }

    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
    }

    public Integer getRepositoryId() {
        return this.repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("tmpDocId", getTmpDocId())
            .toString();
    }

}
