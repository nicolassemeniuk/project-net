package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassField implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassFieldPK comp_id;

    /** nullable persistent field */
    private String dataColumnName;

    /** nullable persistent field */
    private String dataTableName;

    /** nullable persistent field */
    private String fieldLabel;

    /** nullable persistent field */
    private Integer dataColumnSize;

    /** persistent field */
    private int dataColumnExists;

    /** persistent field */
    private int rowNum;

    /** persistent field */
    private int rowSpan;

    /** persistent field */
    private int columnNum;

    /** nullable persistent field */
    private Integer columnSpan;

    /** nullable persistent field */
    private BigDecimal columnId;

    /** persistent field */
    private int useDefault;

    /** nullable persistent field */
    private String fieldGroup;

    /** persistent field */
    private int hasDomain;

    /** nullable persistent field */
    private String maxValue;

    /** nullable persistent field */
    private String minValue;

    /** nullable persistent field */
    private String defaultValue;

    /** persistent field */
    private int isMultiSelect;

    /** nullable persistent field */
    private BigDecimal sourceFieldId;

    /** nullable persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Integer dataColumnScale;

    /** nullable persistent field */
    private Clob instructionsClob;

    /** nullable persistent field */
    private Integer isValueRequired;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClass pnClass;

    /** persistent field */
    private net.project.hibernate.model.PnElement pnElement;

    /** persistent field */
    private net.project.hibernate.model.PnClassDomain pnClassDomain;

    /** full constructor */
    public PnClassField(net.project.hibernate.model.PnClassFieldPK comp_id, String dataColumnName, String dataTableName, String fieldLabel, Integer dataColumnSize, int dataColumnExists, int rowNum, int rowSpan, int columnNum, Integer columnSpan, BigDecimal columnId, int useDefault, String fieldGroup, int hasDomain, String maxValue, String minValue, String defaultValue, int isMultiSelect, BigDecimal sourceFieldId, Date crc, String recordStatus, Integer dataColumnScale, Clob instructionsClob, Integer isValueRequired, net.project.hibernate.model.PnObject pnObject, net.project.hibernate.model.PnClass pnClass, net.project.hibernate.model.PnElement pnElement, net.project.hibernate.model.PnClassDomain pnClassDomain) {
        this.comp_id = comp_id;
        this.dataColumnName = dataColumnName;
        this.dataTableName = dataTableName;
        this.fieldLabel = fieldLabel;
        this.dataColumnSize = dataColumnSize;
        this.dataColumnExists = dataColumnExists;
        this.rowNum = rowNum;
        this.rowSpan = rowSpan;
        this.columnNum = columnNum;
        this.columnSpan = columnSpan;
        this.columnId = columnId;
        this.useDefault = useDefault;
        this.fieldGroup = fieldGroup;
        this.hasDomain = hasDomain;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.defaultValue = defaultValue;
        this.isMultiSelect = isMultiSelect;
        this.sourceFieldId = sourceFieldId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.dataColumnScale = dataColumnScale;
        this.instructionsClob = instructionsClob;
        this.isValueRequired = isValueRequired;
        this.pnObject = pnObject;
        this.pnClass = pnClass;
        this.pnElement = pnElement;
        this.pnClassDomain = pnClassDomain;
    }

    /** default constructor */
    public PnClassField() {
    }

    /** minimal constructor */
    public PnClassField(net.project.hibernate.model.PnClassFieldPK comp_id, int dataColumnExists, int rowNum, int rowSpan, int columnNum, int useDefault, int hasDomain, int isMultiSelect, String recordStatus, net.project.hibernate.model.PnElement pnElement, net.project.hibernate.model.PnClassDomain pnClassDomain) {
        this.comp_id = comp_id;
        this.dataColumnExists = dataColumnExists;
        this.rowNum = rowNum;
        this.rowSpan = rowSpan;
        this.columnNum = columnNum;
        this.useDefault = useDefault;
        this.hasDomain = hasDomain;
        this.isMultiSelect = isMultiSelect;
        this.recordStatus = recordStatus;
        this.pnElement = pnElement;
        this.pnClassDomain = pnClassDomain;
    }

    public net.project.hibernate.model.PnClassFieldPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassFieldPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getDataColumnName() {
        return this.dataColumnName;
    }

    public void setDataColumnName(String dataColumnName) {
        this.dataColumnName = dataColumnName;
    }

    public String getDataTableName() {
        return this.dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    public String getFieldLabel() {
        return this.fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public Integer getDataColumnSize() {
        return this.dataColumnSize;
    }

    public void setDataColumnSize(Integer dataColumnSize) {
        this.dataColumnSize = dataColumnSize;
    }

    public int getDataColumnExists() {
        return this.dataColumnExists;
    }

    public void setDataColumnExists(int dataColumnExists) {
        this.dataColumnExists = dataColumnExists;
    }

    public int getRowNum() {
        return this.rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getRowSpan() {
        return this.rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColumnNum() {
        return this.columnNum;
    }

    public void setColumnNum(int columnNum) {
        this.columnNum = columnNum;
    }

    public Integer getColumnSpan() {
        return this.columnSpan;
    }

    public void setColumnSpan(Integer columnSpan) {
        this.columnSpan = columnSpan;
    }

    public BigDecimal getColumnId() {
        return this.columnId;
    }

    public void setColumnId(BigDecimal columnId) {
        this.columnId = columnId;
    }

    public int getUseDefault() {
        return this.useDefault;
    }

    public void setUseDefault(int useDefault) {
        this.useDefault = useDefault;
    }

    public String getFieldGroup() {
        return this.fieldGroup;
    }

    public void setFieldGroup(String fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    public int getHasDomain() {
        return this.hasDomain;
    }

    public void setHasDomain(int hasDomain) {
        this.hasDomain = hasDomain;
    }

    public String getMaxValue() {
        return this.maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return this.minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getIsMultiSelect() {
        return this.isMultiSelect;
    }

    public void setIsMultiSelect(int isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
    }

    public BigDecimal getSourceFieldId() {
        return this.sourceFieldId;
    }

    public void setSourceFieldId(BigDecimal sourceFieldId) {
        this.sourceFieldId = sourceFieldId;
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

    public Integer getDataColumnScale() {
        return this.dataColumnScale;
    }

    public void setDataColumnScale(Integer dataColumnScale) {
        this.dataColumnScale = dataColumnScale;
    }

    public Clob getInstructionsClob() {
        return this.instructionsClob;
    }

    public void setInstructionsClob(Clob instructionsClob) {
        this.instructionsClob = instructionsClob;
    }

    public Integer getIsValueRequired() {
        return this.isValueRequired;
    }

    public void setIsValueRequired(Integer isValueRequired) {
        this.isValueRequired = isValueRequired;
    }

    public net.project.hibernate.model.PnObject getPnObject() {
        return this.pnObject;
    }

    public void setPnObject(net.project.hibernate.model.PnObject pnObject) {
        this.pnObject = pnObject;
    }

    public net.project.hibernate.model.PnClass getPnClass() {
        return this.pnClass;
    }

    public void setPnClass(net.project.hibernate.model.PnClass pnClass) {
        this.pnClass = pnClass;
    }

    public net.project.hibernate.model.PnElement getPnElement() {
        return this.pnElement;
    }

    public void setPnElement(net.project.hibernate.model.PnElement pnElement) {
        this.pnElement = pnElement;
    }

    public net.project.hibernate.model.PnClassDomain getPnClassDomain() {
        return this.pnClassDomain;
    }

    public void setPnClassDomain(net.project.hibernate.model.PnClassDomain pnClassDomain) {
        this.pnClassDomain = pnClassDomain;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassField) ) return false;
        PnClassField castOther = (PnClassField) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
