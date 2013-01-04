package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClass implements Serializable {

    /** identifier field */
    private BigDecimal classId;

    /** nullable persistent field */
    private String className;

    /** nullable persistent field */
    private String classDesc;

    /** nullable persistent field */
    private String classAbbreviation;

    /** nullable persistent field */
    private BigDecimal ownerSpaceId;

    /** nullable persistent field */
    private BigDecimal methodologyId;

    /** persistent field */
    private int maxRow;

    /** persistent field */
    private int maxColumn;

    /** persistent field */
    private int nextDataSeq;

    /** persistent field */
    private int dataTableSeq;

    /** nullable persistent field */
    private String masterTableName;

    /** nullable persistent field */
    private String dataTableKey;

    /** persistent field */
    private int isSequenced;

    /** persistent field */
    private int isSystemClass;

    /** nullable persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Integer supportsDiscussionGroup;

    /** nullable persistent field */
    private Integer supportsDocumentVault;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private net.project.hibernate.model.PnClassType pnClassType;

    /** persistent field */
    private Set pnSpaceHasClasses;

    /** persistent field */
    private Set pnClassFields;

    /** persistent field */
    private Set pnClassLists;

    /** persistent field */
    private Set pnElementDisplayClasses;

    /** full constructor */
    public PnClass(BigDecimal classId, String className, String classDesc, String classAbbreviation, BigDecimal ownerSpaceId, BigDecimal methodologyId, int maxRow, int maxColumn, int nextDataSeq, int dataTableSeq, String masterTableName, String dataTableKey, int isSequenced, int isSystemClass, Date crc, String recordStatus, Integer supportsDiscussionGroup, Integer supportsDocumentVault, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnClassType pnClassType, Set pnSpaceHasClasses, Set pnClassFields, Set pnClassLists, Set pnElementDisplayClasses) {
        this.classId = classId;
        this.className = className;
        this.classDesc = classDesc;
        this.classAbbreviation = classAbbreviation;
        this.ownerSpaceId = ownerSpaceId;
        this.methodologyId = methodologyId;
        this.maxRow = maxRow;
        this.maxColumn = maxColumn;
        this.nextDataSeq = nextDataSeq;
        this.dataTableSeq = dataTableSeq;
        this.masterTableName = masterTableName;
        this.dataTableKey = dataTableKey;
        this.isSequenced = isSequenced;
        this.isSystemClass = isSystemClass;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.supportsDiscussionGroup = supportsDiscussionGroup;
        this.supportsDocumentVault = supportsDocumentVault;
        this.pnObject = pnObject;
        this.pnClassType = pnClassType;
        this.pnSpaceHasClasses = pnSpaceHasClasses;
        this.pnClassFields = pnClassFields;
        this.pnClassLists = pnClassLists;
        this.pnElementDisplayClasses = pnElementDisplayClasses;
    }

    /** default constructor */
    public PnClass() {
    }

    /** minimal constructor */
    public PnClass(BigDecimal classId, int maxRow, int maxColumn, int nextDataSeq, int dataTableSeq, int isSequenced, int isSystemClass, String recordStatus, net.project.hibernate.model.PnClassType pnClassType, Set pnSpaceHasClasses, Set pnClassFields, Set pnClassLists, Set pnElementDisplayClasses) {
        this.classId = classId;
        this.maxRow = maxRow;
        this.maxColumn = maxColumn;
        this.nextDataSeq = nextDataSeq;
        this.dataTableSeq = dataTableSeq;
        this.isSequenced = isSequenced;
        this.isSystemClass = isSystemClass;
        this.recordStatus = recordStatus;
        this.pnClassType = pnClassType;
        this.pnSpaceHasClasses = pnSpaceHasClasses;
        this.pnClassFields = pnClassFields;
        this.pnClassLists = pnClassLists;
        this.pnElementDisplayClasses = pnElementDisplayClasses;
    }

    public BigDecimal getClassId() {
        return this.classId;
    }

    public void setClassId(BigDecimal classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDesc() {
        return this.classDesc;
    }

    public void setClassDesc(String classDesc) {
        this.classDesc = classDesc;
    }

    public String getClassAbbreviation() {
        return this.classAbbreviation;
    }

    public void setClassAbbreviation(String classAbbreviation) {
        this.classAbbreviation = classAbbreviation;
    }

    public BigDecimal getOwnerSpaceId() {
        return this.ownerSpaceId;
    }

    public void setOwnerSpaceId(BigDecimal ownerSpaceId) {
        this.ownerSpaceId = ownerSpaceId;
    }

    public BigDecimal getMethodologyId() {
        return this.methodologyId;
    }

    public void setMethodologyId(BigDecimal methodologyId) {
        this.methodologyId = methodologyId;
    }

    public int getMaxRow() {
        return this.maxRow;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public int getMaxColumn() {
        return this.maxColumn;
    }

    public void setMaxColumn(int maxColumn) {
        this.maxColumn = maxColumn;
    }

    public int getNextDataSeq() {
        return this.nextDataSeq;
    }

    public void setNextDataSeq(int nextDataSeq) {
        this.nextDataSeq = nextDataSeq;
    }

    public int getDataTableSeq() {
        return this.dataTableSeq;
    }

    public void setDataTableSeq(int dataTableSeq) {
        this.dataTableSeq = dataTableSeq;
    }

    public String getMasterTableName() {
        return this.masterTableName;
    }

    public void setMasterTableName(String masterTableName) {
        this.masterTableName = masterTableName;
    }

    public String getDataTableKey() {
        return this.dataTableKey;
    }

    public void setDataTableKey(String dataTableKey) {
        this.dataTableKey = dataTableKey;
    }

    public int getIsSequenced() {
        return this.isSequenced;
    }

    public void setIsSequenced(int isSequenced) {
        this.isSequenced = isSequenced;
    }

    public int getIsSystemClass() {
        return this.isSystemClass;
    }

    public void setIsSystemClass(int isSystemClass) {
        this.isSystemClass = isSystemClass;
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

    public Integer getSupportsDiscussionGroup() {
        return this.supportsDiscussionGroup;
    }

    public void setSupportsDiscussionGroup(Integer supportsDiscussionGroup) {
        this.supportsDiscussionGroup = supportsDiscussionGroup;
    }

    public Integer getSupportsDocumentVault() {
        return this.supportsDocumentVault;
    }

    public void setSupportsDocumentVault(Integer supportsDocumentVault) {
        this.supportsDocumentVault = supportsDocumentVault;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnClassType getPnClassType() {
        return this.pnClassType;
    }

    public void setPnClassType(net.project.hibernate.model.PnClassType pnClassType) {
        this.pnClassType = pnClassType;
    }

    public Set getPnSpaceHasClasses() {
        return this.pnSpaceHasClasses;
    }

    public void setPnSpaceHasClasses(Set pnSpaceHasClasses) {
        this.pnSpaceHasClasses = pnSpaceHasClasses;
    }

    public Set getPnClassFields() {
        return this.pnClassFields;
    }

    public void setPnClassFields(Set pnClassFields) {
        this.pnClassFields = pnClassFields;
    }

    public Set getPnClassLists() {
        return this.pnClassLists;
    }

    public void setPnClassLists(Set pnClassLists) {
        this.pnClassLists = pnClassLists;
    }

    public Set getPnElementDisplayClasses() {
        return this.pnElementDisplayClasses;
    }

    public void setPnElementDisplayClasses(Set pnElementDisplayClasses) {
        this.pnElementDisplayClasses = pnElementDisplayClasses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("classId", getClassId())
            .toString();
    }

}
