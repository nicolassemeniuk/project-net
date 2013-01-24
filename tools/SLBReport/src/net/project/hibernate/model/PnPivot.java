package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPivot implements Serializable {

    /** identifier field */
    private BigDecimal x;

    /** full constructor */
    public PnPivot(BigDecimal x) {
        this.x = x;
    }

    /** default constructor */
    public PnPivot() {
    }

    public BigDecimal getX() {
        return this.x;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("x", getX())
            .toString();
    }

}
