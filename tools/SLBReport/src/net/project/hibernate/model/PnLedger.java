package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLedger implements Serializable {

    /** identifier field */
    private BigDecimal ledgerId;

    /** persistent field */
    private BigDecimal responsiblePartyId;

    /** persistent field */
    private Date dueSinceDatetime;

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

    /** nullable persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private Date invoiceDate;

    /** persistent field */
    private BigDecimal invoiceStatusId;

    /** persistent field */
    private net.project.hibernate.model.PnInvoice pnInvoice;

    /** persistent field */
    private net.project.hibernate.model.PnBill pnBill;

    /** persistent field */
    private net.project.hibernate.model.PnPaymentInformation pnPaymentInformation;

    /** full constructor */
    public PnLedger(BigDecimal ledgerId, BigDecimal responsiblePartyId, Date dueSinceDatetime, BigDecimal unitPriceValue, BigDecimal quantityAmount, BigDecimal quantityUomId, BigDecimal categoryId, String partDetailsPartNumber, String partDetailsPartDescription, BigDecimal groupTypeId, String groupValue, String groupDescription, String recordStatus, Date invoiceDate, BigDecimal invoiceStatusId, net.project.hibernate.model.PnInvoice pnInvoice, net.project.hibernate.model.PnBill pnBill, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.ledgerId = ledgerId;
        this.responsiblePartyId = responsiblePartyId;
        this.dueSinceDatetime = dueSinceDatetime;
        this.unitPriceValue = unitPriceValue;
        this.quantityAmount = quantityAmount;
        this.quantityUomId = quantityUomId;
        this.categoryId = categoryId;
        this.partDetailsPartNumber = partDetailsPartNumber;
        this.partDetailsPartDescription = partDetailsPartDescription;
        this.groupTypeId = groupTypeId;
        this.groupValue = groupValue;
        this.groupDescription = groupDescription;
        this.recordStatus = recordStatus;
        this.invoiceDate = invoiceDate;
        this.invoiceStatusId = invoiceStatusId;
        this.pnInvoice = pnInvoice;
        this.pnBill = pnBill;
        this.pnPaymentInformation = pnPaymentInformation;
    }

    /** default constructor */
    public PnLedger() {
    }

    /** minimal constructor */
    public PnLedger(BigDecimal ledgerId, BigDecimal responsiblePartyId, Date dueSinceDatetime, BigDecimal unitPriceValue, BigDecimal quantityAmount, BigDecimal quantityUomId, BigDecimal categoryId, String partDetailsPartNumber, BigDecimal groupTypeId, BigDecimal invoiceStatusId, net.project.hibernate.model.PnInvoice pnInvoice, net.project.hibernate.model.PnBill pnBill, net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.ledgerId = ledgerId;
        this.responsiblePartyId = responsiblePartyId;
        this.dueSinceDatetime = dueSinceDatetime;
        this.unitPriceValue = unitPriceValue;
        this.quantityAmount = quantityAmount;
        this.quantityUomId = quantityUomId;
        this.categoryId = categoryId;
        this.partDetailsPartNumber = partDetailsPartNumber;
        this.groupTypeId = groupTypeId;
        this.invoiceStatusId = invoiceStatusId;
        this.pnInvoice = pnInvoice;
        this.pnBill = pnBill;
        this.pnPaymentInformation = pnPaymentInformation;
    }

    public BigDecimal getLedgerId() {
        return this.ledgerId;
    }

    public void setLedgerId(BigDecimal ledgerId) {
        this.ledgerId = ledgerId;
    }

    public BigDecimal getResponsiblePartyId() {
        return this.responsiblePartyId;
    }

    public void setResponsiblePartyId(BigDecimal responsiblePartyId) {
        this.responsiblePartyId = responsiblePartyId;
    }

    public Date getDueSinceDatetime() {
        return this.dueSinceDatetime;
    }

    public void setDueSinceDatetime(Date dueSinceDatetime) {
        this.dueSinceDatetime = dueSinceDatetime;
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

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Date getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getInvoiceStatusId() {
        return this.invoiceStatusId;
    }

    public void setInvoiceStatusId(BigDecimal invoiceStatusId) {
        this.invoiceStatusId = invoiceStatusId;
    }

    public net.project.hibernate.model.PnInvoice getPnInvoice() {
        return this.pnInvoice;
    }

    public void setPnInvoice(net.project.hibernate.model.PnInvoice pnInvoice) {
        this.pnInvoice = pnInvoice;
    }

    public net.project.hibernate.model.PnBill getPnBill() {
        return this.pnBill;
    }

    public void setPnBill(net.project.hibernate.model.PnBill pnBill) {
        this.pnBill = pnBill;
    }

    public net.project.hibernate.model.PnPaymentInformation getPnPaymentInformation() {
        return this.pnPaymentInformation;
    }

    public void setPnPaymentInformation(net.project.hibernate.model.PnPaymentInformation pnPaymentInformation) {
        this.pnPaymentInformation = pnPaymentInformation;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("ledgerId", getLedgerId())
            .toString();
    }

}
