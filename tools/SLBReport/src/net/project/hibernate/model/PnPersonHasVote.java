package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasVote implements Serializable {

    /** identifier field */
    private BigDecimal personId;

    /** identifier field */
    private BigDecimal voteId;

    /** full constructor */
    public PnPersonHasVote(BigDecimal personId, BigDecimal voteId) {
        this.personId = personId;
        this.voteId = voteId;
    }

    /** default constructor */
    public PnPersonHasVote() {
    }

    public BigDecimal getPersonId() {
        return this.personId;
    }

    public void setPersonId(BigDecimal personId) {
        this.personId = personId;
    }

    public BigDecimal getVoteId() {
        return this.voteId;
    }

    public void setVoteId(BigDecimal voteId) {
        this.voteId = voteId;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("personId", getPersonId())
            .append("voteId", getVoteId())
            .toString();
    }

}
