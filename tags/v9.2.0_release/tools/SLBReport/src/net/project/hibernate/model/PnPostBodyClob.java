package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPostBodyClob implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** nullable persistent field */
    private Clob clobField;

    /** persistent field */
    private Set pnPosts;

    /** full constructor */
    public PnPostBodyClob(BigDecimal objectId, Clob clobField, Set pnPosts) {
        this.objectId = objectId;
        this.clobField = clobField;
        this.pnPosts = pnPosts;
    }

    /** default constructor */
    public PnPostBodyClob() {
    }

    /** minimal constructor */
    public PnPostBodyClob(BigDecimal objectId, Set pnPosts) {
        this.objectId = objectId;
        this.pnPosts = pnPosts;
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

    public Set getPnPosts() {
        return this.pnPosts;
    }

    public void setPnPosts(Set pnPosts) {
        this.pnPosts = pnPosts;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
