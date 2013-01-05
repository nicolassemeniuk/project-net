package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocContentElement implements Serializable {

    /** identifier field */
    private BigDecimal docContentId;

    /** nullable persistent field */
    private BigDecimal docFormatId;

    /** nullable persistent field */
    private Integer displaySequence;

    /** nullable persistent field */
    private BigDecimal fileSize;

    /** nullable persistent field */
    private String fileHandle;

    /** persistent field */
    private String recordStatus;

    /** persistent field */
    private net.project.hibernate.model.PnDocRepositoryBase pnDocRepositoryBase;

    /** persistent field */
    private Set pnDocContentRenditions;

    /** persistent field */
    private Set pnDocVersionHasContents;

    /** full constructor */
    public PnDocContentElement(BigDecimal docContentId, BigDecimal docFormatId, Integer displaySequence, BigDecimal fileSize, String fileHandle, String recordStatus, net.project.hibernate.model.PnDocRepositoryBase pnDocRepositoryBase, Set pnDocContentRenditions, Set pnDocVersionHasContents) {
        this.docContentId = docContentId;
        this.docFormatId = docFormatId;
        this.displaySequence = displaySequence;
        this.fileSize = fileSize;
        this.fileHandle = fileHandle;
        this.recordStatus = recordStatus;
        this.pnDocRepositoryBase = pnDocRepositoryBase;
        this.pnDocContentRenditions = pnDocContentRenditions;
        this.pnDocVersionHasContents = pnDocVersionHasContents;
    }

    /** default constructor */
    public PnDocContentElement() {
    }

    /** minimal constructor */
    public PnDocContentElement(BigDecimal docContentId, String recordStatus, net.project.hibernate.model.PnDocRepositoryBase pnDocRepositoryBase, Set pnDocContentRenditions, Set pnDocVersionHasContents) {
        this.docContentId = docContentId;
        this.recordStatus = recordStatus;
        this.pnDocRepositoryBase = pnDocRepositoryBase;
        this.pnDocContentRenditions = pnDocContentRenditions;
        this.pnDocVersionHasContents = pnDocVersionHasContents;
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

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnDocRepositoryBase getPnDocRepositoryBase() {
        return this.pnDocRepositoryBase;
    }

    public void setPnDocRepositoryBase(net.project.hibernate.model.PnDocRepositoryBase pnDocRepositoryBase) {
        this.pnDocRepositoryBase = pnDocRepositoryBase;
    }

    public Set getPnDocContentRenditions() {
        return this.pnDocContentRenditions;
    }

    public void setPnDocContentRenditions(Set pnDocContentRenditions) {
        this.pnDocContentRenditions = pnDocContentRenditions;
    }

    public Set getPnDocVersionHasContents() {
        return this.pnDocVersionHasContents;
    }

    public void setPnDocVersionHasContents(Set pnDocVersionHasContents) {
        this.pnDocVersionHasContents = pnDocVersionHasContents;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docContentId", getDocContentId())
            .toString();
    }

}
