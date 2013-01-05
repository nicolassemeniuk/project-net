package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnScheduleNtwkIdToReturn implements Serializable {

    /** identifier field */
    private BigDecimal id;

    /** full constructor */
    public PnScheduleNtwkIdToReturn(BigDecimal id) {
        this.id = id;
    }

    /** default constructor */
    public PnScheduleNtwkIdToReturn() {
    }

    public BigDecimal getId() {
        return this.id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .toString();
    }

}
