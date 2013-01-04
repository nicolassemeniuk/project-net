package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLicenseCertificate implements Serializable {

    /** identifier field */
    private BigDecimal certificateId;

    /** persistent field */
    private Set pnLicenses;

    /** full constructor */
    public PnLicenseCertificate(BigDecimal certificateId, Set pnLicenses) {
        this.certificateId = certificateId;
        this.pnLicenses = pnLicenses;
    }

    /** default constructor */
    public PnLicenseCertificate() {
    }

    public BigDecimal getCertificateId() {
        return this.certificateId;
    }

    public void setCertificateId(BigDecimal certificateId) {
        this.certificateId = certificateId;
    }

    public Set getPnLicenses() {
        return this.pnLicenses;
    }

    public void setPnLicenses(Set pnLicenses) {
        this.pnLicenses = pnLicenses;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("certificateId", getCertificateId())
            .toString();
    }

}
