package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnInvoiceLob implements Serializable {

    /** identifier field */
    private BigDecimal invoiceId;

    /** nullable persistent field */
    private Clob invoiceLobData;

    /** full constructor */
    public PnInvoiceLob(BigDecimal invoiceId, Clob invoiceLobData) {
        this.invoiceId = invoiceId;
        this.invoiceLobData = invoiceLobData;
    }

    /** default constructor */
    public PnInvoiceLob() {
    }

    /** minimal constructor */
    public PnInvoiceLob(BigDecimal invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(BigDecimal invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Clob getInvoiceLobData() {
        return this.invoiceLobData;
    }

    public void setInvoiceLobData(Clob invoiceLobData) {
        this.invoiceLobData = invoiceLobData;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("invoiceId", getInvoiceId())
            .toString();
    }

}
