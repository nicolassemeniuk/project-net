package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnEnvelopeObjectClob implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** nullable persistent field */
    private Clob clobField;

    /** persistent field */
    private Set pnEnvelopeVersionHasObjects;

    /** full constructor */
    public PnEnvelopeObjectClob(BigDecimal objectId, Clob clobField, Set pnEnvelopeVersionHasObjects) {
        this.objectId = objectId;
        this.clobField = clobField;
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    /** default constructor */
    public PnEnvelopeObjectClob() {
    }

    /** minimal constructor */
    public PnEnvelopeObjectClob(BigDecimal objectId, Set pnEnvelopeVersionHasObjects) {
        this.objectId = objectId;
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
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

    public Set getPnEnvelopeVersionHasObjects() {
        return this.pnEnvelopeVersionHasObjects;
    }

    public void setPnEnvelopeVersionHasObjects(Set pnEnvelopeVersionHasObjects) {
        this.pnEnvelopeVersionHasObjects = pnEnvelopeVersionHasObjects;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
