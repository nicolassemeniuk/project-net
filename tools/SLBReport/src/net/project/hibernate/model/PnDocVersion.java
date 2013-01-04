package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocVersion implements Serializable {

    /** identifier field */
    private BigDecimal docVersionId;

    /** nullable persistent field */
    private String docVersionName;

    /** nullable persistent field */
    private String sourceFileName;

    /** nullable persistent field */
    private String docDescription;

    /** nullable persistent field */
    private Date dateModified;

    /** persistent field */
    private int isCheckedOut;

    /** nullable persistent field */
    private Date dateCheckedOut;

    /** nullable persistent field */
    private String docComment;

    /** persistent field */
    private BigDecimal docVersionNum;

    /** nullable persistent field */
    private String docVersionLabel;

    /** nullable persistent field */
    private Date checkoutDue;

    /** nullable persistent field */
    private String shortFileName;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnDocument pnDocument;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByDocAuthorId;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByModifiedById;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPersonByCheckedOutById;

    /** persistent field */
    private Set pnDocVersionHasContents;

    /** persistent field */
    private Set pnDocConfigurationHasDocs;

    /** full constructor */
    public PnDocVersion(BigDecimal docVersionId, String docVersionName, String sourceFileName, String docDescription, Date dateModified, int isCheckedOut, Date dateCheckedOut, String docComment, BigDecimal docVersionNum, String docVersionLabel, Date checkoutDue, String shortFileName, Date crc, String recordStatus, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnPerson pnPersonByDocAuthorId, net.project.hibernate.model.PnPerson pnPersonByModifiedById, net.project.hibernate.model.PnPerson pnPersonByCheckedOutById, Set pnDocVersionHasContents, Set pnDocConfigurationHasDocs) {
        this.docVersionId = docVersionId;
        this.docVersionName = docVersionName;
        this.sourceFileName = sourceFileName;
        this.docDescription = docDescription;
        this.dateModified = dateModified;
        this.isCheckedOut = isCheckedOut;
        this.dateCheckedOut = dateCheckedOut;
        this.docComment = docComment;
        this.docVersionNum = docVersionNum;
        this.docVersionLabel = docVersionLabel;
        this.checkoutDue = checkoutDue;
        this.shortFileName = shortFileName;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnDocument = pnDocument;
        this.pnPersonByDocAuthorId = pnPersonByDocAuthorId;
        this.pnPersonByModifiedById = pnPersonByModifiedById;
        this.pnPersonByCheckedOutById = pnPersonByCheckedOutById;
        this.pnDocVersionHasContents = pnDocVersionHasContents;
        this.pnDocConfigurationHasDocs = pnDocConfigurationHasDocs;
    }

    /** default constructor */
    public PnDocVersion() {
    }

    /** minimal constructor */
    public PnDocVersion(BigDecimal docVersionId, int isCheckedOut, BigDecimal docVersionNum, Date crc, String recordStatus, net.project.hibernate.model.PnDocument pnDocument, net.project.hibernate.model.PnPerson pnPersonByDocAuthorId, net.project.hibernate.model.PnPerson pnPersonByModifiedById, net.project.hibernate.model.PnPerson pnPersonByCheckedOutById, Set pnDocVersionHasContents, Set pnDocConfigurationHasDocs) {
        this.docVersionId = docVersionId;
        this.isCheckedOut = isCheckedOut;
        this.docVersionNum = docVersionNum;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnDocument = pnDocument;
        this.pnPersonByDocAuthorId = pnPersonByDocAuthorId;
        this.pnPersonByModifiedById = pnPersonByModifiedById;
        this.pnPersonByCheckedOutById = pnPersonByCheckedOutById;
        this.pnDocVersionHasContents = pnDocVersionHasContents;
        this.pnDocConfigurationHasDocs = pnDocConfigurationHasDocs;
    }

    public BigDecimal getDocVersionId() {
        return this.docVersionId;
    }

    public void setDocVersionId(BigDecimal docVersionId) {
        this.docVersionId = docVersionId;
    }

    public String getDocVersionName() {
        return this.docVersionName;
    }

    public void setDocVersionName(String docVersionName) {
        this.docVersionName = docVersionName;
    }

    public String getSourceFileName() {
        return this.sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getDocDescription() {
        return this.docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    public Date getDateModified() {
        return this.dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public int getIsCheckedOut() {
        return this.isCheckedOut;
    }

    public void setIsCheckedOut(int isCheckedOut) {
        this.isCheckedOut = isCheckedOut;
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

    public Date getCheckoutDue() {
        return this.checkoutDue;
    }

    public void setCheckoutDue(Date checkoutDue) {
        this.checkoutDue = checkoutDue;
    }

    public String getShortFileName() {
        return this.shortFileName;
    }

    public void setShortFileName(String shortFileName) {
        this.shortFileName = shortFileName;
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

    public net.project.hibernate.model.PnDocument getPnDocument() {
        return this.pnDocument;
    }

    public void setPnDocument(net.project.hibernate.model.PnDocument pnDocument) {
        this.pnDocument = pnDocument;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByDocAuthorId() {
        return this.pnPersonByDocAuthorId;
    }

    public void setPnPersonByDocAuthorId(net.project.hibernate.model.PnPerson pnPersonByDocAuthorId) {
        this.pnPersonByDocAuthorId = pnPersonByDocAuthorId;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByModifiedById() {
        return this.pnPersonByModifiedById;
    }

    public void setPnPersonByModifiedById(net.project.hibernate.model.PnPerson pnPersonByModifiedById) {
        this.pnPersonByModifiedById = pnPersonByModifiedById;
    }

    public net.project.hibernate.model.PnPerson getPnPersonByCheckedOutById() {
        return this.pnPersonByCheckedOutById;
    }

    public void setPnPersonByCheckedOutById(net.project.hibernate.model.PnPerson pnPersonByCheckedOutById) {
        this.pnPersonByCheckedOutById = pnPersonByCheckedOutById;
    }

    public Set getPnDocVersionHasContents() {
        return this.pnDocVersionHasContents;
    }

    public void setPnDocVersionHasContents(Set pnDocVersionHasContents) {
        this.pnDocVersionHasContents = pnDocVersionHasContents;
    }

    public Set getPnDocConfigurationHasDocs() {
        return this.pnDocConfigurationHasDocs;
    }

    public void setPnDocConfigurationHasDocs(Set pnDocConfigurationHasDocs) {
        this.pnDocConfigurationHasDocs = pnDocConfigurationHasDocs;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docVersionId", getDocVersionId())
            .toString();
    }

}
