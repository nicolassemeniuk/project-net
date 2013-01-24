package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocSpace implements Serializable {

    /** identifier field */
    private BigDecimal docSpaceId;

    /** nullable persistent field */
    private String docSpaceName;

    /** persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObject pnObject;

    /** persistent field */
    private Set pnDocProviderHasDocSpaces;

    /** persistent field */
    private Set pnSpaceHasDocSpaces;

    /** persistent field */
    private Set pnDocSpaceHasContainers;

    /** full constructor */
    public PnDocSpace(BigDecimal docSpaceId, String docSpaceName, Date crc, String recordStatus, net.project.hibernate.model.PnObject pnObject, Set pnDocProviderHasDocSpaces, Set pnSpaceHasDocSpaces, Set pnDocSpaceHasContainers) {
        this.docSpaceId = docSpaceId;
        this.docSpaceName = docSpaceName;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnObject = pnObject;
        this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
        this.pnSpaceHasDocSpaces = pnSpaceHasDocSpaces;
        this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
    }

    /** default constructor */
    public PnDocSpace() {
    }

    public PnDocSpace(BigDecimal docSpaceId, String docSpaceName, Date crc, String recordStatus) {
        this.docSpaceId = docSpaceId;
        this.docSpaceName = docSpaceName;
        this.crc = crc;
        this.recordStatus = recordStatus;
    }
    
    /** minimal constructor */
    public PnDocSpace(BigDecimal docSpaceId, Date crc, String recordStatus, Set pnDocProviderHasDocSpaces, Set pnSpaceHasDocSpaces, Set pnDocSpaceHasContainers) {
        this.docSpaceId = docSpaceId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
        this.pnSpaceHasDocSpaces = pnSpaceHasDocSpaces;
        this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
    }

    public BigDecimal getDocSpaceId() {
        return this.docSpaceId;
    }

    public void setDocSpaceId(BigDecimal docSpaceId) {
        this.docSpaceId = docSpaceId;
    }

    public String getDocSpaceName() {
        return this.docSpaceName;
    }

    public void setDocSpaceName(String docSpaceName) {
        this.docSpaceName = docSpaceName;
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

    public Set getPnDocProviderHasDocSpaces() {
        return this.pnDocProviderHasDocSpaces;
    }

    public void setPnDocProviderHasDocSpaces(Set pnDocProviderHasDocSpaces) {
        this.pnDocProviderHasDocSpaces = pnDocProviderHasDocSpaces;
    }

    public Set getPnSpaceHasDocSpaces() {
        return this.pnSpaceHasDocSpaces;
    }

    public void setPnSpaceHasDocSpaces(Set pnSpaceHasDocSpaces) {
        this.pnSpaceHasDocSpaces = pnSpaceHasDocSpaces;
    }

    public Set getPnDocSpaceHasContainers() {
        return this.pnDocSpaceHasContainers;
    }

    public void setPnDocSpaceHasContainers(Set pnDocSpaceHasContainers) {
        this.pnDocSpaceHasContainers = pnDocSpaceHasContainers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("docSpaceId", getDocSpaceId())
            .toString();
    }

}
