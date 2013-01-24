package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeHistoryClob implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** nullable persistent field */
    private Clob clobField;

    /** persistent field */
    private Set pnEnvelopeHistories;

    /** full constructor */
    public PnEnvelopeHistoryClob(BigDecimal objectId, Clob clobField, Set pnEnvelopeHistories) {
        this.objectId = objectId;
        this.clobField = clobField;
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    /** default constructor */
    public PnEnvelopeHistoryClob() {
    }

    /** minimal constructor */
    public PnEnvelopeHistoryClob(BigDecimal objectId, Set pnEnvelopeHistories) {
        this.objectId = objectId;
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public Clob getClobField() {
        return this.clobField;
    }

    public void setClobField(Clob clobField) {
        this.clobField = clobField;
    }

    public Set getPnEnvelopeHistories() {
        return this.pnEnvelopeHistories;
    }

    public void setPnEnvelopeHistories(Set pnEnvelopeHistories) {
        this.pnEnvelopeHistories = pnEnvelopeHistories;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
