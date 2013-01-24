package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnBill implements Serializable {

    /** identifier field */
    private BigDecimal billId;

    /** persistent field */
    private BigDecimal unitPriceValue;

    /** persistent field */
    private BigDecimal quantityAmount;

    /** persistent field */
    private BigDecimal quantityUomId;

    /** persistent field */
    private BigDecimal categoryId;

    /** persistent field */
    private String partDetailsPartNumber;

    /** nullable persistent field */
    private String partDetailsPartDescription;

    /** persistent field */
    private BigDecimal groupTypeId;

    /** nullable persistent field */
    private String groupValue;

    /** nullable persistent field */
    private String groupDescription;

    /** persistent field */
    private BigDecimal billStatusId;

    /** nullable persistent field */
    private String recordStatus;

    /** persistent field */
    private Date creationDatetime;

    /** persistent field */
    private Date dueDatetime;

    /** persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** persistent field */
    private net.project.hibernate.model.PnLicense pnLicense;

    /** persistent field */
    private net.project.hibernate.model.PnPaymentInformation pnPaymentInformation;

    /** persistent field */
    private Set pnLedgers;

    /** full constructor */
    public PnBill(BigDecimal billId, BigDecimal unitPriceValue, BigDecimal quantityAmount, BigDecimal quantityUomId, BigDecimal categoryId, String partDetailsPartNumber, String partDetailsPartDescription, BigDecimal groupTypeId, String groupValue, String groupDescription, BigDecimal billStatusId, String recordStatus, Date creationDatetime, Date dueDatetime, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnLicense pnLicense, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation, Set pnLedgers) {
        this.billId = billId;
        this.unitPriceValue = unitPriceValue;
        this.quantityAmount = quantityAmount;
        this.quantityUomId = quantityUomId;
        this.categoryId = categoryId;
        this.partDetailsPartNumber = partDetailsPartNumber;
        this.partDetailsPartDescription = partDetailsPartDescription;
        this.groupTypeId = groupTypeId;
        this.groupValue = groupValue;
        this.groupDescription = groupDescription;
        this.billStatusId = billStatusId;
        this.recordStatus = recordStatus;
        this.creationDatetime = creationDatetime;
        this.dueDatetime = dueDatetime;
        this.pnPerson = pnPerson;
        this.pnLicense = pnLicense;
        this.pnPaymentInformation = pnPaymentInformation;
        this.pnLedgers = pnLedgers;
    }

    /** default constructor */
    public PnBill() {
    }

    /** minimal constructor */
    public PnBill(BigDecimal billId, BigDecimal unitPriceValue, BigDecimal quantityAmount, BigDecimal quantityUomId, BigDecimal categoryId, String partDetailsPartNumber, BigDecimal groupTypeId, BigDecimal billStatusId, Date creationDatetime, Date dueDatetime, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnLicense pnLicense, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation, Set pnLedgers) {
        this.billId = billId;
        this.unitPriceValue = unitPriceValue;
        this.quantityAmount = quantityAmount;
        this.quantityUomId = quantityUomId;
        this.categoryId = categoryId;
        this.partDetailsPartNumber = partDetailsPartNumber;
        this.groupTypeId = groupTypeId;
        this.billStatusId = billStatusId;
        this.creationDatetime = creationDatetime;
        this.dueDatetime = dueDatetime;
        this.pnPerson = pnPerson;
        this.pnLicense = pnLicense;
        this.pnPaymentInformation = pnPaymentInformation;
        this.pnLedgers = pnLedgers;
    }

    public BigDecimal getBillId() {
        return this.billId;
    }

    public void setBillId(BigDecimal billId) {
        this.billId = billId;
    }

    public BigDecimal getUnitPriceValue() {
        return this.unitPriceValue;
    }

    public void setUnitPriceValue(BigDecimal unitPriceValue) {
        this.unitPriceValue = unitPriceValue;
    }

    public BigDecimal getQuantityAmount() {
        return this.quantityAmount;
    }

    public void setQuantityAmount(BigDecimal quantityAmount) {
        this.quantityAmount = quantityAmount;
    }

    public BigDecimal getQuantityUomId() {
        return this.quantityUomId;
    }

    public void setQuantityUomId(BigDecimal quantityUomId) {
        this.quantityUomId = quantityUomId;
    }

    public BigDecimal getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(BigDecimal categoryId) {
        this.categoryId = categoryId;
    }

    public String getPartDetailsPartNumber() {
        return this.partDetailsPartNumber;
    }

    public void setPartDetailsPartNumber(String partDetailsPartNumber) {
        this.partDetailsPartNumber = partDetailsPartNumber;
    }

    public String getPartDetailsPartDescription() {
        return this.partDetailsPartDescription;
    }

    public void setPartDetailsPartDescription(String partDetailsPartDescription) {
        this.partDetailsPartDescription = partDetailsPartDescription;
    }

    public BigDecimal getGroupTypeId() {
        return this.groupTypeId;
    }

    public void setGroupTypeId(BigDecimal groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    public String getGroupValue() {
        return this.groupValue;
    }

    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public BigDecimal getBillStatusId() {
        return this.billStatusId;
    }

    public void setBillStatusId(BigDecimal billStatusId) {
        this.billStatusId = billStatusId;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getCreationDatetime() {
        return this.creationDatetime;
    }

    public void setCreationDatetime(Date creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Date getDueDatetime() {
        return this.dueDatetime;
    }

    public void setDueDatetime(Date dueDatetime) {
        this.dueDatetime = dueDatetime;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnLicense getPnLicense() {
        return this.pnLicense;
    }

    public void setPnLicense(net.project.hibernate.model.PnLicense pnLicense) {
        this.pnLicense = pnLicense;
    }

    public net.project.hibernate.model.PnPaymentInformation getPnPaymentInformation() {
        return this.pnPaymentInformation;
    }

    public void setPnPaymentInformation(net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.pnPaymentInformation = pnPaymentInformation;
    }

    public Set getPnLedgers() {
        return this.pnLedgers;
    }

    public void setPnLedgers(Set pnLedgers) {
        this.pnLedgers = pnLedgers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("billId", getBillId())
            .toString();
    }

}
