package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectName implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** persistent field */
    private String name;

    /** full constructor */
    public PnObjectName(BigDecimal objectId, String name) {
        this.objectId = objectId;
        this.name = name;
    }

    /** default constructor */
    public PnObjectName() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .toString();
    }

}
