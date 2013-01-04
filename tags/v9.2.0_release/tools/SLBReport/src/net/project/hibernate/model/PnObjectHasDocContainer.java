package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnObjectHasDocContainer implements Serializable {

    /** identifier field */
    private BigDecimal objectId;

    /** identifier field */
    private BigDecimal containerId;

    /** full constructor */
    public PnObjectHasDocContainer(BigDecimal objectId, BigDecimal containerId) {
        this.objectId = objectId;
        this.containerId = containerId;
    }

    /** default constructor */
    public PnObjectHasDocContainer() {
    }

    public BigDecimal getObjectId() {
        return this.objectId;
    }

    public void setObjectId(BigDecimal objectId) {
        this.objectId = objectId;
    }

    public BigDecimal getContainerId() {
        return this.containerId;
    }

    public void setContainerId(BigDecimal containerId) {
        this.containerId = containerId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectId", getObjectId())
            .append("containerId", getContainerId())
            .toString();
    }

}
