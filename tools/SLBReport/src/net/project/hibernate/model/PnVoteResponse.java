package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnVoteResponse implements Serializable {

    /** identifier field */
    private BigDecimal voteId;

    /** identifier field */
    private String response;

    /** full constructor */
    public PnVoteResponse(BigDecimal voteId, String response) {
        this.voteId = voteId;
        this.response = response;
    }

    /** default constructor */
    public PnVoteResponse() {
    }

    public BigDecimal getVoteId() {
        return this.voteId;
    }

    public void setVoteId(BigDecimal voteId) {
        this.voteId = voteId;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("voteId", getVoteId())
            .append("response", getResponse())
            .toString();
    }

}
