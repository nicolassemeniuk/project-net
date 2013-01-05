package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnProfCertLookup implements Serializable {

    /** identifier field */
    private Integer profCertCode;

    /** persistent field */
    private String profCertName;

    /** nullable persistent field */
    private String profCertDescription;

    /** persistent field */
    private Set pnPersonHasProfCerts;

    /** full constructor */
    public PnProfCertLookup(Integer profCertCode, String profCertName, String profCertDescription, Set pnPersonHasProfCerts) {
        this.profCertCode = profCertCode;
        this.profCertName = profCertName;
        this.profCertDescription = profCertDescription;
        this.pnPersonHasProfCerts = pnPersonHasProfCerts;
    }

    /** default constructor */
    public PnProfCertLookup() {
    }

    /** minimal constructor */
    public PnProfCertLookup(Integer profCertCode, String profCertName, Set pnPersonHasProfCerts) {
        this.profCertCode = profCertCode;
        this.profCertName = profCertName;
        this.pnPersonHasProfCerts = pnPersonHasProfCerts;
    }

    public Integer getProfCertCode() {
        return this.profCertCode;
    }

    public void setProfCertCode(Integer profCertCode) {
        this.profCertCode = profCertCode;
    }

    public String getProfCertName() {
        return this.profCertName;
    }

    public void setProfCertName(String profCertName) {
        this.profCertName = profCertName;
    }

    public String getProfCertDescription() {
        return this.profCertDescription;
    }

    public void setProfCertDescription(String profCertDescription) {
        this.profCertDescription = profCertDescription;
    }

    public Set getPnPersonHasProfCerts() {
        return this.pnPersonHasProfCerts;
    }

    public void setPnPersonHasProfCerts(Set pnPersonHasProfCerts) {
        this.pnPersonHasProfCerts = pnPersonHasProfCerts;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("profCertCode", getProfCertCode())
            .toString();
    }

}
