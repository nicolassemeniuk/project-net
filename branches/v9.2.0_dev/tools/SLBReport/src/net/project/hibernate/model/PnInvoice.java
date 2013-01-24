package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnInvoice implements Serializable {

    /** identifier field */
    private BigDecimal invoiceId;

    /** persistent field */
    private Date creationDatetime;

    /** persistent field */
    private Set pnLedgers;

    /** full constructor */
    public PnInvoice(BigDecimal invoiceId, Date creationDatetime, Set pnLedgers) {
        this.invoiceId = invoiceId;
        this.creationDatetime = creationDatetime;
        this.pnLedgers = pnLedgers;
    }

    /** default constructor */
    public PnInvoice() {
    }

    public BigDecimal getInvoiceId() {
        return this.invoiceId;
    }

    public void setInvoiceId(BigDecimal invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Date getCreationDatetime() {
        return this.creationDatetime;
    }

    public void setCreationDatetime(Date creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Set getPnLedgers() {
        return this.pnLedgers;
    }

    public void setPnLedgers(Set pnLedgers) {
        this.pnLedgers = pnLedgers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("invoiceId", getInvoiceId())
            .toString();
    }

}
