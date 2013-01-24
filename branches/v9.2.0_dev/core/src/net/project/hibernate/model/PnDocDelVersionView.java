/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.hibernate.model;
// Generated Jun 13, 2009 11:41:49 PM by Hibernate Tools 3.2.4.GA


import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnDocDelVersionView generated by hbm2java
 */
@Entity
@Table(name="PN_DOC_DEL_VERSION_VIEW"
)
public class PnDocDelVersionView  implements java.io.Serializable {

    /** identifier field */
    private BigDecimal versionId;

    /** identifier field */
    private BigDecimal documentId;

    /** identifier field */
    private String documentName;

    /** identifier field */
    private BigDecimal docVersionNum;

    /** identifier field */
    private String docVersionLabel;

    /** identifier field */
    private Date dateModified;

    /** identifier field */
    private String modifiedBy;

    /** identifier field */
    private String docComment;

    /** identifier field */
    private String sourceFileName;

    /** identifier field */
    private String shortFileName;

    /** identifier field */
    private BigDecimal docAuthorId;

    /** identifier field */
    private String author;
     private int isCheckedOut;
     private BigDecimal checkedOutById;
     private String ckoBy;
     private Date dateCheckedOut;
     private Date ckoDue;
     private String recordStatus;
     private int repositoryId;
    /** identifier field */
    private String repositoryPath;

    /** identifier field */
    private String fileHandle;

    public PnDocDelVersionView() {
    }

    public PnDocDelVersionView(BigDecimal versionId, BigDecimal documentId, String documentName, BigDecimal docVersionNum, String docVersionLabel, Date dateModified, String modifiedBy, String docComment, String sourceFileName, String shortFileName, BigDecimal docAuthorId, String author, int isCheckedOut, BigDecimal checkedOutById, String ckoBy, Date dateCheckedOut, Date ckoDue, String recordStatus, int repositoryId, String repositoryPath, String fileHandle) {
       this.versionId = versionId;
       this.documentId = documentId;
       this.documentName = documentName;
       this.docVersionNum = docVersionNum;
       this.docVersionLabel = docVersionLabel;
       this.dateModified = dateModified;
       this.modifiedBy = modifiedBy;
       this.docComment = docComment;
       this.sourceFileName = sourceFileName;
       this.shortFileName = shortFileName;
       this.docAuthorId = docAuthorId;
       this.author = author;
       this.isCheckedOut = isCheckedOut;
       this.checkedOutById = checkedOutById;
       this.ckoBy = ckoBy;
       this.dateCheckedOut = dateCheckedOut;
       this.ckoDue = ckoDue;
       this.recordStatus = recordStatus;
       this.repositoryId = repositoryId;
       this.repositoryPath = repositoryPath;
       this.fileHandle = fileHandle;
    }
   

    
    @Column(name="VERSION_ID", nullable=false, length=20)
    public BigDecimal getVersionId() {
        return this.versionId;
    }
    
    public void setVersionId(BigDecimal versionId) {
        this.versionId = versionId;
    }

    
    @Column(name="DOCUMENT_ID", nullable=false, length=20)
    public BigDecimal getDocumentId() {
        return this.documentId;
    }
    
    public void setDocumentId(BigDecimal documentId) {
        this.documentId = documentId;
    }

    
    @Column(name="DOCUMENT_NAME", nullable=false, length=256)
    public String getDocumentName() {
        return this.documentName;
    }
    
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    
    @Column(name="DOC_VERSION_NUM", nullable=false, length=20)
    public BigDecimal getDocVersionNum() {
        return this.docVersionNum;
    }
    
    public void setDocVersionNum(BigDecimal docVersionNum) {
        this.docVersionNum = docVersionNum;
    }

    
    @Column(name="DOC_VERSION_LABEL", nullable=false, length=240)
    public String getDocVersionLabel() {
        return this.docVersionLabel;
    }
    
    public void setDocVersionLabel(String docVersionLabel) {
        this.docVersionLabel = docVersionLabel;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="DATE_MODIFIED", nullable=false, length=7)
    public Date getDateModified() {
        return this.dateModified;
    }
    
    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    
    @Column(name="MODIFIED_BY", nullable=false, length=240)
    public String getModifiedBy() {
        return this.modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    
    @Column(name="DOC_COMMENT", nullable=false, length=500)
    public String getDocComment() {
        return this.docComment;
    }
    
    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }

    
    @Column(name="SOURCE_FILE_NAME", nullable=false, length=240)
    public String getSourceFileName() {
        return this.sourceFileName;
    }
    
    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    
    @Column(name="SHORT_FILE_NAME", nullable=false, length=240)
    public String getShortFileName() {
        return this.shortFileName;
    }
    
    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
    }

    
    @Column(name="DOC_AUTHOR_ID", nullable=false, length=20)
    public BigDecimal getDocAuthorId() {
        return this.docAuthorId;
    }
    
    public void setDocAuthorId(BigDecimal docAuthorId) {
        this.docAuthorId = docAuthorId;
    }

    
    @Column(name="AUTHOR", nullable=false, length=240)
    public String getAuthor() {
        return this.author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }

    
    @Column(name="IS_CHECKED_OUT", nullable=false, length=1)
    public int getIsCheckedOut() {
        return this.isCheckedOut;
    }
    
    public void setIsCheckedOut(int isCheckedOut) {
        this.isCheckedOut = isCheckedOut;
    }

    
    @Column(name="CHECKED_OUT_BY_ID", nullable=false, length=20)
    public BigDecimal getCheckedOutById() {
        return this.checkedOutById;
    }
    
    public void setCheckedOutById(BigDecimal checkedOutById) {
        this.checkedOutById = checkedOutById;
    }

    
    @Column(name="CKO_BY", nullable=false, length=240)
    public String getCkoBy() {
        return this.ckoBy;
    }
    
    public void setCkoBy(String ckoBy) {
        this.ckoBy = ckoBy;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="DATE_CHECKED_OUT", nullable=false, length=7)
    public Date getDateCheckedOut() {
        return this.dateCheckedOut;
    }
    
    public void setDateCheckedOut(Date dateCheckedOut) {
        this.dateCheckedOut = dateCheckedOut;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="CKO_DUE", nullable=false, length=7)
    public Date getCkoDue() {
        return this.ckoDue;
    }
    
    public void setCkoDue(Date ckoDue) {
        this.ckoDue = ckoDue;
    }

    
    @Column(name="RECORD_STATUS", nullable=false, length=1)
    public String getRecordStatus() {
        return this.recordStatus;
    }
    
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    
    @Column(name="REPOSITORY_ID", nullable=false, length=4)
    public int getRepositoryId() {
        return this.repositoryId;
    }
    
    public void setRepositoryId(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    
    @Column(name="REPOSITORY_PATH", nullable=false, length=240)
    public String getRepositoryPath() {
        return this.repositoryPath;
    }
    
    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    
    @Column(name="FILE_HANDLE", nullable=false, length=240)
    public String getFileHandle() {
        return this.fileHandle;
    }
    
    public void setFileHandle(String fileHandle) {
        this.fileHandle = fileHandle;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("versionId", getVersionId())
            .append("documentId", getDocumentId())
            .append("documentName", getDocumentName())
            .append("docVersionNum", getDocVersionNum())
            .append("docVersionLabel", getDocVersionLabel())
            .append("dateModified", getDateModified())
            .append("modifiedBy", getModifiedBy())
            .append("docComment", getDocComment())
            .append("sourceFileName", getSourceFileName())
            .append("shortFileName", getShortFileName())
            .append("docAuthorId", getDocAuthorId())
            .append("author", getAuthor())
            .append("isCheckedOut", getIsCheckedOut())
            .append("checkedOutById", getCheckedOutById())
            .append("ckoBy", getCkoBy())
            .append("dateCheckedOut", getDateCheckedOut())
            .append("ckoDue", getCkoDue())
            .append("recordStatus", getRecordStatus())
            .append("repositoryId", getRepositoryId())
            .append("repositoryPath", getRepositoryPath())
            .append("fileHandle", getFileHandle())
            .toString();
    }

}


